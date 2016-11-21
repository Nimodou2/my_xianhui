package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;

/**
 * Created by LYS on 2016/9/13.
 */
public class Consume implements Serializable{
    private int item_id;
    private String fullname;
    private String saledate;

    public Consume(int item_id, String fullname, String saledate) {
        this.item_id = item_id;
        this.fullname = fullname;
        this.saledate = saledate;
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

    public String getSaledate() {
        return saledate;
    }

    public void setSaledate(String saledate) {
        this.saledate = saledate;
    }
}
