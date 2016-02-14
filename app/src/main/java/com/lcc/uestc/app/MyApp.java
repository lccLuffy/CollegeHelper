package com.lcc.uestc.app;

import android.util.TypedValue;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.lcc.uestc.R;
import com.orm.SugarApp;

import cn.bmob.v3.Bmob;

/**
 * Created by wanli on 2015/6/28.
 */
public class MyApp extends SugarApp {
    private static MyApp instance;
    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        Fresco.initialize(this);
        Bmob.initialize(this,"eb04a74de3c8a97f2279be3548673d82");
    }

    public static MyApp getInstance() {
        return instance;
    }
    public int getColorPrimary(){
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }
}
