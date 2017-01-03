package com.maibo.lvyongsheng.xianhui.entity;

/**
 * Created by LYS on 2016/12/16.
 */

public class EventDatas {
    private String tag;
    private String response;

    public EventDatas() {
    }

    public EventDatas(String tag, String response) {
        this.tag = tag;
        this.response = response;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
