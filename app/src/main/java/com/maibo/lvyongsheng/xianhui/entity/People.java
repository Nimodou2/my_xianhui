package com.maibo.lvyongsheng.xianhui.entity;

import java.util.List;

/**
 * Created by LYS on 2016/11/25.
 */

public class People {
    private String id;
    private String name;
    private String amount;
    private List<Project> detail;

    public People() {}

    public People(String id, String name, String amount, List<Project> detail) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public List<Project> getDetail() {
        return detail;
    }

    public void setDetail(List<Project> detail) {
        this.detail = detail;
    }
}
