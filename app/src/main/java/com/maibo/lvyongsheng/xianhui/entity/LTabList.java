package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;

/**
 * Created by LYS on 2016/9/18.
 */
public class LTabList implements Serializable {

    private String amount;
    private String fullname;
    private int chartAmount;
    //饼状图的数据
    public LTabList(String fullname, int chartAmount) {
        this.fullname = fullname;
        this.chartAmount = chartAmount;
    }

    public int getChartAmount() {
        return chartAmount;
    }

    public void setChartAmount(int chartAmount) {
        this.chartAmount = chartAmount;
    }

    public LTabList(String fullname, String amount) {
        this.amount = amount;
        this.fullname = fullname;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
