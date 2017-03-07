package com.maibo.lvyongsheng.xianhui.entity;

/**
 * Created by LYS on 2016/12/16.
 */

public class EventDatas {
    private String tag;
    private String response;
    private String messageStatus;
    private int tag2;

    public EventDatas() {
    }

    public EventDatas(String tag, String response) {
        this.tag = tag;
        this.response = response;
    }

    public EventDatas(String tag,String messageStatus,String response) {
        this.tag = tag;
        this.response = response;
        this.messageStatus = messageStatus;
    }

    public int getTag2() {
        return tag2;
    }

    public void setTag2(int tag2) {
        this.tag2 = tag2;
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

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }
}
