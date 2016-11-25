package com.maibo.lvyongsheng.xianhui.entity;

/**
 * Created by LYS on 2016/11/21.
 */

public class SearchPeople {
    private int times;
    private int position;

    public SearchPeople(int times, int position) {
        this.times = times;
        this.position = position;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
