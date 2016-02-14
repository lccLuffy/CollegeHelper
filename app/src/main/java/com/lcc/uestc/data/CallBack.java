package com.lcc.uestc.data;

/**
 * Created by wanli on 2015/7/3.
 */
public interface CallBack<T> {
    void onSuccess(T data);
    void onFailed(String failReason);
    void onStart();
}
