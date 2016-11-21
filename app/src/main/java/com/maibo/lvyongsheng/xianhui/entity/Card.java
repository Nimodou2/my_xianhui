package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;

/**
 * Created by LYS on 2016/9/13.
 */
public class Card implements Serializable{
    private String fullname;
    private int times;
    private String card_class;
    private String card_num;
    private String price;
    private int item_id;
    private String amount;
    private int amounts;

    public Card(String fullname,int amounts) {
        this.amounts = amounts;
        this.fullname = fullname;
    }

    public Card(String fullname, int times, String amount) {
        this.fullname = fullname;
        this.times = times;
        this.amount = amount;
    }

    public int getAmounts() {
        return amounts;
    }

    public void setAmounts(int amounts) {
        this.amounts = amounts;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Card(String fullname, int times, String card_class, String card_num, String price, int item_id) {
        this.fullname = fullname;
        this.times = times;
        this.card_class = card_class;
        this.card_num = card_num;
        this.price = price;
        this.item_id = item_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int  getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getCard_class() {
        return card_class;
    }

    public void setCard_class(String card_class) {
        this.card_class = card_class;
    }

    public String getCard_num() {
        return card_num;
    }

    public void setCard_num(String card_num) {
        this.card_num = card_num;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }
}
