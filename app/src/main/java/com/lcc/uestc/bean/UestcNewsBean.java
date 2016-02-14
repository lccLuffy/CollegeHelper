package com.lcc.uestc.bean;

/**
 * Created by wanli on 2015/7/3.
 */
public class UestcNewsBean {
    private String title;
    private String content;
    private String url;
    private String pic;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public String getPic() {
        return pic;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Override
    public String toString() {
        return title+"  "+pic+"   "+content;
    }
}
