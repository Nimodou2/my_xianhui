package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;

/**
 * Created by LYS on 2016/9/8.
 */
public class Employee implements Serializable{
    private int user_id;
    private String username;
    private String display_name;
    private String avator_url;
    private String guid;

    //新增
    private String job;
    private boolean user_level;
    private String mobile;
    private String user_code;
    private String entry_date;
    private String org_name;
    private int org_id;
    /*private int user_id;
    private String display_name;
    private String avator_url;
    private String guid;*/
    private int status;
    private double project_hours;
    private int project_qty;
    private String schedule_time;

    public Employee(int user_id,String display_name, String avator_url, String job, boolean user_level, String mobile,
                    String user_code, String entry_date, String org_name, int status, double project_hours, int project_qty, String schedule_time) {
        this.user_id=user_id;
        this.display_name = display_name;
        this.avator_url = avator_url;
        this.job = job;
        this.user_level = user_level;
        this.mobile = mobile;
        this.user_code = user_code;
        this.entry_date = entry_date;
        this.org_name = org_name;
        this.status = status;
        this.project_hours = project_hours;
        this.project_qty = project_qty;
        this.schedule_time = schedule_time;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public boolean isUser_level() {
        return user_level;
    }

    public void setUser_level(boolean user_level) {
        this.user_level = user_level;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public int getOrg_id() {
        return org_id;
    }

    public void setOrg_id(int org_id) {
        this.org_id = org_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getProject_hours() {
        return project_hours;
    }

    public void setProject_hours(double project_hours) {
        this.project_hours = project_hours;
    }

    public int getProject_qty() {
        return project_qty;
    }

    public void setProject_qty(int project_qty) {
        this.project_qty = project_qty;
    }

    public String getSchedule_time() {
        return schedule_time;
    }

    public void setSchedule_time(String schedule_time) {
        this.schedule_time = schedule_time;
    }

    public Employee(int user_id, String display_name) {
        this.display_name = display_name;
        this.user_id=user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public void setAvator_url(String avator_url) {
        this.avator_url = avator_url;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getUser_id() {
        return user_id;

    }

    public String getUsername() {
        return username;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getAvator_url() {
        return avator_url;
    }

    public String getGuid() {
        return guid;
    }


}
