package com.call.block.group.model;

/**
 * Created by sourabh on 24/2/17.
 */

public class Log {
    static boolean LOG = true;

    public Log(){

    }

    public Log(Boolean check){
        LOG = check;
    }

    public static void i(String tag, String string) {
        if (LOG) android.util.Log.i(tag, string);
    }
    public static void e(String tag, String string) {
        if (LOG) android.util.Log.e(tag, string);
    }
    public static void d(String tag, String string) {
        if (LOG) android.util.Log.d(tag, string);
    }
    public static void v(String tag, String string) {
        if (LOG) android.util.Log.v(tag, string);
    }
    public static void w(String tag, String string) {
        if (LOG) android.util.Log.w(tag, string);
    }
}
