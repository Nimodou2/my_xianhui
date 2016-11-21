package com.maibo.lvyongsheng.xianhui.entity;

import java.util.List;

/**
 * Created by LYS on 2016/10/19.
 */
public class SelectEntitys {
    private String name;
    private String param;
    private String filterResult;
    private List<SelectEntity> list;

    public SelectEntitys(String name, String param,String filterResult,List<SelectEntity> list) {
        this.name = name;
        this.param=param;
        this.filterResult=filterResult;
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public List<SelectEntity> getList() {
        return list;
    }

    public String getFilterResult() {
        return filterResult;
    }

    public void setFilterResult(String filterResult) {
        this.filterResult = filterResult;
    }

    public void setList(List<SelectEntity> list) {
        this.list = list;
    }
}
