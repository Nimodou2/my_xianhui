package com.maibo.lvyongsheng.xianhui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.maibo.lvyongsheng.xianhui.adapter.GroupChatAdapter;
import com.maibo.lvyongsheng.xianhui.entity.SearchPeople;
import com.maibo.lvyongsheng.xianhui.myinterface.OnLetterTouchListener;
import com.maibo.lvyongsheng.xianhui.utils.ZzLetterSideBar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.utils.LCIMConstants;
import cn.leancloud.chatkit.view.LCIMDividerItemDecoration;

/**
 * Created by LYS on 2017/3/2.
 */

public class CreateGroupChatActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.et_search)
    SearchView et_search;
    @Bind(R.id.tv_group_name)
    TextView tv_group_name;
    @Bind(R.id.rl_group)
    RecyclerView rl_group;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.back)
    TextView back;
    @Bind(R.id.tv_new_build)
    TextView tv_new_build;
    @Bind(R.id.hsv)
    HorizontalScrollView hsv;
    @Bind(R.id.tv_dialog)
    TextView tv_dialog;
    @Bind(R.id.sidebar)
    ZzLetterSideBar sidebar;

    //用户系统
    List<LCChatKitUser> allUser;
    SharedPreferences sp;
    LinearLayoutManager layoutManager;
    //适配器
    GroupChatAdapter adapter;
    //所有用户姓名集合
    List<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_chat);
        //初始化View
        initView();
        //初始化监听器
        initListener();
        //初始化数据
        initDatas();
        //初始化适配器
        initAdapter();
    }

    /**
     * 初始化View
     */
    private void initView() {
        adapterLitterBar(ll_head);
        int id = et_search.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView hintText = (TextView) findViewById(id);
        hintText.setTextSize(14);
        hintText.setTextColor(Color.BLACK);
        Class<?> c = et_search.getClass();
        try {
            Field f = c.getDeclaredField("mSearchPlate");//通过反射，获得类对象的一个属性对象
            f.setAccessible(true);//设置此私有属性是可访问的
            View v = (View) f.get(et_search);//获得属性的值
//            v.setBackgroundResource(R.drawable.searchview_shap_all_white_bg);//设置此view的背景
            v.setBackgroundColor(Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        back.setOnClickListener(this);
        tv_new_build.setOnClickListener(this);
        sidebar.setLetterTouchListener(new MyLettertouchListener());
        et_search.setOnQueryTextListener(new MyQueryTextListener());
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        String guid = sp.getString("guid", null);
        //用户体系里的所有用户
        allUser = CustomUserProvider.getInstance().getAllUsers();
        Iterator<LCChatKitUser> iterator = allUser.iterator();
        if (TextUtils.isEmpty(guid) || allUser == null) {
            showToast("数据异常,返回重试");
            return;
        }
        while (iterator.hasNext()) {
            LCChatKitUser user = iterator.next();
            if (user.getUserId().equals(guid)) {
                iterator.remove();
            }
        }
        names = new ArrayList<>();
        for (LCChatKitUser ll : allUser) {
            names.add(ll.getUserName());
        }

    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        layoutManager = new LinearLayoutManager(this);
        rl_group.setLayoutManager(layoutManager);
        rl_group.addItemDecoration(new LCIMDividerItemDecoration(this));
        adapter = new GroupChatAdapter(this, allUser, viewHeight / 35);
        rl_group.setAdapter(adapter);
    }

    /**
     * 处理点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_new_build:
                List<String> groupUser = adapter.getGroupUser();
                if (groupUser == null || groupUser.size() < 2) {
                    showToast("至少选中两个人");
                } else {
                    showAlertDialogForName(groupUser);
                }
                break;

        }
    }

    /**
     * 输入群名的dialog
     */
    private void showAlertDialogForName(final List<String> groupUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_group, null);
        final EditText et_name = (EditText) view.findViewById(R.id.et_name);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();
        view.findViewById(R.id.btn_negative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.btn_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建群聊
                String groupName = et_name.getText().toString().trim();
                if (!TextUtils.isEmpty(groupName)) {
                    createGroup(groupUser, groupName);
                    finish();
                } else {
                    showToast("无效的名称");
                }
            }
        });
    }

    /**
     * 创建群聊
     *
     * @param groupUser
     * @param groupName
     */
    private void createGroup(List<String> groupUser, String groupName) {
        LCChatKit.getInstance().getClient().createConversation(
                groupUser, groupName, null, false, true, new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation avimConversation, AVIMException e) {
                        Intent intent = new Intent(CreateGroupChatActivity.this, LCIMConversationActivity.class);
                        intent.putExtra(LCIMConstants.CONVERSATION_ID, avimConversation.getConversationId());
                        startActivity(intent);
                    }
                });
    }

    /**
     * 导航条
     */
    public class MyLettertouchListener implements OnLetterTouchListener {

        @Override
        public void onLetterTouch(String letter, int position) {
            if (letter.equals("↑")) {
                layoutManager.scrollToPositionWithOffset(0, 0);
            } else {
                layoutManager.scrollToPositionWithOffset(adapter.getScrollPosition(letter), 0);
            }
            tv_dialog.setVisibility(View.VISIBLE);
            tv_dialog.setText(letter);
        }

        @Override
        public void onActionUp() {
            tv_dialog.setVisibility(View.GONE);
        }
    }

    /**
     * 搜索监听器
     */
    public class MyQueryTextListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (TextUtils.isEmpty(newText)) {
                //还原适配器
                adapter.setDatas(allUser);
            } else {
                //改变数据更新适配器
                List<SearchPeople> searchPeople=queryDatas(newText);
                setQueryAdapter(searchPeople);
            }
            return false;
        }
    }

    /**
     * 数据查询
     *
     * @param newText
     * @return
     */
    private List<SearchPeople> queryDatas(String newText) {
        List<SearchPeople> searchPeoples = new ArrayList<SearchPeople>();
        String[] text = new String[newText.length()];
        for (int j = 0; j < newText.length(); j++) {
            text[j] = String.valueOf(newText.charAt(j));
        }

        for (int i = 0; i < names.size(); i++) {
            int times = 0;
            for (int p = 0; p < text.length; p++) {
                int isContent = names.get(i).indexOf(text[p]);
                if (isContent != -1) {
                    //记录下来当前i值和进入该判断语句的次数
                    times++;
                }
            }
            searchPeoples.add(new SearchPeople(times, i));
        }
        return searchPeoples;
    }

    /**
     * 提供Adapter数据
     *
     * @param searchPeoples
     */
    private void setQueryAdapter(List<SearchPeople> searchPeoples) {
        //处理数据，得到适配器所需数据
        List<LCChatKitUser> lcChatKitUsers = new ArrayList<>();
        List<Integer> times = new ArrayList<>();
        //找出times中最大值
        int max = 0;
        for (int i = 0; i < searchPeoples.size(); i++) {
            if (searchPeoples.get(i).getTimes() > max) {
                max = searchPeoples.get(i).getTimes();
            }
        }

        for (int i = 0; i < searchPeoples.size(); i++) {
            if (searchPeoples.get(i).getTimes() > 0 && searchPeoples.get(i).getTimes() == max) {
                lcChatKitUsers.add(allUser.get(searchPeoples.get(i).getPosition()));
                times.add(searchPeoples.get(i).getTimes());
            }
        }
        adapter.setDatas(lcChatKitUsers);
    }
}
