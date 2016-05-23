package com.example.gulei.myapplication.common.utils;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.gulei.myapplication.GApplication;

/**
 * 输出工具类
 * Created by gulei on 2016/5/23 0023.
 */
public class PrintUtils {
    public static int v(String tag, String msg) {
        if(GApplication.getInstance().isDebug()){
            return Log.v(tag, msg);
        }
        return 0;
    }

    public static int d(String tag, String msg) {
        if(GApplication.getInstance().isDebug()){
            return Log.d(tag, msg);
        }
        return 0;
    }


    public static int i(String tag, String msg) {
        if(GApplication.getInstance().isDebug()){
            return Log.i(tag, msg);
        }
        return 0;
    }


    public static int w(String tag, String msg) {
        if(GApplication.getInstance().isDebug()){
            return Log.w(tag, msg);
        }
        return 0;
    }


    public static int e(String tag, String msg) {
        if(GApplication.getInstance().isDebug()){
            return Log.e(tag, msg);
        }
        return 0;
    }
    /**
     * @param message
     */
    public static void showToast(String message) {
        if(TextUtils.isEmpty(message)){
            return;
        }
        Toast.makeText(GApplication.getInstance(), message, Toast.LENGTH_LONG).show();
    }
}
