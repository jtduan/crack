package cn.jtduan.crack;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.lody.whale.xposed.XC_MethodHook;
import com.lody.whale.xposed.XposedHelpers;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        System.out.println("====" + context.getClass().getName());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
            }
        }

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
    }

    public void func1(View view) {
        TextView tv = findViewById(R.id.sample_text);
//        tv.setText("res=" + NativeAPI.testLong(10, 1578029614978L));
        tv.setText("res=" + NativeAPI.testSyscall() + "\n" + NativeAPI.stringFromJNI());
    }

    /**
     * 读取设备信息
     *
     * @param view
     */
    public void func2(View view) {
        TextView tv = findViewById(R.id.sample_text);
        DeviceService deviceService = new DeviceService(getApplicationContext());
        String location = deviceService.getLocation();
        String res = "location=" + location + "\n"
                + "androidid=" + deviceService.getAndroidId() + "\n"
                + "hareware=" + deviceService.getHardware() + "\n"
                + "imei=" + deviceService.getIMEI() + "\n"
                + "serial=" + deviceService.getSerial() + "\n"
                + "mac=" + deviceService.getMacAddress() + "\n";
        tv.setText(res);
    }
}
