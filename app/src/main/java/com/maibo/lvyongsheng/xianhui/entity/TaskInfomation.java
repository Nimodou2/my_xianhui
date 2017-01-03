package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LYS on 2016/11/24.
 */

public class TaskInfomation implements Serializable{
    private String task_id;
    private String start_date;
    private String end_date;
    private String publish_date;
    private String type;
    private String range;
    private String percentage;
    private String is_update;
    private String target;
    private String range_name;
    private String type_name;

    private String org_id;
    private String reality;

    private List<People> adviser_list;
    private List<People> engineer_list;
    private List<People> customer_list;

    private Task type_task;
    private Task range_task;
    private List<Task> user_list;
    private String note;



    public TaskInfomation() {}

    public TaskInfomation(String task_id, Task type_task, Task range_task,
                          String target, String start_date, String end_date,
                          List<Task> user_list, String publish_date, String note) {
        this.task_id = task_id;
        this.type_task = type_task;
        this.range_task = range_task;
        this.target = target;
        this.start_date = start_date;
        this.end_date = end_date;
        this.user_list = user_list;
        this.publish_date = publish_date;
        this.note = note;
    }

    public TaskInfomation(String task_id, String start_date, String end_date, String publish_date,
                          String type, String range, String percentage, String is_update,
                          String target, String range_name, String type_name, String org_id, String reality) {
        this.task_id = task_id;
        this.start_date = start_date;
        this.end_date = end_date;
        this.publish_date = publish_date;
        this.type = type;
        this.range = range;
        this.percentage = percentage;
        this.is_update = is_update;
        this.target = target;
        this.range_name = range_name;
        this.type_name = type_name;
        this.org_id = org_id;
        this.reality = reality;
    }

    public TaskInfomation(String task_id, String start_date, String end_date,
                          String publish_date, String type, String range, String percentage,
                          String is_update, String target, String range_name, String type_name,
                          String org_id, String reality, List<People> adviser_list,
                          List<People> engineer_list, List<People> customer_list) {
        this.task_id = task_id;
        this.start_date = start_date;
        this.end_date = end_date;
        this.publish_date = publish_date;
        this.type = type;
        this.range = range;
        this.percentage = percentage;
        this.is_update = is_update;
        this.target = target;
        this.range_name = range_name;
        this.type_name = type_name;
        this.org_id = org_id;
        this.reality = reality;
        this.adviser_list = adviser_list;
        this.engineer_list = engineer_list;
        this.customer_list = customer_list;
    }

    public TaskInfomation(String task_id, String start_date, String end_date,
                          String publish_date, String type, String range, String percentage,
                          String is_update, String target, String range_name, String type_name) {
        this.task_id = task_id;
        this.start_date = start_date;
        this.end_date = end_date;
        this.publish_date = publish_date;
        this.type = type;
        this.range = range;
        this.percentage = percentage;
        this.is_update = is_update;
        this.target = target;
        this.range_name = range_name;
        this.type_name = type_name;
    }

    public Task getType_task() {
        return type_task;
    }

    public void setType_task(Task type_task) {
        this.type_task = type_task;
    }

    public Task getRange_task() {
        return range_task;
    }

    public void setRange_task(Task range_task) {
        this.range_task = range_task;
    }

    public List<Task> getUser_list() {
        return user_list;
    }

    public void setUser_list(List<Task> user_list) {
        this.user_list = user_list;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrg_id() {
        return org_id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }

    public String getReality() {
        return reality;
    }

    public void setReality(String reality) {
        this.reality = reality;
    }

    public List<People> getAdviser_list() {
        return adviser_list;
    }

    public void setAdviser_list(List<People> adviser_list) {
        this.adviser_list = adviser_list;
    }

    public List<People> getEngineer_list() {
        return engineer_list;
    }

    public void setEngineer_list(List<People> engineer_list) {
        this.engineer_list = engineer_list;
    }

    public List<People> getCustomer_list() {
        return customer_list;
    }

    public void setCustomer_list(List<People> customer_list) {
        this.customer_list = customer_list;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getIs_update() {
        return is_update;
    }

    public void setIs_update(String is_update) {
        this.is_update = is_update;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getRange_name() {
        return range_name;
    }

    public void setRange_name(String range_name) {
        this.range_name = range_name;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }
}
