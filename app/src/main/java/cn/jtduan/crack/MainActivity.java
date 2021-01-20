package cn.jtduan.crack;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lody.whale.xposed.XC_MethodHook;
import com.lody.whale.xposed.XposedHelpers;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        System.out.println("====" + context.getClass().getName());

        XposedHelpers.findAndHookMethod("cn.jtduan.crack.NativeAPI", this.getClassLoader(), "testSyscall", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.e("AAA", "testSysCall");
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.e("AAA", "testSysCall res=" + param.getResult());
                super.afterHookedMethod(param);
            }
        });

        TextView tv = findViewById(R.id.sample_text);
        NativeAPI.updateKey("345");
//        tv.setText("res=" + new NativeAPI().callJava("123"));
        tv.setText("res=" + NativeAPI.stringFromJNI());
//        tv.setText("res=" + NativeAPI.testLong(1578029614978L));
    }

    public void func1(View view) {
        TextView tv = findViewById(R.id.sample_text);
//        tv.setText("res=" + NativeAPI.testLong(10, 1578029614978L));
        tv.setText("res=" + NativeAPI.testSyscall());
    }
}
