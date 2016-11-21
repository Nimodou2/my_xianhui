package com.maibo.lvyongsheng.xianhui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.entity.ActionItem;
import com.maibo.lvyongsheng.xianhui.implement.ConversationItemHolder;
import com.maibo.lvyongsheng.xianhui.implement.MyDividerItem;
import com.maibo.lvyongsheng.xianhui.implement.TitlePopup;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.adapter.LCIMCommonListAdapter;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import cn.leancloud.chatkit.event.LCIMConversationItemLongClickEvent;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.event.LCIMOfflineMessageCountChangeEvent;
import de.greenrobot.event.EventBus;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.Call;

/**
 * Created by LYS on 2016/9/9.
 */
public class ConversationListFragment extends Fragment implements View.OnClickListener {

    protected SwipeRefreshLayout refreshLayout;
    protected RecyclerView recyclerView;
    //protected Conversation itemAdapter;
    protected LCIMCommonListAdapter<AVIMConversation> itemAdapter;
    protected LinearLayoutManager layoutManager;
    SharedPreferences sp;
    String apiURL;
    String token;
    TextView scanner, tv_qute_pc;
    LinearLayout ll_scanner;
    ViewGroup contentView;
    //定义标题栏弹窗按钮
    private TitlePopup titlePopup;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //保存web令牌
                    String result = (String) msg.obj;
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("webtoken", result);
                    editor.commit();
                    //显示登录成功界面
                    tv_qute_pc.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.lcim_conversation_list_fragments,container,false);
        sp = getActivity().getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
        String jsonBody = sp.getString("jsonBody", null);
        String webToken = sp.getString("webtoken", null);
        //初始化监听器
        contentView = (ViewGroup) view.findViewById(android.R.id.content);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_conversation_srl_pullrefreshs);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_conversation_srl_views);
        scanner = (TextView) view.findViewById(R.id.scanner);
        ll_scanner = (LinearLayout) view.findViewById(R.id.ll_scanner);
        tv_qute_pc = (TextView) view.findViewById(R.id.tv_qute_pc);
        ll_scanner.setOnClickListener(this);
        tv_qute_pc.setOnClickListener(this);
        titlePopup = new TitlePopup(getContext(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titlePopup.addAction(new ActionItem(getContext(), "扫一扫", R.drawable.man_yes));
        //titlePopup条目点击事件
        if (!TextUtils.isEmpty(webToken)) {
            tv_qute_pc.setVisibility(View.VISIBLE);
        }
        scanner();
        if (jsonBody != null) {
            JsonObject jsonObject = new JsonParser().parse(jsonBody).getAsJsonObject();
            String body = jsonObject.get("alert").getAsString();
        } else {

        }
        //刷新页面
        refreshLayout.setEnabled(true);
        //设置卷内的颜色
        refreshLayout.setColorSchemeResources(R.color.colorLightYellow,
                R.color.colorLightYellow, R.color.colorLightYellow, R.color.colorLightYellow);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateConversationList();
                refreshLayout.setRefreshing(false);
            }
        });
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //设置分割线
        recyclerView.addItemDecoration(new MyDividerItem(getContext(), MyDividerItem.VERTICAL_LIST));
        itemAdapter = new LCIMCommonListAdapter<>(ConversationItemHolder.class);
        recyclerView.setAdapter(itemAdapter);
        EventBus.getDefault().register(this);
        updateConversationList();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_scanner:
                titlePopup.show(view);
                break;

            case R.id.tv_qute_pc:
                startActivity(new Intent(getActivity(), QuitPCActivity.class));
                //退出PC端
                break;
        }

    }

    private void scanner() {
        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
            @Override
            public void onItemClick(ActionItem item, int position) {
                if (position == 0) {
                    PermissionGen.needPermission(ConversationListFragment.this, 100,
                            new String[] {
                                    Manifest.permission.CAMERA
                            }
                    );
                }
            }
        });
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                     int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
    @PermissionSuccess(requestCode = 100)
    public void doSomething(){
        //权限已获得
        // //权限已获得
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivityForResult(intent, 1);
    }
    @PermissionFail(requestCode = 100)
    public void doFailSomething(){
        App.showToast(getContext(),"请设置权限");
    }

    /**
     * 处理二维码扫描结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == 1) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Log.e("扫描结果：",result);
                    LoginPC(result);

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {

                }
            }
        }
    }
    //登录PC端
    public void LoginPC(final String result){

        if(result!=null){
            //扫描二维码登录PC端
            AlertDialog ad=new AlertDialog.Builder(getContext()).create();
            ad.setTitle("闲惠");
            //ad.setIcon(R.drawable.ic_launcher);
            ad.setMessage("确定登录PC端吗？");
            ad.setButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //请求服务器登录PC端
                    OkHttpUtils
                            .post()
                            .url(apiURL+"/rest/employee/loginqrdo")
                            .addParams("token",token)
                            .addParams("webtoken",result)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {

                                }
                                @Override
                                public void onResponse(String response, int id) {
                                    App.showToast(getContext(),"登录成功!");
                                    Message msg=Message.obtain();
                                    msg.what=0;
                                    msg.obj=result;
                                    handler.sendMessage(msg);
                                }
                            });

                }
            });
            ad.setButton2("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            ad.show();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        updateConversationList();
        //判断是否退出PC端了
        String webToken=sp.getString("webtoken",null);
        if (TextUtils.isEmpty(webToken)){
            tv_qute_pc.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    /**
     * 收到对方消息时响应此事件
     * @param event
     */
    public void onEvent(LCIMIMTypeMessageEvent event) {
        updateConversationList();
    }

    /**
     * 删除会话列表中的某个 item
     * @param event
     */
    public void onEvent(LCIMConversationItemLongClickEvent event) {
        if (null != event.conversation) {
            String conversationId = event.conversation.getConversationId();
            LCIMConversationItemCache.getInstance().deleteConversation(conversationId);
            updateConversationList();
        }
    }

    /**
     * 刷新页面
     */
    private void updateConversationList() {
        //获取聊天数据
        List<String> convIdList = LCIMConversationItemCache.getInstance().getSortedConversationList();
        List<AVIMConversation> conversationList = new ArrayList<>();
        //添加聊天数据
        for (String convId : convIdList) {
            conversationList.add(LCChatKit.getInstance().getClient().getConversation(convId));
        }
        itemAdapter.setDataList(conversationList);
        itemAdapter.notifyDataSetChanged();
    }
    /**
     * 离线消息数量发生变化是响应此事件
     * 避免登陆后先进入此页面，然后才收到离线消息数量的通知导致的页面不刷新的问题
     * @param updateEvent
     */
    public void onEvent(LCIMOfflineMessageCountChangeEvent updateEvent) {
        updateConversationList();
    }

}
