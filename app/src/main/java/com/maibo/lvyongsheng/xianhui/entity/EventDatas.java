package com.maibo.lvyongsheng.xianhui.entity;

import com.avos.avoscloud.im.v2.AVIMConversation;

/**
 * Created by LYS on 2016/12/16.
 */

public class EventDatas {
    private String tag;
    private String response;
    private String messageStatus;
    private int tag2;
    private String buffer;
    AVIMConversation conversation;
    private int position;
    private int product_total;
    private int project_total;
    private String result;

    public EventDatas(String tag,int position, int project_total, int product_total, String result) {
        this.tag=tag;
        this.position = position;
        this.project_total = project_total;
        this.product_total = product_total;
        this.result = result;
    }

    public EventDatas() {
    }

    public EventDatas(String tag, String response) {
        this.tag = tag;
        this.response = response;
    }

    public EventDatas(AVIMConversation conversation, String tag) {
        this.conversation = conversation;
        this.tag = tag;
    }

    public EventDatas(String tag, String messageStatus, String response) {
        this.tag = tag;
        this.response = response;
        this.messageStatus = messageStatus;
    }

    public EventDatas(String tag, String messageStatus, String response, String buffer) {
        this.tag = tag;
        this.response = response;
        this.messageStatus = messageStatus;
        this.buffer = buffer;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getProduct_total() {
        return product_total;
    }

    public void setProduct_total(int product_total) {
        this.product_total = product_total;
    }

    public int getProject_total() {
        return project_total;
    }

    public void setProject_total(int project_total) {
        this.project_total = project_total;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public String getBuffer() {
        return buffer;
    }

    public void setBuffer(String buffer) {
        this.buffer = buffer;
    }

    public AVIMConversation getConversation() {
        return conversation;
    }

    public void setConversation(AVIMConversation conversation) {
        this.conversation = conversation;
    }
}
