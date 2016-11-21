package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LYS on 2016/9/13.
 */
public class CustemProjects implements Serializable {
    private List<Project> list;
    private List<Consume> consumes;
    private int[] selected;
    private int total;

    public CustemProjects() {}

    public CustemProjects(List<Project> list, List<Consume> consumes, int[] selected, int total) {
        this.list = list;
        this.consumes = consumes;
        this.selected = selected;
        this.total = total;
    }

    public List<Project> getList() {
        return list;
    }

    public void setList(List<Project> list) {
        this.list = list;
    }

    public List<Consume> getConsumes() {
        return consumes;
    }

    public void setConsumes(List<Consume> consumes) {
        this.consumes = consumes;
    }

    public int[] getSelected() {
        return selected;
    }

    public void setSelected(int[] selected) {
        this.selected = selected;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
