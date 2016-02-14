package com.lcc.uestc.bean;

import com.lcc.uestc.adapter.ActionBarSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lcc_luffy on 2016/1/1.
 */
public abstract class ActionBarCommands {
    protected ActionBarSpinnerAdapter actionBarSpinnerAdapter;
    public final List<Command> commands = new ArrayList<>();
    public void action(int position){
        commands.get(position).action();
    };
    protected int select = 0;


    public void attachToAdapter(ActionBarSpinnerAdapter actionBarSpinnerAdapter)
    {
        this.actionBarSpinnerAdapter = actionBarSpinnerAdapter;
    }
    public void notifyDataSetChanged()
    {
        if(actionBarSpinnerAdapter == null || isEmpty())
            return;
        actionBarSpinnerAdapter.showSpinner(true);
        actionBarSpinnerAdapter.notifyDataSetChanged();
    }

    public void OnFragmentSelected(){};

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public boolean isEmpty()
    {
        return commands.isEmpty();
    }
    public int count()
    {
        return commands.size();
    }
    public String titleAt(int position)
    {
        return commands.get(position).toString();
    }

    public void refresh(boolean force){}
    public void hook(){}
}
