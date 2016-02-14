package com.lcc.uestc;

import android.util.ArrayMap;

import java.util.Map;

/**
 * Created by wanli on 2015/6/28.
 */
public class Config {

    public static final int NO_LOGIN_EVENT = 0X00;
    public static final int UPDATE_EVENT = 0X01;
    public static final int FORCE_UPDATE_EVENT_EXAM = 0X02;
    public static final int FORCE_UPDATE_EVENT_GRADE = 0X03;


    public final static Map<Integer,String> NEWS = new ArrayMap<>();
    static
    {
        NEWS.put(42,"焦点新闻");
        NEWS.put(50,"校园时讯");
        NEWS.put(43,"教育教学");
        NEWS.put(44,"科研学术");
        NEWS.put(45,"合作交流");
        NEWS.put(49,"院部新闻");
        NEWS.put(46,"成电讲堂");
        NEWS.put(48,"成电人物");
        NEWS.put(51,"媒体成电");
        NEWS.put(15698,"多彩校园");
        NEWS.put(47,"校友动态");
    }

    public static final int jdxw = 42;
    public static final int xysx = 50;
    public static final int jyjx = 43;
    public static final int kyxs = 44;
    public static final int hzjl = 45;
    public static final int ybxw = 49;
    public static final int cdjt = 46;
    public static final int cdrw = 48;
    public static final int mtcd = 51;
    public static final int dcxy = 15698;
    public static final int xydt = 47;
    public static final class Str
    {
        public static final String jdxw = "焦点新闻";
        public static final String xysx = "校园时讯";
        public static final String jyjx = "教育教学";
        public static final String kyxs = "科研学术";
        public static final String hzjl = "合作交流";
        public static final String ybxw = "院部新闻";
        public static final String cdjt = "成电讲堂";
        public static final String cdrw = "成电人物";
        public static final String mtcd = "媒体成电";
        public static final String dcxy = "多彩校园";
        public static final String xydt = "校友动态";
    }

}
