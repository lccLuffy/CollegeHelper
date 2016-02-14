package com.lcc.uestc.iview;

/**
 * Created by wanli on 2015/7/3.
 */
public interface IView<T> {
    void showSuccess(T data);
    void showFail(String msg);
    void showStart();
}
