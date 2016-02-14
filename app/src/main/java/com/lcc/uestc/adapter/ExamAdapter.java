package com.lcc.uestc.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lcc.uestc.R;
import com.lcc.uestc.bean.ExamInfoBean;
import com.lcc.uestc.widget.Recycler.NiceAdapter;
import com.lcc.uestc.widget.Recycler.NiceViewHolder;
import com.lcc.uestc.widget.TimeLineView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wanli on 2015/7/13.
 */
public class ExamAdapter extends NiceAdapter<ExamInfoBean> {
    List<ExamInfoBean> hasEndedExam = new ArrayList<>();
    List<ExamInfoBean> noEndedExam = new ArrayList<>();
    Resources resources;
    LayoutInflater mInflater;
    public ExamAdapter(Activity context)
    {
        super(context);
        resources = context.getResources();
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getViewType(int position)
    {
        final int size = data.size() - 1;
        if (size == 0)
            return ItemType.ATOM;
        else if (position == 0)
            return ItemType.START;
        else if (position == size)
            return ItemType.END;
        else
            return ItemType.NORMAL;
    }

    @Override
    public void initData(Collection<ExamInfoBean> collection) {
        if(collection == null)
        {
            notifyDataSetChanged();
            return;
        }

        noEndedExam.clear();
        hasEndedExam.clear();

        for(ExamInfoBean i : collection)
        {
            if (i.getStartTime() > System.currentTimeMillis())
            {
                noEndedExam.add(i);
            }
            else
            {
                hasEndedExam.add(i);
            }
        }
        Collections.sort(noEndedExam, new Comparator<ExamInfoBean>() {
            @Override
            public int compare(ExamInfoBean lhs, ExamInfoBean rhs) {
                return (int) (lhs.getStartTime() - rhs.getStartTime());
            }
        });
        Collections.sort(hasEndedExam, new Comparator<ExamInfoBean>() {
            @Override
            public int compare(ExamInfoBean lhs, ExamInfoBean rhs) {
                return (int) (rhs.getStartTime() - lhs.getStartTime());
            }
        });
        noEndedExam.addAll(hasEndedExam);

        super.initData(noEndedExam);
    }

    @Override
    protected NiceViewHolder onCreateNiceViewHolder(ViewGroup parent, int viewType) {
        return new Holder(mInflater.inflate(R.layout.item_exam_layout,parent,false),viewType);
    }


    String parseTime(long time)
    {
        long gap = time - System.currentTimeMillis();

        if(gap < 0)
        {
            return "结束";
        }

        gap /=1000;
        StringBuilder stringBuilder = new StringBuilder();
        long day=gap / (24*3600);
        long hour=gap % (24*3600)/3600;
        long minute=gap % 3600/60;
        if(day != 0)
        {
            stringBuilder.append(day+" D\n");
        }
        if(hour != 0) {
            stringBuilder.append(hour + " H\n");
            if(day != 0)
                return stringBuilder.toString();
        }
        if(minute != 0) {
            stringBuilder.append(minute + " M");
        }
        return stringBuilder.toString();
    }
    class Holder extends NiceViewHolder<ExamInfoBean>
    {
        @Bind(R.id.courseName)
        TextView courseName;

        @Bind(R.id.arrange)
        TextView arrange;


        @Bind(R.id.place)
        TextView place;

        @Bind(R.id.leftTime)
        TextView leftTime;

        @Bind(R.id.timelineView)
        TimeLineView timeLineView;
        public Holder(View itemView,int type)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);
            if (type == ItemType.ATOM) {
                timeLineView.setBeginLine(null);
                timeLineView.setEndLine(null);
            } else if (type == ItemType.START) {
                timeLineView.setBeginLine(null);
            } else if (type == ItemType.END) {
                timeLineView.setEndLine(null);
            }
        }

        @Override
        public void onBindData(ExamInfoBean data)
        {
            courseName.setText(data.getCourseName());

            arrange.setText(data.getTime());

            place.setText(data.getPlace()+"  座位 "+data.getSeat());

            timeLineView.setTime(data.getDate().substring(data.getDate().indexOf('-')+1));

            if(data.getStartTime() < System.currentTimeMillis())
            {
                int color = resources.getColor(R.color.background_material_dark);
                leftTime.setTextColor(color);
                place.setTextColor(color);
                arrange.setTextColor(color);
                courseName.setTextColor(color);
            }
            else
            {
                leftTime.setTextColor(resources.getColor(android.R.color.holo_orange_dark));
                place.setTextColor(resources.getColor(android.R.color.holo_green_light));
                arrange.setTextColor(resources.getColor(android.R.color.holo_green_dark));
                courseName.setTextColor(resources.getColor(android.R.color.holo_blue_light));
            }

            leftTime.setText(parseTime(data.getStartTime()));
        }
    }


    public static class ItemType {
        public final static int NORMAL = 0;

        public final static int HEADER = 1;
        public final static int FOOTER = 2;

        public final static int START = 4;
        public final static int END = 8;

        public final static int ATOM = 16;
    }
}
