package cn.jtduan.crack.sandhook;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.swift.sandhook.xposedcompat.XposedCompat;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static com.swift.sandhook.xposedcompat.XposedCompat.getCacheDir;

public class SandHookDemo {
    public static void hookJava(Context context) {
        XposedCompat.cacheDir = getCacheDir();

        XposedCompat.context = context;
        XposedCompat.classLoader = context.getClassLoader();
        XposedCompat.isFirstApplication = true;

        XposedHelpers.findAndHookMethod("cn.jtduan.crack.NativeAPI", context.getClassLoader(), "testSyscall", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.e("AAA", "SandHook: testSysCall");
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.e("AAA", "SandHook: testSysCall res=" + param.getResult());
                super.afterHookedMethod(param);
            }
        });
    }
}
