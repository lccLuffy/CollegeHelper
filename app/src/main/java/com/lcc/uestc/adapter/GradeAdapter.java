package com.lcc.uestc.adapter;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lcc.uestc.R;
import com.lcc.uestc.bean.GradeBean;
import com.lcc.uestc.widget.Recycler.NiceAdapter;
import com.lcc.uestc.widget.Recycler.NiceViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wanli on 2015/7/13.
 */
public class GradeAdapter extends NiceAdapter<GradeBean> {
    private double averageGPA;
    private List<GradeBean> sortGrade;
    public GradeAdapter(Activity context)
    {
        super(context);
        sortGrade = new ArrayList<>();
    }

    @Override
    protected NiceViewHolder onCreateNiceViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade_layout,parent,false));
    }

    @Override
    public void initData(Collection<GradeBean> collection) {
        sortGrade.clear();
        sortGrade.addAll(collection);
        double GPA = 0,credit = 0;
        try {
            for (GradeBean i : sortGrade) {
                data.add(i);
                double tmp = Double.parseDouble(i.getCredit() == null ? "0" : i.getCredit());
                credit = credit + tmp;
                GPA = GPA + Double.parseDouble(i.getGPA() == null ? "0" : i.getGPA()) * tmp;
            }
        }
        catch (NumberFormatException e)
        {
            GPA = 0.0;
        }
        averageGPA = GPA / credit;
        Collections.sort(sortGrade);

        super.initData(sortGrade);
    }

    class Holder extends NiceViewHolder<GradeBean>
    {
        @Bind(R.id.courseName)
        TextView courseName;

        @Bind(R.id.finalGrade)
        TextView finalGrade;

        @Bind(R.id.GPA)
        TextView GPA;

        @Bind(R.id.credit)
        TextView credit;

        @Bind(R.id.progressBar)
        ProgressBar progressBar;
        public Holder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onBindData(GradeBean data) {
            int g = 0;
            try {
                g = Integer.parseInt(data.getFinalGrade());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            ObjectAnimator.ofInt(progressBar, "progress", 0, g)
                    .setDuration(1000)
                    .start();

            ObjectAnimator.ofFloat(itemView, "scaleX", 0.75f, 1f)
                    .setDuration(100)
                    .start();
            ObjectAnimator.ofFloat(itemView, "scaleY", 0.75f, 1f)
                    .setDuration(100)
                    .start();
            courseName.setText(data.getCourseName());
            progressBar.setProgress(g);
            finalGrade.setText("最终成绩: "+data.getFinalGrade());
            credit.setText("学    分: " + data.getCredit());
            GPA.setText("绩    点: " + data.getGPA());
        }
    }
    public double getAverageGPA() {
        return averageGPA;
    }
}
