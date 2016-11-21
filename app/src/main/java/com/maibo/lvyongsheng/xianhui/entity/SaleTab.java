package com.maibo.lvyongsheng.xianhui.entity;

/**
 * Created by LYS on 2016/10/6.
 */
public class SaleTab {
    private String saledate;
    private String qty;
    private String amount;
    private int item_id;
    private String fullname;

    public SaleTab(String saledate, String qty, String amount, int item_id, String fullname) {
        this.saledate = saledate;
        this.qty = qty;
        this.amount = amount;
        this.item_id = item_id;
        this.fullname = fullname;
    }

    public String getSaledate() {
        return saledate;
    }

    public void setSaledate(String saledate) {
        this.saledate = saledate;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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
}
