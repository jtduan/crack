package com.weibo.xvideo;

public class NativeApi {
    static {
        System.loadLibrary("oasiscore");
    }

    public native String s(String param1, boolean param2);
}
