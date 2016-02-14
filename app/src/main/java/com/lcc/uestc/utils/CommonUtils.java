package com.lcc.uestc.utils;

import android.util.Log;
import android.widget.Toast;

import com.lcc.uestc.app.MyApp;

/**
 * Created by wanli on 2015/7/13.
 */
public class CommonUtils {
    public static final String TAG = "main";
    public static void toast(Object s){
        Toast.makeText(MyApp.getInstance(),s==null?"":s.toString(),Toast.LENGTH_SHORT).show();
    }
    public static void toast(Object format, Object... args){
        if(format == null)
            throw new NullPointerException("format cannot be null");

        Toast.makeText(MyApp.getInstance(),String.format(format.toString(),args),Toast.LENGTH_SHORT).show();
    }
    public static void i(Object s){
        Log.i(TAG,s == null ? "null" : s.toString());
    }
}
