package com.maibo.lvyongsheng.xianhui.entity;

/**
 * Created by LYS on 2016/9/18.
 */
public class Notice {
    private int notice_id;
    private String notice_type;
    private String subject;
    private String body;
    private String creat_time;
    private int status;
    private String extra_id;
    private String org_name;
    public Notice() {}

    public Notice(int notice_id, String notice_type, String subject, String body,
                  String creat_time, int status, String extra_id,String org_name) {
        this.notice_id = notice_id;
        this.notice_type = notice_type;
        this.subject = subject;
        this.body = body;
        this.creat_time = creat_time;
        this.status = status;
        this.extra_id = extra_id;
        this.org_name=org_name;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public int getNotice_id() {
        return notice_id;
    }

    public void setNotice_id(int notice_id) {
        this.notice_id = notice_id;
    }

    public String getNotice_type() {
        return notice_type;
    }

    public void setNotice_type(String notice_type) {
        this.notice_type = notice_type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(String creat_time) {
        this.creat_time = creat_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getExtra_id() {
        return extra_id;
    }

    public void setExtra_id(String extra_id) {
        this.extra_id = extra_id;
    }
}
