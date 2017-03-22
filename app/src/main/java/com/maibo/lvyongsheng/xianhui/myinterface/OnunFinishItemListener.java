package com.maibo.lvyongsheng.xianhui.myinterface;

/**
 * Created by LYS on 2017/3/9.
 */

public interface OnUnFinishItemListener {
    void onAdviseListener(int position);

    void onNextAdviserListener(int position, int customerID,String customerName);

    void onMyItemClickListener(int position, int customerID,String customerName);

    void onClickHeadListener(int position,int customerID);
}
