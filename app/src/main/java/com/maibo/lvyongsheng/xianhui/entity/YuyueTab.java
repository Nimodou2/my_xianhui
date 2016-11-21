package com.maibo.lvyongsheng.xianhui.entity;

/**
 * Created by LYS on 2016/10/27.
 */
public class YuyueTab {
    private String schedule_id;
    private String adate;
    private String start_time;
    private String end_time;
    private String item_id;
    private String fullname;
    private String engineer_id;
    private String engineer_name;
    private String status;

    public YuyueTab(String schedule_id, String adate, String start_time, String end_time,
                    String item_id, String fullname, String engineer_id, String engineer_name, String status) {
        this.schedule_id = schedule_id;
        this.adate = adate;
        this.start_time = start_time;
        this.end_time = end_time;
        this.item_id = item_id;
        this.fullname = fullname;
        this.engineer_id = engineer_id;
        this.engineer_name = engineer_name;
        this.status = status;
    }

    public String getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(String schedule_id) {
        this.schedule_id = schedule_id;
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

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEngineer_id() {
        return engineer_id;
    }

    public void setEngineer_id(String engineer_id) {
        this.engineer_id = engineer_id;
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
}
