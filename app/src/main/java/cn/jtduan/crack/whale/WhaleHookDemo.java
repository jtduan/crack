package cn.jtduan.crack.whale;

import android.content.Context;
import android.util.Log;

import com.lody.whale.xposed.XC_MethodHook;
import com.lody.whale.xposed.XposedHelpers;

public class WhaleHookDemo {
    public static void hookJava(Context context) {
        XposedHelpers.findAndHookMethod("cn.jtduan.crack.NativeAPI", context.getClassLoader(), "testSyscall", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.e("AAA", "Whale: testSysCall");
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.e("AAA", "Whale: testSysCall res=" + param.getResult());
                super.afterHookedMethod(param);
            }
        });
    }
}
