package com.lcc.uestc.presenter;

import com.lcc.uestc.bean.ExamInfoBean;
import com.lcc.uestc.data.CallBack;
import com.lcc.uestc.data.UESTC;
import com.lcc.uestc.iview.IView;

import java.util.List;

/**
 * Created by wanli on 2015/7/21.
 */
public class ExamPresenter extends BasePresenter<List<ExamInfoBean>>{
    public ExamPresenter(IView<List<ExamInfoBean>> view)
    {
        super(view);
    }
    public void getExamInfo()
    {
        UESTC.getExamInfo(new CallBack<List<ExamInfoBean>>() {
            @Override
            public void onStart() {
                view.showStart();
            }

            @Override
            public void onSuccess(List<ExamInfoBean> list) {
                view.showSuccess(list);
            }

            @Override
            public void onFailed(String failReason) {
                view.showFail(failReason);
            }
        });
    }

}
