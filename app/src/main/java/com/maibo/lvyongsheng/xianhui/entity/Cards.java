package com.maibo.lvyongsheng.xianhui.entity;

import java.util.List;

/**
 * Created by LYS on 2016/10/6.
 */
public class Cards {
    private int card_sort;
    private String card_class;
    private String amount;
    private String card_num;
    private int item_id;
    private String fullname;
    private List<Card> card;

    public Cards(int card_sort, String card_class, String amount, String card_num,
                 int item_id, String fullname, List<Card> card) {
        this.card_sort = card_sort;
        this.card_class = card_class;
        this.amount = amount;
        this.card_num = card_num;
        this.item_id = item_id;
        this.fullname = fullname;
        this.card = card;
    }

    public int getCard_sort() {
        return card_sort;
    }

    public void setCard_sort(int card_sort) {
        this.card_sort = card_sort;
    }

    public String getCard_class() {
        return card_class;
    }

    public void setCard_class(String card_class) {
        this.card_class = card_class;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCard_num() {
        return card_num;
    }

    public void setCard_num(String card_num) {
        this.card_num = card_num;
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

    public List<Card> getCard() {
        return card;
    }

    public void setCard(List<Card> card) {
        this.card = card;
    }
}
