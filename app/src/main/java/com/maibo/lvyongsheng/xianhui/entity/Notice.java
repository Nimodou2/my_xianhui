package com.maibo.lvyongsheng.xianhui.entity;

/**
 * Created by LYS on 2016/9/18.
 */
public class Notice {

    /**
     * notice_id : 10914
     * notice_type : daily_report
     * subject : 日报表提醒
     * body : 请查看今日[海景]店销售日报表
     * create_time : 2017-03-26 21:00:00
     * status : 0
     * extra_id : 2017-03-26
     * org_id : 4
     * org_name : 海景
     * week : 0
     * week_text : 周日
     * noticetype : daily_report
     */
    private String extra_type;
    private String customer_id;
    private int notice_id;
    private String notice_type;
    private String subject;
    private String body;
    private String create_time;
    private int status;
    private String extra_id;
    private int org_id;
    private String org_name;
    private String week;
    private String week_text;

    public Notice(int notice_id, String notice_type, String subject, String body, String create_time, int status, String extra_id, int org_id, String org_name, String week, String week_text, String noticetype) {
        this.notice_id = notice_id;
        this.notice_type = notice_type;
        this.subject = subject;
        this.body = body;
        this.create_time = create_time;
        this.status = status;
        this.extra_id = extra_id;
        this.org_id = org_id;
        this.org_name = org_name;
        this.week = week;
        this.week_text = week_text;
        this.noticetype = noticetype;
    }

    private String noticetype;




    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }



    public void setOrg_id(int org_id) {
        this.org_id = org_id;
    }


    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getWeek_text() {
        return week_text;
    }

    public void setWeek_text(String week_text) {
        this.week_text = week_text;
    }

    public String getNoticetype() {
        return noticetype;
    }

    public void setNoticetype(String noticetype) {
        this.noticetype = noticetype;
    }





    public Notice() {}

    public Notice(int notice_id, String notice_type, String subject, String body,
                  String create_time, int status, String extra_id, String org_name,
                  int org_id, String extra_type, String customer_id) {
        this.notice_id = notice_id;
        this.notice_type = notice_type;
        this.subject = subject;
        this.body = body;
        this.create_time = create_time;
        this.status = status;
        this.extra_id = extra_id;
        this.org_name = org_name;
        this.org_id = org_id;
        this.extra_type = extra_type;
        this.customer_id = customer_id;
    }

    public Notice(int notice_id, String notice_type, String subject, String body,
                  String create_time, int status, String extra_id, String org_name, int org_id) {
        this.notice_id = notice_id;
        this.notice_type = notice_type;
        this.subject = subject;
        this.body = body;
        this.create_time = create_time;
        this.status = status;
        this.extra_id = extra_id;
        this.org_name=org_name;
        this.org_id=org_id;
    }

    public String getExtra_type() {
        return extra_type;
    }

    public void setExtra_type(String extra_type) {
        this.extra_type = extra_type;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public int getOrg_id() {
        return org_id;
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
