package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;

/**
 * Created by LYS on 2016/10/9.
 */
public class Order implements Serializable{
    private String flowno;
    private int cstid;
    private String engineer_id;
    private int item_id;
    private String item_name;
    private String qty;
    private String customer_name;
    private String engineer_name;
    private String status;
    //新增
    private int schedule_id;
    private int customer_id;
    private String start_time;
    private String end_time;
    private String bed_name;
    private String project_code;
    private String project_name;
    private String avator_url;
    private String guid;
    private String org_name;
    private int raw_status;

    public Order(int schedule_id, int customer_id, String status, String start_time, String end_time, String engineer_id, String bed_name,
                 String project_code, String project_name, String customer_name, String engineer_name,String org_name,int raw_status) {
        this.schedule_id = schedule_id;
        this.customer_id = customer_id;
        this.status = status;
        this.start_time = start_time;
        this.end_time = end_time;
        this.engineer_id = engineer_id;
        this.bed_name = bed_name;
        this.project_code = project_code;
        this.project_name = project_name;
        this.customer_name = customer_name;
        this.engineer_name = engineer_name;
        this.org_name=org_name;
        this.raw_status=raw_status;

    }

    public Order(int schedule_id, String status, String start_time, String end_time, String engineer_id,
                 String bed_name, String project_code, String project_name, String avator_url, String guid,
                 int customer_id, String customer_name, String engineer_name,String org_name,int raw_status) {

        this.customer_id = customer_id;
        this.status = status;
        this.start_time = start_time;
        this.end_time = end_time;
        this.engineer_id = engineer_id;
        this.bed_name = bed_name;
        this.project_code = project_code;
        this.project_name = project_name;
        this.avator_url=avator_url;
        this.guid=guid;
        this.schedule_id = schedule_id;
        this.customer_name = customer_name;
        this.engineer_name = engineer_name;
        this.org_name=org_name;
        this.raw_status=raw_status;

    }
    public Order(String flowno, int cstid, String engineer_id,
                 int item_id, String item_name, String qty, String customer_name, String status) {
        this.flowno = flowno;
        this.cstid = cstid;
        this.engineer_id = engineer_id;
        this.item_id = item_id;
        this.item_name = item_name;
        this.qty = qty;
        this.customer_name = customer_name;
        this.status = status;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public int getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(int schedule_id) {
        this.schedule_id = schedule_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
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

    public String getBed_name() {
        return bed_name;
    }

    public void setBed_name(String bed_name) {
        this.bed_name = bed_name;
    }

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getFlowno() {
        return flowno;
    }

    public void setFlowno(String flowno) {
        this.flowno = flowno;
    }

    public int getCstid() {
        return cstid;
    }

    public void setCstid(int cstid) {
        this.cstid = cstid;
    }

    public String getEngineer_id() {
        return engineer_id;
    }

    public void setEngineer_id(String engineer_id) {
        this.engineer_id = engineer_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getEngineer_name() {
        return engineer_name;
    }

    public void setEngineer_name(String engineer_name) {
        this.engineer_name = engineer_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRaw_status() {
        return raw_status;
    }

    public void setRaw_status(int raw_status) {
        this.raw_status = raw_status;
    }
}
