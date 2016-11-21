package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;

/**
 * Created by LYS on 2016/9/18.
 */
public class Radar implements Serializable{

    private int project;
    private int cash;
    private int product;
    private int customer;
    private int employee;
    private String name;
    private int amount;
    private int score;
    private String date;
    public Radar(String name, int amount,int score) {
        this.name = name;
        this.amount = amount;
        this.score=score;
    }

    public Radar(String name, int amount, int score, String date) {
        this.name = name;
        this.amount = amount;
        this.score = score;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Radar(int project, int cash, int product, int customer, int employee) {
        this.project = project;
        this.cash = cash;
        this.product = product;
        this.customer = customer;
        this.employee = employee;
    }

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public int getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public int getEmployee() {
        return employee;
    }

    public void setEmployee(int employee) {
        this.employee = employee;
    }
}
