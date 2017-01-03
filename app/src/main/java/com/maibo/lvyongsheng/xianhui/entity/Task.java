package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;

/**
 * Created by LYS on 2016/11/23.
 */

public class Task implements Serializable{
    private String text;
    private String value;
    private int type;
    private int isChecked;

    public Task() {}

    public Task(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public Task(String text, String value, int type, int isChecked) {
        this.text = text;
        this.value = value;
        this.type = type;
        this.isChecked=isChecked;
    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
