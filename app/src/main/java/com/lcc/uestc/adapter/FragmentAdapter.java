package com.lcc.uestc.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lcc.uestc.fragment.BaseFragment;
import com.lcc.uestc.fragment.BroadcastFragment;
import com.lcc.uestc.fragment.CodeFragment;
import com.lcc.uestc.fragment.GradeFragment;
import com.lcc.uestc.fragment.ExamFragment;
import com.lcc.uestc.fragment.WeatherFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanli on 2015/7/7.
 */
public class FragmentAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragments;
    ActionBarSpinnerAdapter actionBarSpinnerAdapter;
    public FragmentAdapter(FragmentManager fm,ActionBarSpinnerAdapter actionBarSpinnerAdapter) {
        super(fm);
        this.actionBarSpinnerAdapter = actionBarSpinnerAdapter;
        mFragments = new ArrayList<>();

        mFragments.add(new ExamFragment());

        mFragments.add(new GradeFragment());

        mFragments.add(new BroadcastFragment());

        mFragments.add(new CodeFragment());

        mFragments.add(new WeatherFragment());
    }

    public void refresh(boolean force)
    {
        actionBarSpinnerAdapter.refresh(force);
    }
    public void OnSelect(int position)
    {
        actionBarSpinnerAdapter.setActionBarCommands(mFragments.get(position).getActionBarCommands());
    }
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }
    @Override
    public int getCount() {
        return mFragments.size();
    }
    @Override
    public CharSequence getPageTitle(int position)
    {
        return mFragments.get(position).toString();
    }
}
