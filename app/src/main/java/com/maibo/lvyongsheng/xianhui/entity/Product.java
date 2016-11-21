package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LYS on 2016/9/13.
 */
public class Product implements Serializable{
    private int item_id;
    private String fullname;
    private int item_type;
    private List<Card> card_list;
    private String amount;
    //新增
    private String buy_qty;
    private String buy_num;
    private String stock_qty;
    private String org_name;
    //新增
    private String brand_name;
    private String item_code;
    private String card_total;
    //新增
    private String item_class;
    private String spec;
    private String use_unit;
    private String use_spec;
    private String report_ratio;
    //新增
    /*org_list,fee_type,card_discount,vipcard_type*/
    private String[] org_list;
    private String[] fee_type;
    private String card_discount;
    private String[] vipcard_type;
    private String avator_url;

    public String getAvator_url() {
        return avator_url;
    }

    public void setAvator_url(String avator_url) {
        this.avator_url = avator_url;
    }

    public Product(String[] org_list, String[] fee_type, String card_discount, String[] vipcard_type) {
        this.org_list = org_list;
        this.fee_type = fee_type;
        this.card_discount = card_discount;
        this.vipcard_type = vipcard_type;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
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

    public String getReport_ratio() {
        return report_ratio;
    }

    public void setReport_ratio(String report_ratio) {
        this.report_ratio = report_ratio;
    }

    public String getItem_class() {
        return item_class;
    }

    public void setItem_class(String item_class) {
        this.item_class = item_class;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getUse_unit() {
        return use_unit;
    }

    public void setUse_unit(String use_unit) {
        this.use_unit = use_unit;
    }

    public String getUse_spec() {
        return use_spec;
    }

    public void setUse_spec(String use_spec) {
        this.use_spec = use_spec;
    }

    public Product(String fullname, String brand_name, String item_code, String card_total,String avator_url) {
        this.fullname = fullname;
        this.brand_name = brand_name;
        this.item_code = item_code;
        this.card_total = card_total;
        this.avator_url=avator_url;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getCard_total() {
        return card_total;
    }

    public void setCard_total(String card_total) {
        this.card_total = card_total;
    }

    public Product(String avator_url,int item_id, String fullname, String buy_qty, String buy_num, String stock_qty,String org_name) {
        this.avator_url=avator_url;
        this.item_id = item_id;
        this.fullname = fullname;
        this.buy_qty = buy_qty;
        this.buy_num = buy_num;
        this.stock_qty = stock_qty;
        this.org_name=org_name;
    }

    public String getBuy_qty() {
        return buy_qty;
    }

    public void setBuy_qty(String buy_qty) {
        this.buy_qty = buy_qty;
    }

    public String getBuy_num() {
        return buy_num;
    }

    public void setBuy_num(String buy_num) {
        this.buy_num = buy_num;
    }

    public String getStock_qty() {
        return stock_qty;
    }

    public void setStock_qty(String stock_qty) {
        this.stock_qty = stock_qty;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Product(String fullname, String amount) {
        this.fullname = fullname;
        this.amount = amount;
    }

    public Product(int item_id, String fullname, int item_type, List<Card> card_list) {
        this.item_id = item_id;
        this.fullname = fullname;
        this.item_type = item_type;
        this.card_list = card_list;
    }
    public Product(int item_id, String fullname, int item_type) {
        this.item_id = item_id;
        this.fullname = fullname;
        this.item_type = item_type;
    }

    public Product() {}

    public List<Card> getCard_list() {
        return card_list;
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
