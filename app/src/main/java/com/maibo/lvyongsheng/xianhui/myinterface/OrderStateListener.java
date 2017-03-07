package com.maibo.lvyongsheng.xianhui.myinterface;

/**
 * Created by LYS on 2017/3/3.
 */

public interface OrderStateListener {
    void startOrder(int position,int schedule_id);
    void cancelOrder(int position,int schedule_id);
    void finishOrder(int position,int schedule_id);
}
