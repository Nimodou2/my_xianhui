package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;

/**
 * Created by LYS on 2016/9/29.
 */
public class TabMax implements Serializable{


    private String name;
    private int min;
    private int max;
    private float step;
    private String unit;
    private float[] defaults;
    private float value;

    public TabMax(String name, int min, int max, float step, String unit, float[] defaults, float value) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.step = step;
        this.unit = unit;
        this.defaults = defaults;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public float getStep() {
        return step;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float[] getDefaults() {
        return defaults;
    }

    public void setDefaults(float[] defaults) {
        this.defaults = defaults;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
