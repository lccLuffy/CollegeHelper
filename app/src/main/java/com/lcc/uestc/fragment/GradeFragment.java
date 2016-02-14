package com.lcc.uestc.fragment;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lcc.uestc.R;
import com.lcc.uestc.adapter.GradeAdapter;
import com.lcc.uestc.bean.ActionBarCommands;
import com.lcc.uestc.bean.Command;
import com.lcc.uestc.bean.GradeBean;
import com.lcc.uestc.data.SimpleCallback;
import com.lcc.uestc.data.UESTC;
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
public class GradeFragment extends BaseFragment {
    @Bind(R.id.stateRecyclerView)
    StateRecyclerView stateRecyclerView;

    GradeAdapter adapter;
    LinearLayoutManager layoutManager;
    private double averageGPA = 0.00;

    TextView textView;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_exam;
    }

    @Override
    protected void initInWhenConstruct() {
        actionBarCommands = new GradeCommands();
    }

    @Override
    protected void initInOnCreate() {

        textView = new TextView(getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layoutManager = new LinearLayoutManager(activity);
        adapter = new GradeAdapter(activity);
        adapter.showNoMoreView();
        stateRecyclerView.setAdapter(adapter);
        stateRecyclerView.setLayoutManager(layoutManager);
        stateRecyclerView.setColorSchemeColors(getColorPrimary());
        stateRecyclerView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                get();
            }
        });
        get();

        adapter.addHeaderView(new NiceAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return textView;
            }

            @Override
            public void onBindViewHolder(int realPosition) {
            }
        });

    }

    private void get() {
        stateRecyclerView.setRefreshing(true);
        UESTC.getGradeInfo(new SimpleCallback<List<GradeBean>>() {
            @Override
            public void onSuccess(List<GradeBean> list) {
                stateRecyclerView.setRefreshing(false);
                adapter.initData(list);
                averageGPA = adapter.getAverageGPA();
                textView.setText(String.format("平均绩点: %.2f.",averageGPA));
            }

            @Override
            public void onFailed(String failReason) {
                stateRecyclerView.setRefreshing(false);
                showSnackBar(failReason);
            }
        });
    }

    public void update(boolean force) {
        if (!force) {
            showSnackBar("清除本地数据,从信息门户同步成绩?", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SugarRecord.deleteAll(GradeBean.class);
                    get();
                }
            });
        } else {
            SugarRecord.deleteAll(GradeBean.class);
            get();
        }
    }

    class GradeCommand extends Command {
        private final int semester;

        public GradeCommand(String title, int semester) {
            super(title);
            this.semester = semester;
        }

        @Override
        public void action() {
            PreferenceUtils.getInstance().save(PreferenceUtils.SEMESTER_KEY_GRADE, semester);
            get();
        }

        @Override
        public int id() {
            return semester;
        }
    }

    private class GradeCommands extends ActionBarCommands {
        GradeCommands() {
            for (Map.Entry<Integer, String> kv : UESTC.SEMESTERS.entrySet()) {
                commands.add(new GradeCommand(kv.getValue(), kv.getKey()));
            }
            int select = PreferenceUtils.
                    getInstance().getIntParam(PreferenceUtils.SEMESTER_KEY_GRADE, UESTC.DEFAULT_SEMESTER);
            for (int i = 0; i < commands.size(); i++) {
                if (commands.get(i).id() == select) {
                    setSelect(i);
                    break;
                }
            }
        }

        @Override
        public void refresh(boolean force) {
            update(force && PreferenceUtils.getInstance().getBooleanParam(SettingFragment.update_no_snackbar, true));
        }

    }

    @Override
    public String toString() {
        return "成绩";
    }
}
