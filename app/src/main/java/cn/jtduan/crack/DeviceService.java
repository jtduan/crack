package cn.jtduan.crack;

import android.content.Context;
import android.provider.Settings;

public class DeviceService {
    public String getAndroidId(Context context) {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        return ANDROID_ID;
    }
}
