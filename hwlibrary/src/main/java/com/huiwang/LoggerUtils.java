package com.huiwang;

import android.util.Log;

public class LoggerUtils {
    public static boolean debug=false;

    private static final String TAG="HuiWang";

    public static void e(String msg){
        if (debug)
        Log.e(TAG, msg);
    }

}
