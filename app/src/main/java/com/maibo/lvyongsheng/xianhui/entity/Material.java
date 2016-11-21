package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;

/**
 * Created by LYS on 2016/10/9.
 */
public class Material implements Serializable{
    private int item_id;
    private String fullname;
    private String qyt;
    private String use_spec;

    public Material(int item_id, String fullname, String qyt, String use_spec) {
        this.item_id = item_id;
        this.fullname = fullname;
        this.qyt = qyt;
        this.use_spec = use_spec;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getQyt() {
        return qyt;
    }

    public void setQyt(String qyt) {
        this.qyt = qyt;
    }

    public String getUse_spec() {
        return use_spec;
    }

    public void setUse_spec(String use_spec) {
        this.use_spec = use_spec;
    }
}
