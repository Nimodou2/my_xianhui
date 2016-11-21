package com.maibo.lvyongsheng.xianhui;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import cn.leancloud.chatkit.activity.LCIMConversationListFragment;


/**
 * Created by LYS on 2016/10/17.
 */
public class AAAATestActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aaaatest);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //ConversationFragment conver = new ConversationFragment();
        LCIMConversationListFragment conver=new LCIMConversationListFragment();
        //此方法可动态添加
        transaction.replace(android.R.id.content, conver);
        //transaction.add(conver,"conver");
        transaction.commit();
    }

}
