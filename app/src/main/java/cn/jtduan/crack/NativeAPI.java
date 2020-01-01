package cn.jtduan.crack;

public class NativeAPI {
    static {
        System.loadLibrary("native-lib");
    }

    public static native void basic1();

//    public static native void onlySysCall();
//
//    public static native String onlyJni();
//
//    public static native String mallocJni();

    public static native String callJava(String param);

    public static native void updateKey(String param);

    public static native String stringFromJNI();

    public static String strFromJava(String param) {
        return "string from java," + innerFunc(param);
    }

    public static String innerFunc(String str) {
        return "(" + str + ")";
    }
}
