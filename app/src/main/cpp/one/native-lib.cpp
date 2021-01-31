#include <jni.h>
#include "android/log.h"
#include "../../include/three.h"
#include "../../include/whale.h"
#include "../../include/sandhook_native.h"
#include <stdio.h>
#include <string>
#include <dlfcn.h>

#ifndef LOG_TAG
#define LOG_TAG "JNI_LOG" //Log 的 tag 名字
//定义各种类型 Log 的函数别名
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG ,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG ,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,LOG_TAG ,__VA_ARGS__)
#endif

#ifdef __cplusplus
extern "C" {
#endif
//定义类名
static const char *className = "cn/jtduan/crack/NativeAPI";
static char key[10] = "123456789";

jlong (*Origin_testSyscall)(JNIEnv *env, jobject);

jlong Hooked_testSyscall(JNIEnv *env, jobject obj) {
    jlong (*O)(JNIEnv *, jobject) = Origin_testSyscall;
    std::string tag = "ndk third called";
    char *msg = "hook jni";
    __android_log_print(ANDROID_LOG_DEBUG, tag.c_str(), "%d%s\n", sum(3, 6), msg);
    return O(env, obj);
}

int (*Origin_func)(const char *__name, char *__value);

int Hooked_func(const char *__name, char *__value) {
    int (*O)(const char *, char *) = Origin_func;
    std::string tag = "ndk third called";
    if (!strcmp(__name, "ro.build.id")) {
        __android_log_print(ANDROID_LOG_DEBUG, tag.c_str(), "%d%s\n", sum(4, 6), __name);
        strcpy(__value, "hooked id");
        return 9;
    }
    return O(__name, __value);
}

//定义对应Java native方法的 C++ 函数，函数名可以随意命名
static void basic1(JNIEnv *env, jobject) {
    char arr[10];
    for (int i = 0; i < 10; i++) {
        arr[i] = (char) ('g' + i);
    }
}

//定义对应Java native方法的 C++ 函数，函数名可以随意命名
static jlong testLong(JNIEnv *env, jobject, jint param1, jlong param2) {
    param2 += param1;
    return param2;
}

//获取系统时间
static jlong testSyscall(JNIEnv *env, jobject) {
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return 1000 * (tv.tv_sec * 1000 + tv.tv_usec / 1000);
}

//定义对应Java native方法的 C++ 函数，函数名可以随意命名
static jstring propertityGet(JNIEnv *env, jobject) {
    char buf[100];
    __system_property_get("ro.build.id", buf);
    return env->NewStringUTF(buf);
}

//定义对应Java native方法的 C++ 函数，函数名可以随意命名
static void updateKey(JNIEnv *env, jobject, jstring str_) {
    const char *str = env->GetStringUTFChars(str_, 0);
    for (int i = 0; i < strlen(str); i++) {
        key[i] = (char) ('a' + i);
    }
}

//初始化方法
static jstring callJava(JNIEnv *env, jobject, jstring str_) {
    //1.通过反射获取 class 实体类
    jclass jclazz = env->FindClass("cn/jtduan/crack/NativeAPI");  //注意 FindClass 不要 L和;
    if (jclazz == NULL) {
        char c[10] = "error";
        return env->NewStringUTF(c);
    }
    //通过 class 找到对应的方法 id
    jmethodID mid = env->GetStaticMethodID(jclazz, "strFromJava",
                                           "(Ljava/lang/String;)Ljava/lang/String;");
//    char ch[10] = "cfanr";
    const char *str = env->GetStringUTFChars(str_, 0);
    jstring jstr = env->NewStringUTF(str);
    return (jstring) env->CallStaticObjectMethod(jclazz, mid, jstr);
}

/*
 * 定义函数映射表（是一个数组，可以同时定义多个函数的映射）
 * 参数1：Java 方法名
 * 参数2：方法描述符，也就是签名
 * 参数3：C++定义对应 Java native方法的函数名，注意是空指针类型
 */
static JNINativeMethod jni_Methods_table[] = {
        {"propertityGet", "()Ljava/lang/String;",                   (void *) propertityGet},
        {"callJava",      "(Ljava/lang/String;)Ljava/lang/String;", (void *) callJava},
        {"updateKey",     "(Ljava/lang/String;)V",                  (void *) updateKey},
        {"basic1",        "()V",                                    (void *) basic1},
        {"testLong",      "(IJ)J",                                  (void *) testLong},
        {"testSyscall",   "()J",                                    (void *) testSyscall},
};

//根据函数映射表注册函数
static int registerNativeMethods(JNIEnv *env, const char *className,
                                 const JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    LOGI("Registering %s natives\n", className);
    clazz = (env)->FindClass(className);
    if (clazz == NULL) {
        LOGE("Native registration unable to find class '%s'\n", className);
        return JNI_ERR;
    }

    if ((env)->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        LOGE("Register natives failed for '%s'\n", className);
        return JNI_ERR;
    }
    //删除本地引用
    (env)->DeleteLocalRef(clazz);
    return JNI_OK;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGI("call JNI_OnLoad");

    JNIEnv *env = NULL;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {  //判断 JNI 版本是否为JNI_VERSION_1_4
        return JNI_EVERSION;
    }

    registerNativeMethods(env, className, jni_Methods_table,
                          sizeof(jni_Methods_table) / sizeof(JNINativeMethod));

    std::string tag = "ndk third called";
    char *msg = "日志打印";
    __android_log_print(ANDROID_LOG_DEBUG, tag.c_str(), "%d%s\n", sum(1, 6), msg);


    void *symbol = (void *) testSyscall;
//    WInlineHookFunction(
//            symbol,
//            reinterpret_cast<void *>(Hooked_testSyscall),
//            reinterpret_cast<void **>(&Origin_testSyscall)
//    );

//    Origin_testSyscall = reinterpret_cast<jlong (*)(JNIEnv *, jobject)>((jlong *) SandInlineHook(
//            symbol, (void *) Hooked_testSyscall));

//whale hook __system_property_get fail
//    void *symbol2 = (void *) __system_property_get;
//    WInlineHookFunction(
//            symbol2,
//            reinterpret_cast<void *>(Hooked_func),
//            reinterpret_cast<void **>(&Origin_func)
//    );

    Origin_func = reinterpret_cast<int (*)(const char *, char *)>(SandInlineHook(
            (void *) __system_property_get, (void *) Hooked_func));

    return JNI_VERSION_1_4;
}
#ifdef __cplusplus
}
#endif