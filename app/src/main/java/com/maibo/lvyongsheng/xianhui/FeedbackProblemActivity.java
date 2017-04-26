package com.maibo.lvyongsheng.xianhui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.entity.Agent;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.implement.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2017/1/11.
 */

public class FeedbackProblemActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.back)
    TextView back;
    @Bind(R.id.tv_confirm)
    TextView tv_confirm;
    @Bind(R.id.et_feedback)
    EditText et_feedback;
    SharedPreferences sp;
    String apiURL, token, agent_id;
    String sign_date = "";
    String userName = "";
    String type = "employee";
    String publicKey = "1addfcf4296d60f0f8e0c81cea87a099";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    List<Agent> list2 = (List<Agent>) msg.obj;
                    List<String> data_list = new ArrayList<>();
                    for (int i = 0; i < list2.size(); i++) {
                        data_list.add(list2.get(i).getAgent_name());
                        if (list2.get(i).getIs_default().equals("1")) {
                            //保存agent_id
                            agent_id=list2.get(i).getAgent_id();
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("agent_id", list2.get(i).getAgent_id());
                            editor.commit();
                        }
                    }
                    sign_date = Util.getSign(userName, type, agent_id, publicKey);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_problem);
        CloseAllActivity.getScreenManager().pushActivity(this);
        initView();
        initBaseData();
    }

    /**
     * 初始化View
     */
    private void initView() {
        //适配UI
        //adapterLitterBar(ll_head);
        //监听点击事件
        back.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
    }

    /**
     * 初始化基础数据
     */
    private void initBaseData() {
        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
        agent_id = sp.getString("agent_id", null);
        userName = sp.getString("userName", null);
        //获取签名
        //判空
        String[] strName = {"用户名", "类型", "代理ID", "公钥"};
        String[] strData = {userName, type, agent_id, publicKey};
        //如果isNull为空则代表数据不是空
        String isNull = Util.getNullString(strName, strData);
        if (TextUtils.isEmpty(isNull)) {
            sign_date = Util.getSign(userName, type, agent_id, publicKey);
        } else{
            String sign_three = Util.getSign(userName, type, publicKey);
            showShortDialog();
            getServiceData(userName, type, sign_three);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_confirm:
                //提交反馈
                String feedback_str = et_feedback.getText().toString().trim();
                if (!TextUtils.isEmpty(feedback_str)) {
                    showDialog(feedback_str);
                } else {
                    showToast("内容不能为空");
                }
                break;
        }

    }

    /**
     * 是否提交的提示
     */
    private void showDialog(final String feedback_str) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定提交反馈吗？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //提交到服务器
                showLongDialog();
                uploadFeedBackDatas(feedback_str);
            }
        });
        builder.show();
    }

    /**
     * 保存反馈信息
     *
     * @param content
     */
    private void uploadFeedBackDatas(String content) {
        OkHttpUtils
                .post()
                .url("http://sso.sosys.cn:8080/mybook/rest/feedback")
                .addParams("username", userName)
                .addParams("type", type)
                .addParams("agent_id", agent_id)
                .addParams("content", content)
                .addParams("sign", sign_date)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showToast(R.string.net_connect_error);
                        dismissLongDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String status = "";
                        String message = "";
                        if (!jsonObject.get("status").isJsonNull())
                            status = jsonObject.get("status").getAsString();
                        if (!jsonObject.get("message").isJsonNull())
                            message = jsonObject.get("message").getAsString();
                        if (status.equals("ok")) {
                            showToast("提交成功");
                            finish();
                        } else {
                            showToast(message);
                        }
                        dismissLongDialog();
                    }
                });

    }

    /**
     * 获取代理商ID
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
                        showToast(R.string.net_connect_error);
                        dismissShortDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //解析
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String message = jsonObject.get("message").getAsString();
                        String status = jsonObject.get("status").getAsString();
                        if (status.equals("ok")) {
                            if (!jsonObject.get("data").isJsonNull()) {
                                JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
                                List<Agent> list1 = new ArrayList<>();
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
                                showToast("数据为空!");
                            }
                        } else {
                            showToast(message);
                        }
                        dismissShortDialog();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
