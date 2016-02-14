package com.lcc.uestc.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lcc.uestc.app.MyApp;


/**
 * Created by lgp on 2014/10/30.
 */
public class PreferenceUtils {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor shareEditor;

    public static final String NAME = "uestc";
    public static final String SEMESTER_KEY_EXAM = "SEMESTER_KEY_EXAM";
    public static final String EXAM_TYPE_KEY_EXAM = "EXAM_TYPE_KEY_EXAM";

    public static final String SEMESTER_KEY_GRADE = "SEMESTER_KEY_GRADE";
    public static final String EXAM_TYPE_KEY_GRADE = "EXAM_TYPE_KEY_GRADE";

    private volatile static PreferenceUtils preferenceUtils = null;

    private PreferenceUtils(Context context){
        sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        shareEditor = sharedPreferences.edit();
    }

    public void destroy()
    {
        preferenceUtils = null;
    }
    public static PreferenceUtils getInstance(){
        if (preferenceUtils == null)
        {
            Class var = PreferenceUtils.class;
            synchronized (var)
            {
                if (preferenceUtils == null) {
                    preferenceUtils = new PreferenceUtils(MyApp.getInstance().getApplicationContext());
                }
            }
        }
        return preferenceUtils;
    }

    public String getStringParam(String key){
        return getStringParam(key, "");
    }

    public String getStringParam(String key, String defaultString){
        return sharedPreferences.getString(key, defaultString);
    }

    public PreferenceUtils save(String key, String value)
    {
        shareEditor.putString(key,value).commit();
        return this;
    }

    public boolean getBooleanParam(String key){
        return getBooleanParam(key, false);
    }

    public boolean getBooleanParam(String key, boolean defaultBool){
        return sharedPreferences.getBoolean(key, defaultBool);
    }

    public PreferenceUtils save(String key, boolean value){
        shareEditor.putBoolean(key, value).commit();
        return this;
    }

    public int getIntParam(String key){
        return getIntParam(key, 0);
    }

    public int getIntParam(String key, int defaultInt){
        return sharedPreferences.getInt(key, defaultInt);
    }

    public PreferenceUtils save(String key, int value){
        shareEditor.putInt(key, value).commit();
        return this;
    }

    public long getLongParam(String key){
        return getLongParam(key, 0);
    }

    public long getLongParam(String key, long defaultInt){
        return sharedPreferences.getLong(key, defaultInt);
    }

    public void clear()
    {
        shareEditor.clear().commit();
    }
    public PreferenceUtils save(String key, long value){
        shareEditor.putLong(key, value).commit();
        return this;
    }
}
