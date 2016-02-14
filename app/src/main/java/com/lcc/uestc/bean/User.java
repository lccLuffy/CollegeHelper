package com.lcc.uestc.bean;

import android.text.TextUtils;

import com.lcc.uestc.data.CallBack;
import com.lcc.uestc.data.SimpleCallback;
import com.lcc.uestc.data.UESTC;
import com.lcc.uestc.utils.CommonUtils;
import com.lcc.uestc.utils.PreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wanli on 2015/7/20.
 */
public class User {
    public static String DEFAULT_USERNAME = "游客";
    private volatile static User user;

    private String xuehao;
    private String password;
    private String name;
    private String info;

    private User()
    {
        xuehao = PreferenceUtils.getInstance().getStringParam("xuehao",null);
        password = PreferenceUtils.getInstance().getStringParam("password", null);
        name = PreferenceUtils.getInstance().getStringParam("name",null);
        info = PreferenceUtils.getInstance().getStringParam("info",null);
    }
    public static User getInstance()
    {
        if(user == null)
        {
            Class var = User.class;
            synchronized (var)
            {
                if(user == null)
                    user = new User();
            }
        }
        return user;
    }
    public String getInfo() {
        return info;
    }

    public User setInfo(String info) {
        this.info = info;
        return this;
    }
    public String getName() {
        if(name == null)
        {
            return DEFAULT_USERNAME;
        }
        return name;
    }

    private User setName(String name) {
        this.name = name;
        return this;
    }
    public String getPassword() {
        return password;
    }
    private User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getXuehao() {
        return xuehao;
    }

    private User setXuehao(String xuehao) {
        this.xuehao = xuehao;
        return this;
    }
    public void save()
    {
        PreferenceUtils.getInstance()
                .save("xuehao", xuehao)
                .save("password", password)
                .save("name", name)
                .save("info", info);
    }
    public boolean isLogout()
    {
        return TextUtils.isEmpty(getInstance().getXuehao());
    }
    public void logout()
    {
        PreferenceUtils.getInstance().clear();
        user = null;
        ExamInfoBean.deleteAll(ExamInfoBean.class);
        GradeBean.deleteAll(GradeBean.class);
        CommonUtils.toast("已经退出登录");
    }
    public void login(final String xuehao,final String password,final CallBack<String> callBack)
    {
        UESTC.getStudentInfo(xuehao, password, new SimpleCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                try {
                    String name = data.getString("姓名：");
                    ExamInfoBean.deleteAll(ExamInfoBean.class);
                    GradeBean.deleteAll(GradeBean.class);
                    getInstance()
                            .setXuehao(xuehao)
                            .setPassword(password)
                            .setName(name)
                            .setInfo(data.toString())
                            .save();
                    if(callBack != null)
                    {
                        callBack.onSuccess("success");
                    }
                }
                catch (JSONException e)
                {
                    if(callBack != null)
                    {
                        callBack.onFailed(e.toString());
                    }
                }
            }
            @Override
            public void onFailed(String failReason) {
                if(callBack != null)
                {
                    callBack.onFailed(failReason);
                }
            }
        });
    }
}
