package com.lcc.uestc.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by lcc_luffy on 2016/1/9.
 */
public class CodeBean extends BmobObject{

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    private boolean collected;
    private String title;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    private String picture;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String url;
    private String description;
}
