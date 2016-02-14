package com.lcc.uestc.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lcc.uestc.R;
import com.lcc.uestc.bean.ActionBarCommands;

import butterknife.ButterKnife;

/**
 * Created by wanli on 2015/7/13.
 */
public abstract class BaseFragment extends Fragment {
    public Activity activity;
    public View rootView;
    protected ActionBarCommands actionBarCommands = null;
    public BaseFragment(){initInWhenConstruct();};
    protected void initInWhenConstruct()
    {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = LayoutInflater.from(activity).inflate(getLayoutView(),null);
        ButterKnife.bind(this, rootView);
        initInOnCreate();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initInOnCreateView();
        return rootView;
    }
    public int getColorPrimary(){
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }
    protected abstract int getLayoutView();

    /**
     * may be called not only once,it is controlled by fragmentManager
     */
    protected void initInOnCreateView(){};

    /**
     * is called only once when fragment is created
     */
    protected void initInOnCreate(){};
    public void showSnackBar(String msg)
    {
        msg = (msg == null ? "null" : msg);
        Snackbar.make(rootView,msg,Snackbar.LENGTH_LONG).show();
    }
    public void showSnackBar(String msg,View.OnClickListener onClickListener)
    {
        msg = (msg == null ? "null" : msg);
        Snackbar.make(rootView,msg,Snackbar.LENGTH_LONG).setAction("确定",onClickListener).show();
    }
    public ActionBarCommands getActionBarCommands()
    {
        return actionBarCommands;
    }
}
