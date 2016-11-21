package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;

/**
 * Created by LYS on 2016/9/12.
 */
public class Custemer implements Serializable{
    private String fullname;
    private  int customer_id;
    private  String avator_url;
    private  int guid;
    private String planed;
    private String status;
    private String adate;
    private String start_time;
    private String end_time;
    private Boolean selected;
    private String gu_id;
    private String update_time;

    public Custemer(int customer_id, String fullname, String avator_url, String gu_id, Boolean selected,String update_time) {
        this.customer_id = customer_id;
        this.fullname = fullname;
        this.avator_url = avator_url;
        this.gu_id = gu_id;
        this.selected = selected;
        this.update_time=update_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getGu_id() {
        return gu_id;
    }

    public void setGu_id(String gu_id) {
        this.gu_id = gu_id;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Custemer() {}

    public Custemer(String fullname, String status, String adate, String start_time, String end_time) {
        this.fullname = fullname;
        //this.customer_id=customer_id;
        this.status = status;
        this.adate = adate;
        this.start_time = start_time;
        this.end_time = end_time;
    }
    public Custemer(String avator_url,String fullname, int customer_id,String planed) {
        this.avator_url=avator_url;
        this.fullname = fullname;
        this.customer_id=customer_id;
        this.planed = planed;
    }

    public String getStatus() {

        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdate() {
        return adate;
    }

    public void setAdate(String adate) {
        this.adate = adate;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }



    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getAvator_url() {
        return avator_url;
    }

    public void setAvator_url(String avator_url) {
        this.avator_url = avator_url;
    }

    public int getGuid() {
        return guid;
    }

    public void setGuid(int guid) {
        this.guid = guid;
    }

    public String getPlaned() {
        return planed;
    }

    public void setPlaned(String planed) {
        this.planed = planed;
    }
}
