package com.maibo.lvyongsheng.xianhui.entity;

/**
 * Created by LYS on 2016/10/19.
 */
public class SelectEntity {
    private String value;
    private String text;
    private Boolean selected;
    private Boolean disabled;

    public SelectEntity(String value, String text, Boolean selected,Boolean disabled) {
        this.value = value;
        this.text = text;
        this.selected = selected;
        this.disabled=disabled;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
