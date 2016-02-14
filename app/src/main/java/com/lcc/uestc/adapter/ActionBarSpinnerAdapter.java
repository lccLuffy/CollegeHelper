package com.lcc.uestc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.lcc.uestc.R;
import com.lcc.uestc.bean.ActionBarCommands;

/**
 * Created by lcc_luffy on 2016/1/1.
 */
public class ActionBarSpinnerAdapter extends android.widget.BaseAdapter {
    ActionBarCommands actionBarCommands = null;
    private LayoutInflater layoutInflater;
    private Spinner spinner;
    public ActionBarSpinnerAdapter(Context context, Spinner spinner) {
        layoutInflater = LayoutInflater.from(context);
        this.spinner=spinner;

        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(actionBarCommands.getSelect() != position)
                {
                    actionBarCommands.setSelect(position);
                    actionBarCommands.action(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public int getCount() {
        return actionBarCommands ==null?0: actionBarCommands.count();
    }

    @Override
    public String getItem(int position) {
        return actionBarCommands.titleAt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView :
                layoutInflater.inflate(R.layout.toolbar_spinner_item_actionbar, parent, false);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getItem(position));
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View view = convertView != null ? convertView :
                layoutInflater.inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getItem(position));

        return view;
    }

    public void refresh(boolean force)
    {
        if(null != actionBarCommands)
            actionBarCommands.refresh(force);
    }
    public void setActionBarCommands(final ActionBarCommands actionBarCommands)
    {
        if(actionBarCommands != null)
        {
            if(this.actionBarCommands != null)
                this.actionBarCommands.attachToAdapter(null);

            this.actionBarCommands = actionBarCommands;
            this.actionBarCommands.OnFragmentSelected();
            this.actionBarCommands.attachToAdapter(this);
            spinner.setSelection(actionBarCommands.getSelect());
            if(!actionBarCommands.isEmpty())
            {
                showSpinner(true);
                notifyDataSetChanged();
            }
            else
            {
                showSpinner(false);
            }
        }
        else
        {
            if(this.actionBarCommands != null)
                this.actionBarCommands.attachToAdapter(null);
            showSpinner(false);
        }
    }

    public void showSpinner(boolean show)
    {
        if(show)
        {
            if(spinner.getVisibility() != View.VISIBLE)
            {
                spinner.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            if(spinner.getVisibility() != View.GONE)
            {
                spinner.setVisibility(View.GONE);
            }
        }
    }
}
