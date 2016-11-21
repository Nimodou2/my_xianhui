package com.maibo.lvyongsheng.xianhui.entity;

import java.util.List;

/**
 * Created by LYS on 2016/9/13.
 */
public class CustemProducts {
    private List<Product> product;
    private List<Consume> consumes;
    private int[] selected;
    private int total;

    public CustemProducts() {}

    public CustemProducts(List<Product> product, List<Consume> consumes, int[] selected, int total) {
        this.product = product;
        this.consumes = consumes;
        this.selected = selected;
        this.total = total;
    }

    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }

    public List<Consume> getConsumes() {
        return consumes;
    }

    public void setConsumes(List<Consume> consumes) {
        this.consumes = consumes;
    }

    public int[] getSelected() {
        return selected;
    }

    public void setSelected(int[] selected) {
        this.selected = selected;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
