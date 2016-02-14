package com.lcc.uestc.bean;

/**
 * Created by lcc_luffy on 2016/1/7.
 */
public abstract class Command {
    protected String title;
    public Command(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title;
    }
    public abstract void action();
    public int id(){return 0;};
}
