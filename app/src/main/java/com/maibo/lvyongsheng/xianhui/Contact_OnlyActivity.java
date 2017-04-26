package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.adapter.Contact_Recyclerview_Adapter;
import com.maibo.lvyongsheng.xianhui.implement.Util;
import com.maibo.lvyongsheng.xianhui.myinterface.OnLetterTouchListener;
import com.maibo.lvyongsheng.xianhui.utils.ZzLetterSideBar;

import java.util.List;

import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.view.LCIMDividerItemDecoration;

public class Contact_OnlyActivity extends AppCompatActivity {
    private TextView text_back;
    private TextView text_dialog;
    private SwipeRefreshLayout srl_list;
    private RecyclerView recyclerview;
    private ZzLetterSideBar zzsidebar;
    private SharedPreferences sp;
    private LinearLayoutManager linearLayoutManager;
    private Contact_Recyclerview_Adapter contact_adapter;
    private int viewHeight;
    private LinearLayout linearhead_activity;
    private List<LCChatKitUser> list_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact__only);
        initview();
        initdate();
    }
    private void initview() {
        text_back = (TextView) findViewById(R.id.contact_activity_text_back);
        text_dialog = (TextView) findViewById(R.id.contact_activity_text_dialog);
        srl_list = (SwipeRefreshLayout) findViewById(R.id.contact_activity_srl_list);
        recyclerview = (RecyclerView) findViewById(R.id.contact_activity_recyclerview_list);
        zzsidebar = (ZzLetterSideBar) findViewById(R.id.contact_activity_sidebar);
        linearhead_activity = (LinearLayout) findViewById(R.id.contact_activity_headlinearlayout);

        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        linearLayoutManager=new LinearLayoutManager(this);
        viewHeight= (Util.getScreenHeight(this) - getStatusBarHeight()) / 35;
        /*LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearhead_activity.getLayoutParams();
        params.height = viewHeight * 2;
        linearhead_activity.setLayoutParams(params);*/
    }
    private void initdate() {
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.addItemDecoration(new LCIMDividerItemDecoration(this));
        list_user= CustomUserProvider.getInstance().getAllUsers();
        contact_adapter= new Contact_Recyclerview_Adapter(this, list_user, sp.getString("displayname", null), viewHeight);
        recyclerview.setAdapter(contact_adapter);
        //设置颜色
        srl_list.setColorSchemeResources(R.color.colorLightYellow,
                R.color.colorLightYellow, R.color.colorLightYellow, R.color.colorLightYellow);
        srl_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMembers();
            }
        });
        zzsidebar.setLetterTouchListener(new OnLetterTouchListener() {
            @Override
            public void onLetterTouch(String letter, int position) {
                if (letter.equals("↑")) {
                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                } else {
                    linearLayoutManager.scrollToPositionWithOffset(contact_adapter.getScrollPosition(letter), 0);
                }
                text_dialog.setVisibility(View.VISIBLE);
                text_dialog.setText(letter);
            }

            @Override
            public void onActionUp() {
                text_dialog.setVisibility(View.GONE);
            }
        });

        text_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        int isChangeDoor = sp.getInt("isChangeDoor", -1);
        if (isChangeDoor == 1) {

            refreshMembers();
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("isChangeDoor", 0);
            editor.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    //swipeRefreshLayout用于数据刷新
    private void refreshMembers() {
        //更新成员列表
        List<LCChatKitUser> list = CustomUserProvider.getInstance().getAllUsers();
        String displayname = sp.getString("displayname", null);
        if (displayname != null) {
            contact_adapter = new Contact_Recyclerview_Adapter(this, list, displayname, viewHeight);
            recyclerview.setAdapter(contact_adapter);
            srl_list.setRefreshing(false);
        }
    }
}
