package com.maibo.lvyongsheng.xianhui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.Agent;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.implement.Util;
import com.maibo.lvyongsheng.xianhui.serviceholdermessage.ServiceDatas;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import de.greenrobot.event.EventBus;
import okhttp3.Call;

/**
 * Created by LYS on 2016/9/28.
 */
public class AgentActivity extends BaseActivity implements View.OnClickListener {
    List<Agent> list2;
    //ProgressDialog proDialog;
    TextView back, quit, door;
    String apiURL;
    String token;
    SharedPreferences sp1, sp2, sp3, sp4, sp5, sp6;
    ArrayList<String> data_list;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;

    String userName;
    String type;
    String sign;
    String publicKey = "1addfcf4296d60f0f8e0c81cea87a099";

    ProgressDialog pdDialog;

    android.os.Handler handler = new android.os.Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    //获取代理商数据成功
                    handlerMessage0(msg);
                    break;
                case 1:
                    //设置代理成功
                    handlerMessage1(msg);
                    break;
                case 2:
                    //获取用户体系
                    handlerMessage2(msg);
                    break;
                case 3:
                    //此处代表获取账号密码成功,开始切换帐号
                    handlerMessage3(msg);
                    break;
                case 4:
                    //重新获取代理
                    showShortDialog();
                    //重新获取全局变量
                    getGlobleVariable();
                    //获取代理数据
                    Log.e("Agent", "切换成功");
                    getServiceData(userName, type, sign);
                    showToast("切换成功");
                    EventBus.getDefault().post(new EventDatas(Constants.SWITCH_STORES, ""));
                    break;
            }
        }
    };

    /**
     * 重新登录来获取基础数据
     *
     * @param msg
     */
    private void handlerMessage3(Message msg) {
        String agent_id = (String) msg.obj;
        String userName = sp1.getString("userName", null);
        String publicKey = "1addfcf4296d60f0f8e0c81cea87a099";
        String type = "employee";
        String sign = Util.getSign(userName, type, agent_id, publicKey);
        showShortDialog();
        setAgent(userName, type, agent_id, sign);
    }

    /**
     * 获取用户体系
     *
     * @param msg
     */
    private void handlerMessage2(Message msg) {
        String guID = (String) msg.obj;
        //重新获取用户体系
        LCChatKit.getInstance().setProfileProvider(CustomUserProvider.getInstance());
        LCChatKit.getInstance().open(guID, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    //同步聊天记录
                    ServiceDatas serviceDatas = new ServiceDatas(getApplicationContext());
                    serviceDatas.getConversationIDFromService();
                } else {
                    showToast(e.getMessage());
                }
            }
        });
    }

    /**
     * 设置代理成功
     *
     * @param msg
     */
    private void handlerMessage1(Message msg) {
        int result = msg.arg1;
        if (result == 1) {
            //关闭推送服务
            String guid_close = sp1.getString("guid", null);
            if (guid_close != null)
                closeLeancloud(guid_close);
            //此处要清理缓存
            sp1.edit().clear().commit();
            sp2.edit().clear().commit();
            sp3.edit().clear().commit();
            sp6.edit().clear().commit();
            //如果账号密码已保存则自动登录，否则跳到登录界面重新登录
            if (sp4.getString("userName", null) != null) {
                //此处重新获取登录数据
                String userNames = sp4.getString("userName", null);
                String passwords = sp4.getString("password", null);
                showShortDialog();
                getDate(userNames, passwords);
            }

        } else {
            App.showToast(getApplicationContext(), "切换失败");
        }
    }

    /**
     * 获取代理商数据
     *
     * @param msg
     */
    private void handlerMessage0(Message msg) {
        list2 = (List<Agent>) msg.obj;
        data_list = new ArrayList<>();
        for (int i = 0; i < list2.size(); i++) {
            data_list.add(list2.get(i).getAgent_name());
            if (list2.get(i).getIs_default().equals("1")) {
                door.setText(list2.get(i).getAgent_name());
                //保存agent_id为了反馈问题页面所用
                SharedPreferences.Editor editor = sp1.edit();
                editor.putString("agent_id", list2.get(i).getAgent_id());
                editor.commit();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent);
        CloseAllActivity.getScreenManager().pushActivity(this);
        uploadConversationId();
        initView();
        initDatas();
        EventBus.getDefault().register(this);
    }

    /**
     * 初始化基础数据
     */
    private void initDatas() {
        sp1 = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        sp2 = getSharedPreferences("checkBoxProduct", MODE_PRIVATE);
        sp3 = getSharedPreferences("checkBox", MODE_PRIVATE);
        sp4 = getSharedPreferences("changeAccount", MODE_PRIVATE);
        sp5 = getSharedPreferences("app", MODE_PRIVATE);
        sp6 = getSharedPreferences("dataBase", Context.MODE_PRIVATE);
        //获取全局变量，此处随着账号的切换会发生改变，所以必须动态获取，或者转变为局部变量
        getGlobleVariable();
        showLongDialog();
        getServiceData(userName, type, sign);
    }

    /**
     * 获取全局变量，此处随着账号的切换会发生改变，所以必须动态获取，或者转变为局部变量
     */
    private void getGlobleVariable() {
        apiURL = sp1.getString("apiURL", null);
        token = sp1.getString("token", null);
        userName = sp1.getString("userName", null);
        type = "employee";
        sign = Util.getSign(userName, type, publicKey);
    }

    /**
     * 初始化View
     */
    private void initView() {
        //adapterLitterBar(ll_head);
        quit = (TextView) findViewById(R.id.quit);
        back = (TextView) findViewById(R.id.back);
        door = (TextView) findViewById(R.id.tv_door);
        door.setOnClickListener(this);
        back.setOnClickListener(this);
        quit.setOnClickListener(this);
    }

    /**
     * 选择门店
     */
    private void diaLog() {
        //dialog
        LinearLayout linearLayoutMain = new LinearLayout(AgentActivity.this);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(AgentActivity.this);//this为获取当前的上下文
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        listView.setFadingEdgeLength(0);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                AgentActivity.this,
                android.R.layout.simple_expandable_list_item_1,
                data_list);
        listView.setAdapter(adapter);
        //往这个布局中加入listview
        linearLayoutMain.addView(listView);
        final AlertDialog dialog = new AlertDialog.Builder(AgentActivity.this)
                .setTitle("请选择账号").setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .create();
        dialog.setCanceledOnTouchOutside(true);//使除了dialog以外的地方不能被点击
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String agent_id = list2.get(i).getAgent_id();
                dialog.cancel();
                //如果是当前已选则不再切换,否则切换
                if (!list2.get(i).getIs_default().equals("1")) {
                    //先确定帐号密码是否存在
                    isExistAccount(agent_id);
                }
            }
        });
    }

    /**
     * 此处判断帐号密码是否存在
     */
    private void isExistAccount(final String agent_id) {
        if (sp4.getString("password", null) != null) {
            String userName = sp1.getString("userName", null);
            String publicKey = "1addfcf4296d60f0f8e0c81cea87a099";
            String type = "employee";
            String sign = Util.getSign(userName, type, agent_id, publicKey);
            showShortDialog();
            setAgent(userName, type, agent_id, sign);
        } else {
            //弹出帐号验证窗口
            View view = View.inflate(this, R.layout.style_dialog_agentactivity, null);
            final EditText et_zhanghao = (EditText) view.findViewById(R.id.et_zhanghao);
            final EditText et_password = (EditText) view.findViewById(R.id.et_password);
            final AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .setPositiveButton("确定", null)
                    .setNegativeButton("取消", null)
                    .show();
            Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //获取帐号和密码
                    String zhanghao = et_zhanghao.getText().toString().trim();
                    String newPassword = et_password.getText().toString().trim();
                    if (!TextUtils.isEmpty(zhanghao) && !TextUtils.isEmpty(newPassword)) {
                        //请求服务器，重新获取帐号和密码
                        showShortDialog();
                        LoginAgain(zhanghao, newPassword, agent_id);
                    } else {
                        App.showToast(getApplicationContext(), "帐号或密码不能为空！");
                    }
                    alertDialog.dismiss();
                }

            });

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.quit:
                quitApplication();
                break;
            case R.id.tv_door:
                //判断是否有数据
                if (data_list != null)
                    diaLog();
                break;
            case R.id.back:
                finish();
                break;
        }

    }

    /**
     * 获取代理商数据
     *
     * @param userName
     * @param type
     * @param sign
     */
    public void getServiceData(String userName, String type, String sign) {
        OkHttpUtils
                .post()
                .url("http://sso.sosys.cn:8080/mybook/rest/getagentlist")
                .addParams("username", userName)
                .addParams("type", type)
                .addParams("sign", sign)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissLongDialog();
                        dismissShortDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.e("getServiceData", response);
                        //解析
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String message = jsonObject.get("message").getAsString();
                        if (!jsonObject.get("data").isJsonNull()) {
                            JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
                            List<Agent> list1 = new ArrayList<Agent>();
                            for (JsonElement jsonElement : jsonArray) {
                                JsonObject jo = jsonElement.getAsJsonObject();
                                String agent_id = jo.get("agent_id").getAsString();
                                String agent_name = jo.get("agent_name").getAsString();
                                String is_default = jo.get("is_default").getAsString();
                                list1.add(new Agent(agent_id, agent_name, is_default));
                            }
                            Message msg = Message.obtain();
                            msg.what = 0;
                            msg.obj = list1;
                            handler.sendMessage(msg);
                        } else {
                            App.showToast(getApplicationContext(), message);
                        }
                        dismissLongDialog();
                        dismissShortDialog();
                    }
                });
    }

    /**
     * 设置默认代理
     *
     * @param userName
     * @param type
     * @param agent_id
     * @param sign
     */
    public void setAgent(String userName, String type, String agent_id, String sign) {
        OkHttpUtils
                .post()
                .url("http://sso.sosys.cn:8080/mybook/rest/setdefaultagent")
                .addParams("username", userName)
                .addParams("type", type)
                .addParams("agent_id", agent_id)
                .addParams("sign", sign)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissShortDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                        String status = jo.get("status").getAsString();
                        if (status.equals("ok")) {
                            Message msg = Message.obtain();
                            msg.what = 1;
                            msg.arg1 = 1;
                            handler.sendMessage(msg);
                        } else {
                            Message msg = Message.obtain();
                            msg.what = 1;
                            msg.arg1 = 2;
                            handler.sendMessage(msg);
                        }
                        dismissShortDialog();
                    }
                });
    }


    /**
     * 退出应用
     */
    private void quitApplication() {
        AlertDialog ad = new AlertDialog.Builder(AgentActivity.this).create();
        ad.setTitle("闲惠");
        //ad.setIcon(R.drawable.ic_launcher);
        ad.setMessage("确定退出应用吗？");
        ad.setButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //关闭推送服务
                String guid_close = sp1.getString("guid", null);
                if (guid_close != null)
                    closeLeancloud(guid_close);
                //退出应用,清楚所用缓存
                sp1.edit().clear().commit();
                sp2.edit().clear().commit();
                sp3.edit().clear().commit();
                sp4.edit().clear().commit();
                sp5.edit().clear().commit();
                sp6.edit().clear().commit();

                CustomUserProvider.getInstance().customUserProvider = null;
                //清除聊天记录
                List<String> conversationId = LCIMConversationItemCache.getInstance().getSortedConversationList();
                for (int i = 0; i < conversationId.size(); i++) {
                    LCIMConversationItemCache.getInstance().deleteConversation(conversationId.get(i));
                }
                //通知服务器，注销帐号
                OkHttpUtils
                        .post()
                        .url("http://sso.sosys.cn:8080/mybook/rest/logout")
                        .addParams("token", token)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String response, int id) {
                            }
                        });
                CloseAllActivity.getScreenManager().clearAllActivity();
                //跳转到SplashActivity界面
                startActivity(new Intent(AgentActivity.this, SplashActivity.class));
                finish();

            }
        });
        ad.setButton2("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ad.show();
    }

    /**
     * 退出当前应用时，要关闭消息推送
     */
    private void closeLeancloud(String guid) {

        AVIMClient myClient = AVIMClient.getInstance(guid);
        myClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient client, AVIMException e) {
                if (e == null) {
                    //登录成功
                    client.close(new AVIMClientCallback() {
                        @Override
                        public void done(AVIMClient client, AVIMException e) {
                            if (e == null) {
                                //登出成功
                                Log.e("close", "退出成功！");
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 当切换门店的时候，重新获取相关数据，重新登录
     *
     * @param name
     * @param password
     */
    private void getDate(final String name, final String password) {
        OkHttpUtils
                .post()
                .url("http://sso.sosys.cn:8080/mybook/rest/loginmobile")
                .addParams("mobile", name)
                .addParams("password", password)
                .addParams("type", "employee")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissShortDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        SharedPreferences.Editor editor = sp1.edit();
                        //登录判断
                        JsonObject obj = new JsonParser().parse(response).getAsJsonObject();
                        String status = obj.get("status").getAsString().trim();
                        if (status.equals("ok")) {
                            JsonObject data = obj.get("data").getAsJsonObject();
                            String token = "";
                            String apiURL = "";
                            String guID = "";
                            String avator_url = "";
                            String displayname = "";
                            String agent_id = "";
                            String agent_name = "";
                            if (!data.get("token").isJsonNull())
                                token = data.get("token").getAsString();
                            if (!data.get("api_url").isJsonNull())
                                apiURL = data.get("api_url").getAsString();
                            if (!data.get("guid").isJsonNull())
                                guID = data.get("guid").getAsString();
                            if (!data.get("avator_url").isJsonNull())
                                avator_url = data.get("avator_url").getAsString();
                            if (!data.get("display_name").isJsonNull())
                                displayname = data.get("display_name").getAsString();
                            if (data.get("agent_info").isJsonObject()) {
                                JsonObject agent_info = data.get("agent_info").getAsJsonObject();
                                if (!agent_info.get("agent_id").isJsonNull())
                                    agent_id = agent_info.get("agent_id").getAsString();
                                if (!agent_info.get("agent_name").isJsonNull())
                                    agent_name = agent_info.get("agent_name").getAsString();
                            }
                            //保存登录成功状态、基础数据
                            editor.putString("userName", name);
                            editor.putString("password", password);
                            editor.putInt("success", 1);
                            editor.putString("token", token);
                            //告知门店已经更改,0:未更改，1:更改
                            editor.putInt("isChangeDoor", 1);
                            editor.putString("apiURL", apiURL);
                            editor.putString("guid", guID);
                            editor.putString("avator_url", avator_url);
                            editor.putString("displayname", displayname);
                            editor.putString("agent_id", agent_id);
                            editor.putString("agent_name", agent_name);
                            editor.commit();
                            //清除聊天记录
                            List<String> conversationId = LCIMConversationItemCache.getInstance().getSortedConversationList();
                            for (int i = 0; i < conversationId.size(); i++) {
                                LCIMConversationItemCache.getInstance().deleteConversation(conversationId.get(i));
                            }
                            //更新用户体系
                            getMyCustomerList(apiURL, token, displayname, guID);
                        } else {
                            dismissShortDialog();
                        }
                    }
                });
    }

    /**
     * 重新获取账号和密码
     *
     * @param name
     * @param password
     * @param agent_id
     */
    private void LoginAgain(final String name, final String password, final String agent_id) {
        OkHttpUtils
                .post()
                .url("http://sso.sosys.cn:8080/mybook/rest/loginmobile")
                .addParams("mobile", name)
                .addParams("password", password)
                .addParams("type", "employee")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissShortDialog();
                        showToast(R.string.net_connect_error);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        SharedPreferences.Editor editor1 = sp4.edit();
                        JsonObject jsonobject = new JsonParser().parse(response).getAsJsonObject();
                        String status = "";
                        String message = "";
                        if (!jsonobject.get("status").isJsonNull()) {
                            status = jsonobject.get("status").getAsString();
                        }
                        if (!jsonobject.get("message").isJsonNull()) {
                            message = jsonobject.get("message").getAsString();
                        }
                        if (status.equals("ok")) {
                            //将name和password保存下来
                            editor1.putString("userName", name);
                            editor1.putString("password", password);
                            editor1.commit();
                            Message msg = Message.obtain();
                            msg.what = 3;
                            msg.obj = agent_id;
                            handler.sendMessage(msg);
                        } else {
                            App.showToast(getApplicationContext(), message);
                        }
                        dismissShortDialog();
                    }
                });
    }

    /**
     * 更新门店本质上就是换账号
     *
     * @param apiURL
     * @param token
     * @param name
     * @param guid
     */
    public void getMyCustomerList(String apiURL, String token, final String name, final String guid) {
        //初始化用户信息
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getuserlist")
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissShortDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //获取员工信息
                        Log.e("getMyCustomerList:", response);
                        CustomUserProvider.getInstance().partUsers.clear();
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        JsonArray array = object.get("data").getAsJsonArray();
                        for (JsonElement jsonElement : array) {
                            JsonObject jObject = jsonElement.getAsJsonObject();
                            String names = jObject.get("display_name").getAsString();
                            String guid = jObject.get("guid").getAsString();
                            String avator_url = jObject.get("avator_url").getAsString();
                            CustomUserProvider.getInstance().partUsers.add(new LCChatKitUser(guid, names, avator_url));
                        }
                        Message msg = Message.obtain();
                        msg.what = 2;
                        msg.arg1 = 0;
                        msg.obj = guid;
                        handler.sendMessage(msg);
                        dismissShortDialog();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 上传ConversationId
     */
    private void uploadConversationId() {
        List<String> convIdList = LCIMConversationItemCache.getInstance().getSortedConversationList();
        if (convIdList.size() != 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < convIdList.size(); i++) {
                if (i != convIdList.size() - 1) {
                    buffer.append(convIdList.get(i) + ",");
                } else {
                    buffer.append(convIdList.get(i));
                }
            }
            //上传
            ServiceDatas sd = new ServiceDatas(this);
            sd.uploadConversationIdToService(buffer.toString().trim());
        }
    }

    /**
     * 接收上传ConversationID后的消息状态
     *
     * @param event
     */
    public void onEvent(EventDatas event) {
        if (event.getTag().equals(Constants.UPLOAD_CONVERSAYION_ID)) {
            if (event.getResponse().equals("error")) {
                App.showToast(this, "网络错误");
            } else if (event.getResponse().equals("right")) {
                Log.e("right", "上传成功");
            } else {
                App.showToast(this, event.getResponse());
            }
        } else if (event.getTag().equals(Constants.GET_CONVERSATION_ID)) {
            if (event.getMessageStatus().equals("error")) {
                Log.e("IDNUmber", "error");
                showToast(R.string.net_connect_error);
            } else if (event.getMessageStatus().equals("right")) {
                Log.e("IDNUmber", "right");
                String response = event.getResponse();
                //解析数据
                analysisConversationDate(response);
            } else if (event.getMessageStatus().equals("message")) {
                Log.e("IDNUmber", "message");
                showToast(event.getResponse());
            }

        }
    }

    /**
     * 解析数据
     *
     * @param response
     */
    private void analysisConversationDate(String response) {
        //处理数据
        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
        final List<String> list_conver = new ArrayList<String>();
        if (jsonObject.get("data").isJsonArray()) {
            JsonArray data = jsonObject.get("data").getAsJsonArray();
            for (JsonElement je : data) {
                JsonObject jo = je.getAsJsonObject();
                String conv_id = "";
                if (!jo.get("conv_id").isJsonNull()) {
                    conv_id = jo.get("conv_id").getAsString();
                }
                list_conver.add(conv_id);
            }
            showProgressDialog(list_conver.size());
            uploadDatasToStorage(list_conver);
        }

    }

    /**
     * 加载聊天记录到内存和数据库
     *
     * @param list_conver
     */
    private void uploadDatasToStorage(final List<String> list_conver) {
        new Thread() {
            @Override
            public void run() {
                int number_conversation = 0;
                try {
                    for (final String str : list_conver) {
                        number_conversation++;
                        LCIMConversationItemCache.getInstance().insertConversation(str);
                        pdDialog.setProgress(number_conversation);
                        Thread.sleep(100);
                        if (number_conversation == list_conver.size()) {
                            Message msg = Message.obtain();
                            msg.what = 4;
                            handler.sendMessage(msg);
                            pdDialog.cancel();
                        }
                    }

                } catch (Exception e) {
                    pdDialog.cancel();
                }

            }
        }.start();
    }

    /**
     * 展示带进度的进度条
     *
     * @param max
     */
    private void showProgressDialog(int max) {
        pdDialog = new ProgressDialog(this);
        pdDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pdDialog.setTitle("提示");
        pdDialog.setMessage("正在加载数据……");
        pdDialog.setMax(max);
//        // 设置ProgressDialog 的进度条是否不明确
//        pdDialog.setIndeterminate(false);
        // 设置ProgressDialog 是否可以按退回按键取消
        pdDialog.setCancelable(false);
        pdDialog.show();
    }
}
