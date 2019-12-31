package cn.jtduan.crack;

public class NativeAPI {
    static {
        System.loadLibrary("native-lib");
    }

    public native String stringFromJNI();

    public native String callJava(String param);

    public native String updateKey(String param);

    public static String strFromJava(String param) {
        return "string from java," + innerFunc(param);
    }

    public static String innerFunc(String str) {
        return "(" + str + ")";
    }
}
