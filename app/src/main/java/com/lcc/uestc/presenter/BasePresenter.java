package com.lcc.uestc.presenter;

import android.support.annotation.NonNull;

import com.lcc.uestc.iview.IView;

/**
 * Created by wanli on 2015/7/3.
 */
public class BasePresenter<T> {
    public  @NonNull IView<T> view;
    public BasePresenter(IView<T> view) {
        this.view = view;
    }
}
