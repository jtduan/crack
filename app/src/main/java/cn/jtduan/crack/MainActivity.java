package cn.jtduan.crack;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

import cn.jtduan.crack.sandhook.SandHookDemo;
import dalvik.system.DexClassLoader;

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
//        WhaleHookDemo.hookJava(this);
//        SandHookDemo.hookJava(this);
    }

    /**
     * 系统调用
     *
     * @param view
     */
    public void func1(View view) {
        TextView tv = findViewById(R.id.sample_text);
//        tv.setText("res=" + NativeAPI.testLong(10, 1578029614978L));
        tv.setText("res=" + NativeAPI.testSyscall());
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


    /**
     * jni设备信息
     *
     * @param view
     */
    public void func3(View view) {
        TextView tv = findViewById(R.id.sample_text);
        tv.setText("res=" + NativeAPI.propertityGet());
    }

    /**
     * 加载外部DEX
     *
     * @param view
     */
    public void func4(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }

        TextView tv = findViewById(R.id.sample_text);
        File dexOutputDir = this.getApplicationContext().getDir("dex", 0);
        File libDir = this.getApplicationContext().getDir("lib", 0);

        String dexPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/plugin/plugin.dex";
        String soPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/plugin/libnativeplugin.so";
        if (!new File(dexPath).exists()) {
            tv.setText("res=" + "no plugin!");
            return;
        }
        DexClassLoader dexClassLoader;
        if (new File(soPath).exists()) {
            copyFile(soPath, libDir.getAbsolutePath() + "/libnativeplugin.so");
            dexClassLoader = new DexClassLoader(dexPath, dexOutputDir.getAbsolutePath(), libDir.getAbsolutePath(), getClassLoader());
        } else {
            dexClassLoader = new DexClassLoader(dexPath, dexOutputDir.getAbsolutePath(), null, getClassLoader());
        }

        Class libProviderClazz = null;
        try {
            libProviderClazz = dexClassLoader.loadClass("cn.jtduan.plugin.Main");
            Method[] methods = libProviderClazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Log.e("AAA", methods[i].toString());
            }
            Method start = libProviderClazz.getDeclaredMethod("hookJava", Context.class);// 获取方法
            start.setAccessible(true);// 把方法设为public，让外部可以调用
            start.invoke(libProviderClazz.newInstance(), this.getApplicationContext());// 调用方法并获取返回值

            if (new File(soPath).exists()) {
                Method start2 = libProviderClazz.getDeclaredMethod("hookC");// 获取方法
                start2.setAccessible(true);// 把方法设为public，让外部可以调用
                start2.invoke(libProviderClazz.newInstance());// 调用方法并获取返回值
            }

            tv.setText("res=" + "ok");
        } catch (Exception exception) {
            exception.printStackTrace();
            tv.setText("res=" + "error");
        }
    }

    public boolean copyFile(String oldPath$Name, String newPath$Name) {
        try {
            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists()) {
                Log.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }

            /* 如果不需要打log，可以使用下面的语句
            if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
                return false;
            }
            */

            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
