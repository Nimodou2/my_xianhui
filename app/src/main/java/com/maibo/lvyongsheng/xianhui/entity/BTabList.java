package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LYS on 2016/9/18.
 */
public class BTabList implements Serializable{
    private int number;
    private String name;
    private String date;
    private List<LTabList> lTabLists;
    private String amount;

    public BTabList(String name, List<LTabList> lTabLists, String amount) {
        this.name = name;
        this.lTabLists = lTabLists;
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public BTabList(int number, String name, String date, List<LTabList> lTabLists) {
        this.number = number;
        this.name = name;
        this.date=date;
        this.lTabLists = lTabLists;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LTabList> getlTabLists() {
        return lTabLists;
    }

    public void setlTabLists(List<LTabList> lTabLists) {
        this.lTabLists = lTabLists;
    }
}
