package com.lcc.uestc.fragment;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.lcc.uestc.R;
import com.lcc.uestc.adapter.ExamAdapter;
import com.lcc.uestc.bean.ActionBarCommands;
import com.lcc.uestc.bean.Command;
import com.lcc.uestc.bean.ExamInfoBean;
import com.lcc.uestc.data.UESTC;
import com.lcc.uestc.iview.IView;
import com.lcc.uestc.presenter.ExamPresenter;
import com.lcc.uestc.utils.PreferenceUtils;
import com.lcc.uestc.widget.Recycler.NiceAdapter;
import com.lcc.uestc.widget.Recycler.StateRecyclerView;
import com.orm.SugarRecord;

import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExamFragment extends BaseFragment implements IView<List<ExamInfoBean>>{

    @Bind(R.id.stateRecyclerView)
    StateRecyclerView niceRecyclerView;


    Spinner examTypeSpinner;

    ExamPresenter presenter;
    ExamAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_exam;
    }

    @Override
    protected void initInWhenConstruct() {
        actionBarCommands =new ExamCommands();
    }

    @Override
    protected void initInOnCreate() {
        presenter = new ExamPresenter(this);
        layoutManager = new LinearLayoutManager(activity);

        adapter = new ExamAdapter(getActivity());
        adapter.showEmptyDataIsEmpty(false);
        niceRecyclerView.setAdapter(adapter,true);
        niceRecyclerView.setLayoutManager(layoutManager);
        niceRecyclerView.setColorSchemeColors(getColorPrimary());
        niceRecyclerView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getExamInfo();
            }
        });
        presenter.getExamInfo();
        final View spinner = LayoutInflater.from(getContext()).inflate(R.layout.exam_type_spinner,null);
        examTypeSpinner = (Spinner) spinner.findViewById(R.id.examTypeSpinner);
        spinner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        adapter.addHeaderView(new NiceAdapter.ItemView() {

            @Override

            public View onCreateView(ViewGroup parent) {
                return spinner;
            }

            @Override
            public void onBindViewHolder(int realPosition) {

            }
        });
        examTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                        PreferenceUtils.getInstance().save(PreferenceUtils.EXAM_TYPE_KEY_EXAM, UESTC.EXAM_TYPE_FINAL);
                        break;
                    case 1:
                        PreferenceUtils.getInstance().save(PreferenceUtils.EXAM_TYPE_KEY_EXAM, UESTC.EXAM_TYPE_MID);
                        break;
                    case 2:
                        PreferenceUtils.getInstance().save(PreferenceUtils.EXAM_TYPE_KEY_EXAM, UESTC.EXAM_TYPE_MAKEUP);
                        break;
                    case 3:
                        PreferenceUtils.getInstance().save(PreferenceUtils.EXAM_TYPE_KEY_EXAM, UESTC.EXAM_TYPE_HUANKAO);
                        break;
                }
                presenter.getExamInfo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        examTypeSpinner.setSelection(parseExamType(PreferenceUtils.getInstance().getIntParam(PreferenceUtils.EXAM_TYPE_KEY_EXAM, UESTC.EXAM_TYPE_FINAL)));
    }

    public void update(boolean force)
    {
        if(force || adapter.isDataEmpty())
        {
            adapter.clear(true);
            ExamInfoBean.deleteAll(ExamInfoBean.class);
            presenter.getExamInfo();
        }
        else
        {
            showSnackBar("清除本地数据,从信息门户同步考试?", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.clear(true);
                    SugarRecord.deleteAll(ExamInfoBean.class);
                    presenter.getExamInfo();
                }
            });
        }
    }

    public static final int parseExamType(int type)
    {
        int result = 0;
        switch (type)
        {
            case UESTC.EXAM_TYPE_FINAL:
                result = 0;
                break;
            case UESTC.EXAM_TYPE_MID:
                result = 1;
                break;
            case UESTC.EXAM_TYPE_MAKEUP:
                result = 2;
                break;
            case UESTC.EXAM_TYPE_HUANKAO:
                result = 3;
                break;
        }
        return result;
    }


    @Override
    public void showSuccess(List<ExamInfoBean> data) {
        niceRecyclerView.setRefreshing(false);
        adapter.initData(data);
        if(!data.isEmpty())
        {
            adapter.showNoMoreView();
        }
    }

    @Override
    public void showFail(String msg) {
        niceRecyclerView.setRefreshing(false);
        adapter.clear(true);
        showSnackBar(msg);
        adapter.showErrorView();
    }

    @Override
    public void showStart() {
        niceRecyclerView.setRefreshing(true);
    }
    class ExamCommand extends Command
    {
        private final int semester;

        public ExamCommand(String title, int semester) {
            super(title);
            this.semester = semester;
        }
        @Override
        public void action() {
            PreferenceUtils.getInstance().save(PreferenceUtils.SEMESTER_KEY_EXAM, semester);
            presenter.getExamInfo();
        }

        @Override
        public int id() {
            return semester;
        }
    }
    private class ExamCommands extends ActionBarCommands
    {
        ExamCommands()
        {
            for (Map.Entry<Integer,String> kv : UESTC.SEMESTERS.entrySet())
            {
                commands.add(new ExamCommand(kv.getValue(),kv.getKey()));
            }

            int select = PreferenceUtils.
                    getInstance().getIntParam(PreferenceUtils.SEMESTER_KEY_EXAM, UESTC.DEFAULT_SEMESTER);
            for (int i=0;i<commands.size();i++)
            {
                if(commands.get(i).id() == select)
                {
                    setSelect(i);
                    break;
                }
            }
        }

        @Override
        public void refresh(boolean force) {
            update(force&&PreferenceUtils.getInstance().getBooleanParam(SettingFragment.update_no_snackbar, true));
        }
    }

    @Override
    public String toString() {
        return "考试";
    }
}
