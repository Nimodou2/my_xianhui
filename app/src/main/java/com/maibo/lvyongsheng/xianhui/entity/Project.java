package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LYS on 2016/9/13.
 */
public class Project implements Serializable {
    private List<Card> card_list;
    private int item_id;
    private String fullname;
    private int item_type;
    private String amount;
    //新增
    private int project_id;
    private String schedule_num;
    private String paid_num;
    private String project_name;
    private String org_name;
    //新增
    private String project_code;
    private String brand_name;
    private int card_total;
    private String satisfy_rate;
    //新增
    private String project_type;
    private String project_class;
    private String retail_price;
    private String hours;
    private String op_type;
    private List<Material> formula;
    //新增
    private String report_ratio;
    private String manual_type;
    private String manual_fee;
    //新增
    private String[] org_list;
    private String[] fee_type;
    private String card_discount;
    private String[] vipcard_type;
    private String avator_url;
    private String date;

    public String getAvator_url() {
        return avator_url;
    }

    public void setAvator_url(String avator_url) {
        this.avator_url = avator_url;
    }

    public List<Material> getFormula() {
        return formula;
    }

    public void setFormula(List<Material> formula) {
        this.formula = formula;
    }

    public Project() {}

    public Project(String[] org_list, String[] fee_type, String card_discount, String[] vipcard_type) {
        this.org_list = org_list;
        this.fee_type = fee_type;
        this.card_discount = card_discount;
        this.vipcard_type = vipcard_type;
    }

    public String[] getOrg_list() {
        return org_list;
    }

    public void setOrg_list(String[] org_list) {
        this.org_list = org_list;
    }

    public String[] getFee_type() {
        return fee_type;
    }

    public void setFee_type(String[] fee_type) {
        this.fee_type = fee_type;
    }

    public String getCard_discount() {
        return card_discount;
    }

    public void setCard_discount(String card_discount) {
        this.card_discount = card_discount;
    }

    public String[] getVipcard_type() {
        return vipcard_type;
    }

    public void setVipcard_type(String[] vipcard_type) {
        this.vipcard_type = vipcard_type;
    }

    public Project(String report_ratio, String manual_type, String manual_fee) {
        this.report_ratio = report_ratio;
        this.manual_type = manual_type;
        this.manual_fee = manual_fee;
    }

    public String getReport_ratio() {
        return report_ratio;
    }

    public void setReport_ratio(String report_ratio) {
        this.report_ratio = report_ratio;
    }

    public String getManual_type() {
        return manual_type;
    }

    public void setManual_type(String manual_type) {
        this.manual_type = manual_type;
    }

    public String getManual_fee() {
        return manual_fee;
    }

    public void setManual_fee(String manual_fee) {
        this.manual_fee = manual_fee;
    }

    public Project(String project_type, String project_class,
                   String retail_price, String hours, String op_type,List<Material> formula) {
        this.project_type = project_type;
        this.project_class = project_class;
        this.retail_price = retail_price;
        this.hours = hours;
        this.op_type = op_type;
        this.formula=formula;
    }

    public String getProject_type() {
        return project_type;
    }

    public void setProject_type(String project_type) {
        this.project_type = project_type;
    }

    public String getProject_class() {
        return project_class;
    }

    public void setProject_class(String project_class) {
        this.project_class = project_class;
    }

    public String getRetail_price() {
        return retail_price;
    }

    public void setRetail_price(String retail_price) {
        this.retail_price = retail_price;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getOp_type() {
        return op_type;
    }

    public void setOp_type(String op_type) {
        this.op_type = op_type;
    }

    public Project(String fullname, String brand_name, int card_total, String satisfy_rate, String project_code,String avator_url) {
        this.fullname = fullname;
        this.brand_name = brand_name;
        this.card_total = card_total;
        this.satisfy_rate = satisfy_rate;
        this.project_code = project_code;
        this.avator_url=avator_url;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public int getCard_total() {
        return card_total;
    }

    public void setCard_total(int card_total) {
        this.card_total = card_total;
    }

    public String getSatisfy_rate() {
        return satisfy_rate;
    }

    public void setSatisfy_rate(String satisfy_rate) {
        this.satisfy_rate = satisfy_rate;
    }

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
    }

    public Project(String avator_url,int project_id, String schedule_num, String paid_num, String project_name, String org_name) {
       this.avator_url=avator_url;
        this.project_id = project_id;
        this.schedule_num = schedule_num;
        this.paid_num = paid_num;
        this.project_name = project_name;
        this.org_name = org_name;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public String getSchedule_num() {
        return schedule_num;
    }

    public void setSchedule_num(String schedule_num) {
        this.schedule_num = schedule_num;
    }

    public String getPaid_num() {
        return paid_num;
    }

    public void setPaid_num(String paid_num) {
        this.paid_num = paid_num;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public Project(String amount, String fullname) {
        this.amount = amount;
        this.fullname = fullname;
    }

    public Project(List<Card> card_list, int item_id, String fullname, int item_type,String date) {
        this.card_list = card_list;
        this.item_id = item_id;
        this.fullname = fullname;
        this.item_type = item_type;
        this.date=date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Card> getCard_list() {
        return card_list;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCard_list(List<Card> card_list) {
        this.card_list = card_list;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getItem_type() {
        return item_type;
    }

    public void setItem_type(int item_type) {
        this.item_type = item_type;
    }
}
