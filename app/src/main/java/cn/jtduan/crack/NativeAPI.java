package cn.jtduan.crack;

public class NativeAPI {
    static {
        System.loadLibrary("myc");
    }

    public static native void basic1();

//    public static native void onlySysCall();
//
//    public static native String onlyJni();
//
//    public static native String mallocJni();

    public static native String callJava(String param);

    public static native void updateKey(String param);

    public static native long testLong(int a, long ts);

    public static native long testSyscall();

    public static native String propertityGet();

    public static String strFromJava(String param) {
        return "string from java," + innerFunc(param);
    }

    public static String innerFunc(String str) {
        return "(" + str + ")";
    }
}
