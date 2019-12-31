package com.yxcorp.gifshow.util;

import android.content.Context;

public class CPU {
    public static native String getClock(Context context, byte[] bArr, int i);

    public static native String getMagic(Context context, int i);

    static {
        System.loadLibrary("core");
    }

    public static synchronized String a(Context context, byte[] bArr, int i) {
        String clock;
        synchronized (CPU.class) {
            clock = getClock(context, bArr, i);
        }
        return clock;
    }
}