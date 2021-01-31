package cn.jtduan.crack;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;

public class DeviceService {
    protected Context mContext;

    public DeviceService(Context applicationContext) {
        this.mContext = applicationContext;
    }

    public String getSystemPropertity(String key) {
        String result = "";
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            String value = (String) getMethod.invoke(classType, new Object[]{key});
            return value;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            //如果key超过32个字符则抛出该异常
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    public String getAndroidId() {
        String ANDROID_ID = Settings.System.getString(mContext.getContentResolver(), Settings.System.ANDROID_ID);
        String ANDROID_ID2 = Settings.System.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        return ANDROID_ID + ":" + ANDROID_ID2;
    }

    public String getIMEI() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "unknown";
        }
        return tm.getDeviceId();
    }

    public String getMacAddress() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String macAddress = info.getMacAddress();
        return macAddress == null ? "" : macAddress;
    }

    public String getSerial() {
        return getSystemPropertity("ro.build.id");
    }

    public String getHardware() {
        return Build.HARDWARE;
    }

    LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("AA", String.valueOf(location.getLatitude()));
            Log.i("AA", String.valueOf(location.getLongitude()));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.i("AA", "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.i("AA", "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.i("AA", "onProviderDisabled");
        }
    };

    public String getLocation() {
        LocationManager Locationm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "unknown";
        }

        Locationm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0,
                0, listener, Looper.getMainLooper());
        Location locationGPS = Locationm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String str1 = "";

        if (locationGPS != null) {
            str1 = str1 + locationGPS.getLatitude() + ":" + locationGPS.getLongitude();
        } else {
            str1 = str1 + "null" + "\n";
        }
        if (Locationm.getAllProviders().contains("network")) {
            Locationm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0,
                    0, listener, Looper.getMainLooper());

            Location locationNet = Locationm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (locationNet != null) {
                str1 = str1 + "\n" + locationNet.getLatitude() + ":" + locationNet.getLongitude();
            } else {
                str1 = str1 + "null" + "\n";
            }
        }

        Location locationPassive = Locationm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (locationPassive != null) {
            str1 = str1 + "\n" + locationPassive.getLatitude() + ":" + locationPassive.getLongitude();
        } else {
            str1 = str1 + "null" + "\n";
        }
        return str1;
    }
}
