package com.maibo.lvyongsheng.xianhui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.leancloud.chatkit.activity.LCIMConversationListFragment;

/**
 * Created by LYS on 2016/11/8.
 */
public class ConversationFragment extends LCIMConversationListFragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_title, container, false);
        return super.onCreateView(inflater,container,savedInstanceState);
        /*super.onCreateView(inflater,container,savedInstanceState)*/
    }
}
