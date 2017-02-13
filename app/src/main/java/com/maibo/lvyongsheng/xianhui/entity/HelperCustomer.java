package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;

/**
 * Created by LYS on 2016/10/3.
 */
//extends SugarRecord
public class HelperCustomer implements Serializable {
    private String org_name;
    private int org_id;
    private String vip_star;
    private int customer_id;
    private String fullname;
    private String avator_url;
    private String last_consume_time;
    private String guid;
    private String days;
    private int project_total;
    private String schedule_date;
    private String schedule_time;
    private int status;
    private Long current_time;
    private String schedule_status;
    //新增
    private int planed;
    private int card_total;
    private String manager;
    private String cert_no;

    public HelperCustomer(String org_name, String vip_star, int customer_id, String fullname,
                          String avator_url, String days, int project_total, int status,Long current_time) {
        this.org_name = org_name;
        this.vip_star = vip_star;
        this.customer_id = customer_id;
        this.fullname = fullname;
        this.avator_url = avator_url;
        this.days = days;
        this.project_total = project_total;
        this.status = status;
        this.current_time=current_time;
    }

    public HelperCustomer(String cert_no, String vip_star, int customer_id, String fullname, String avator_url, String guid,
                          String schedule_date, int planed, int card_total, String manager,String last_consume_time,String schedule_status) {
        this.cert_no=cert_no;
        this.vip_star = vip_star;
        this.customer_id = customer_id;
        this.fullname = fullname;
        this.avator_url = avator_url;
        this.guid = guid;
        this.schedule_date = schedule_date;
        this.planed = planed;
        this.card_total = card_total;
        this.manager = manager;
        this.last_consume_time=last_consume_time;
        this.schedule_status=schedule_status;
    }

    public String getSchedule_status() {
        return schedule_status;
    }

    public void setSchedule_status(String schedule_status) {
        this.schedule_status = schedule_status;
    }

    public Long getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(Long current_time) {
        this.current_time = current_time;
    }

    public String getCert_no() {
        return cert_no;
    }

    public void setCert_no(String cert_no) {
        this.cert_no = cert_no;
    }

    public int getPlaned() {
        return planed;
    }

    public void setPlaned(int planed) {
        this.planed = planed;
    }

    public int getCard_total() {
        return card_total;
    }

    public void setCard_total(int card_total) {
        this.card_total = card_total;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
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

    public String getVip_star() {
        return vip_star;
    }

    public void setVip_star(String vip_star) {
        this.vip_star = vip_star;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAvator_url() {
        return avator_url;
    }

    public void setAvator_url(String avator_url) {
        this.avator_url = avator_url;
    }

    public String getLast_consume_time() {
        return last_consume_time;
    }

    public void setLast_consume_time(String last_consume_time) {
        this.last_consume_time = last_consume_time;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public int getProject_total() {
        return project_total;
    }

    public void setProject_total(int project_total) {
        this.project_total = project_total;
    }

    public String getSchedule_date() {
        return schedule_date;
    }

    public void setSchedule_date(String schedule_date) {
        this.schedule_date = schedule_date;
    }

    public String getSchedule_time() {
        return schedule_time;
    }

    public void setSchedule_time(String schedule_time) {
        this.schedule_time = schedule_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
