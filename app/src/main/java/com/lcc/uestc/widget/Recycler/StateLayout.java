package com.lcc.uestc.widget.Recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by lcc_luffy on 2016/1/30.
 */
public class StateLayout extends FrameLayout{

    public static final String DEFAULT_ERROR_TEXT = "出错了-_-";
    public static final String DEFAULT_EMPTY_TEXT = "这儿啥也没有";
    public static final String DEFAULT_PROGRESS_TEXT = "正在拼了老命加载...";


    protected FrameLayout content;
    protected LinearLayout emptyView;
    protected LinearLayout errorView;
    protected LinearLayout progressView;

    protected TextView emptyTextView;
    protected TextView errorTextView;
    protected TextView progressTextView;


    protected Button errorButton;
    protected Button emptyButton;
    public StateLayout(Context context) {
        super(context);
        init();
    }



    public StateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initViews();

        currentShowingView = content;
        emptyView.setVisibility(GONE);
        errorView.setVisibility(GONE);
        progressView.setVisibility(GONE);
    }

    private void initViews() {
        content = new FrameLayout(getContext());
        content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        addView(content);

        /**********************************************************************************************/

        emptyView = new LinearLayout(getContext());
        initInnerView(emptyView);


        emptyButton = new Button(getContext());
        emptyButton.setText("重试");
        emptyButton.setLayoutParams(obtainLayoutParams());
        emptyView.addView(emptyButton);


        emptyTextView = new TextView(getContext());
        emptyTextView.setLayoutParams(obtainLayoutParams());
        emptyView.addView(emptyTextView);
        /**********************************************************************************************/

        errorView = new LinearLayout(getContext());
        initInnerView(errorView);

        errorButton = new Button(getContext());
        errorButton.setText("重试");
        errorButton.setLayoutParams(obtainLayoutParams());
        errorView.addView(errorButton);

        errorTextView = new TextView(getContext());
        errorTextView.setLayoutParams(obtainLayoutParams());
        errorView.addView(errorTextView);
        /**********************************************************************************************/

        progressView = new LinearLayout(getContext());
        initInnerView(progressView);

        ProgressBar progressBar = new ProgressBar(getContext());
        progressView.addView(progressBar);

        progressTextView = new TextView(getContext());
        progressTextView.setLayoutParams(obtainLayoutParams());
        progressView.addView(progressTextView);

    }

    private LayoutParams obtainLayoutParams()
    {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        return layoutParams;
    }

    private void initInnerView(LinearLayout innerView)
    {
        innerView.setGravity(Gravity.CENTER);
        innerView.setOrientation(LinearLayout.VERTICAL);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(innerView,layoutParams);
    }


    public void switchWithAnimation(final View toBeShown)
    {
        final View toBeHided = currentShowingView;
        if(toBeHided == toBeShown)
            return;

        if(toBeHided != null)
        {
            toBeHided.setVisibility(GONE);
        }

        if(toBeShown != null)
        {
            toBeShown.setVisibility(VISIBLE);
            currentShowingView = toBeShown;
        }
    }


    public void addContentView(View content)
    {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(content,layoutParams);
    }
    public void addContentView(View content, ViewGroup.LayoutParams layoutParams)
    {
        this.content.addView(content,layoutParams);
    }


    private View currentShowingView;
    public void showContentView()
    {
        switchWithAnimation(content);
    }
    public void showEmptyView()
    {
        showEmptyView(DEFAULT_EMPTY_TEXT);
    }

    public void showEmptyView(String msg)
    {

        onHideContentView();
        emptyTextView.setText(msg);
        switchWithAnimation(emptyView);

    }

    public void showErrorView()
    {
        showErrorView(DEFAULT_ERROR_TEXT);
    }

    public void showErrorView(String msg)
    {
        onHideContentView();
        errorTextView.setText(msg);
        switchWithAnimation(errorView);

    }

    public void showProgressView()
    {
        showProgressView(DEFAULT_PROGRESS_TEXT);
    }
    public void showProgressView(String msg)
    {
        onHideContentView();
        progressTextView.setText(msg);

        switchWithAnimation(progressView);

    }

    public void setErrorAction(String action, final OnClickListener onErrorButtonClickListener)
    {
        errorButton.setText(action);
        errorButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onErrorButtonClickListener != null)
                {
                    onErrorButtonClickListener.onClick(v);
                }
            }
        });
    }


    public void setEmptyAction(String action, final OnClickListener onEmptyButtonClickListener)
    {
        emptyButton.setText(action);
        emptyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onEmptyButtonClickListener != null)
                {
                    onEmptyButtonClickListener.onClick(v);
                }
            }
        });
    }

    protected void onHideContentView()
    {
        //Override me
    }
}
