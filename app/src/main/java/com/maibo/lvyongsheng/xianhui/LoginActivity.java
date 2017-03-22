package com.maibo.lvyongsheng.xianhui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.serviceholdermessage.ServiceDatas;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import okhttp3.Call;

/**
 * 登陆页面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    protected EditText nameView, passwordView;
    protected TextView loginButton;
    private TextView btnLogin, tv_back;
    LinearLayout tv_change_shop;
    LinearLayout ll_logig_interface;
    private SharedPreferences sp, sp1;
    private final String APP_ID = "wciU4iW0lEVmc9EJ9WzmhyGw-gzGzoHsz";
    private final String APP_KEY = "eXUtSMYSxVCJhE4IHOiGWabv";
    String passwords;
    ProgressDialog dialog;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.iv_icon)
    ImageView iv_icon;
    ProgressDialog pdDialog;

    int number = 0;
    int isSuccess;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //弹出对话框提醒可短信登录
                    showAlertDialog(msg);
                    break;
                case 1:
                    loginApp(msg);
                    break;
                case 2:
                    String message2 = (String) msg.obj;
                    final AlertDialog aDialog2 = new AlertDialog.Builder(LoginActivity.this).create();
                    aDialog2.setTitle("提示");
                    aDialog2.setMessage(message2);
                    aDialog2.setButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            aDialog2.dismiss();
                        }
                    });
                    aDialog2.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        initView();
        //适配界面
        setHeightAndWidth();
        judgeHowLogin();

    }

    private void judgeHowLogin() {
        //判断登录状态
        isSuccess = sp.getInt("success", 0);
        String guid = sp.getString("guid", null);

        //已成功登录
        if (isSuccess == 1) {
            ll_logig_interface.setVisibility(View.INVISIBLE);
            //预加载
            if (CustomUserProvider.getInstance().partUsers.size() > 0) {
                tv_change_shop.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                //此处显示启动页
                tv_change_shop.setVisibility(View.VISIBLE);
                getMyCustomerList(sp.getString("apiURL", null), sp.getString("token", null)
                        , sp.getString("displayname", null), sp.getString("guid", null));
            }
        } else {
            //此处代表首次登录
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirstLoginClient", true);
            editor.commit();

            ll_logig_interface.setVisibility(View.VISIBLE);
            //普通账户密码登录
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //获取输入框内的用户名和密码
                    dialog.show();
                    String names = nameView.getText().toString().trim();
                    passwords = passwordView.getText().toString().trim();
                    getDate(names, passwords);
                    //使软键盘主动消失
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
        pdDialog = new ProgressDialog(this);

        sp = getSharedPreferences("baseDate", MODE_PRIVATE);
        sp1 = getSharedPreferences("changeAccount", MODE_PRIVATE);
        nameView = (EditText) findViewById(R.id.activity_login_et_username);
        passwordView = (EditText) findViewById(R.id.activity_login_et_password);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);
        nameView.setHintTextColor(Color.rgb(186, 186, 188));
        passwordView.setHintTextColor(Color.rgb(186, 186, 188));
        loginButton = (TextView) findViewById(R.id.activity_login_btn_login);
        btnLogin = (TextView) findViewById(R.id.btn_login);
        ll_logig_interface = (LinearLayout) findViewById(R.id.ll_logig_interface);
        tv_change_shop = (LinearLayout) findViewById(R.id.tv_change_shop);
        ll_logig_interface.setVisibility(View.VISIBLE);
        tv_change_shop.setVisibility(View.GONE);
    }

    /**
     * 适配界面
     */
    private void setHeightAndWidth() {
        View views[] = {iv_icon, nameView, passwordView, btnLogin};
        int height[] = {viewHeight * 1 / 6, viewHeight * 20 / 255, viewHeight * 20 / 255, viewHeight * 20 / 255};
        int widths[] = null;
        setViewHeightAndWidth(views, height, widths);
    }

    /**
     * 首次登录
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

                        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        SharedPreferences.Editor editor = sp.edit();
                        SharedPreferences.Editor editor1 = sp1.edit();
//                        Log.e("LoginActivity",response);
                        //登录判断
                        JsonObject obj = new JsonParser().parse(response).getAsJsonObject();
                        String status = obj.get("status").getAsString().trim();
                        String message = obj.get("message").getAsString();
                        if (status.equals("error")) {
                            if (message.trim().equals("用户不存在！")) {
                                Message msg = Message.obtain();
                                msg.what = 2;
                                msg.arg1 = 0;
                                msg.obj = message + "登录前，请激活帐号！";
                                handler.sendMessage(msg);
                            } else {
                                Message msg = Message.obtain();
                                msg.what = 0;
                                msg.arg1 = 0;
                                msg.obj = message;
                                handler.sendMessage(msg);
                            }
                            dialog.dismiss();

                        } else {
                            JsonObject data = obj.get("data").getAsJsonObject();
                            String token = data.get("token").getAsString();
                            String apiURL = data.get("api_url").getAsString();
                            String guID = data.get("guid").getAsString();
                            String avator_url = data.get("avator_url").getAsString();
                            int init_login_password = data.get("init_login_password").getAsInt();
                            String displayname = data.get("display_name").getAsString();
                            JsonObject agent_info=data.get("agent_info").getAsJsonObject();
                            String agent_id=agent_info.get("agent_id").getAsString();
                            String agent_name=agent_info.get("agent_name").getAsString();
                            if (init_login_password == 0) {
                                //保存登录成功状态、基础数据
                                //Log.e("普通登录:",response);
                                editor.putString("userName", name);
                                editor.putString("password", password);
                                editor1.putString("userName", name);
                                editor1.putString("password", password);
                                editor.putInt("success", 1);
                                editor.putString("token", token);
                                editor.putString("apiURL", apiURL);
                                editor.putString("guid", guID);
                                editor.putString("avator_url", avator_url);
                                editor.putString("displayname", displayname);
                                editor.putString("agent_id",agent_id);
                                editor.putString("agent_name",agent_name);
                                editor.commit();
                                editor1.commit();
                                getMyCustomerList(apiURL, token, displayname, guID);
                            } else {
                                //带上电话号码（userName）
                                Intent intent = new Intent(getApplicationContext(), UpdataPasswordActivity.class);
                                intent.putExtra("userName", name);
                                intent.putExtra("tag", "normalPassword");
                                startActivity(intent);
                                finish();
                            }
                        }
                        dialog.dismiss();
                    }
                });
    }


    /**
     * 第一次登录及初始化Leancloud相关内容
     *
     * @param msg
     */
    private void loginApp(Message msg) {
        String str = (String) msg.obj;
        final String[] buffer = str.split(",");
        //预加载
        LCChatKit.getInstance().setProfileProvider(CustomUserProvider.getInstance());
        AVIMClient currentClient = AVIMClient.getInstance(buffer[0], "Mobile");
        currentClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    LCChatKit.getInstance().open(buffer[0], new AVIMClientCallback() {
                        @Override
                        public void done(AVIMClient avimClient, AVIMException e) {
                            if (e == null) {
                                /////////////////
                                if (isSuccess == 0) {
                                    //获取conversation_id
                                    ServiceDatas serviceDatas = new ServiceDatas(getApplicationContext());
                                    serviceDatas.getConversationIDFromService();
                                } else {
                                    finish();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                /////////////////
                            } else {
                                App.showToast(getApplicationContext(), e.getMessage() + "");
                            }
                        }
                    });

                } else {
                    App.showToast(getApplicationContext(), e.getMessage() + "");
                }
            }
        });
        dialog.dismiss();
    }

    /**
     * 弹出短信登录对话框
     *
     * @param msg
     */
    private void showAlertDialog(Message msg) {
        String message = (String) msg.obj;
        final AlertDialog aDialog = new AlertDialog.Builder(LoginActivity.this).create();
        aDialog.setTitle("提示");
        aDialog.setMessage(message);
        aDialog.setButton("短信登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //跳转到验证界面
                Intent intent = new Intent(LoginActivity.this, IdentifyActivity.class);
                intent.putExtra("password", passwords);
                startActivity(intent);
            }
        });
        aDialog.setButton2("重试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                aDialog.dismiss();
            }
        });
        aDialog.show();
    }

    @Override
    public void onClick(View view) {
        //返回到WelcomeGuideActivity界面
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(this, SplashActivity.class));
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 获取用户体系
     *
     * @param apiURL
     * @param token
     * @param name
     * @param guid
     */
    public void getMyCustomerList(final String apiURL, final String token, final String name, final String guid) {
        //初始化用户信息
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getuserlist")
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showToast(R.string.net_connect_error);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //获取员工信息
                        CustomUserProvider.getInstance().partUsers.clear();
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        String status=object.get("status").getAsString();
                        String message=object.get("message").getAsString();
                        if (status.equals("ok")){
                            JsonArray array = object.get("data").getAsJsonArray();
                            for (JsonElement jsonElement : array) {
                                JsonObject jObject = jsonElement.getAsJsonObject();
                                String names = jObject.get("display_name").getAsString();
                                String guid = jObject.get("guid").getAsString();
                                String avator_url = jObject.get("avator_url").getAsString();
                                CustomUserProvider.getInstance().partUsers.add(new LCChatKitUser(guid, names, avator_url));

                            }
                            Message msg = Message.obtain();
                            msg.what = 1;
                            msg.arg1 = 0;
                            msg.obj = guid + "," + apiURL + "," + token;
                            handler.sendMessage(msg);
                        }else{
                            showToast(message);
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);

    }

    /**
     * 获取会话ID
     *
     * @param event
     */
    public void onEvent(EventDatas event) {
        Log.e("Login","event");

        if (event.getTag().equals(Constants.GET_CONVERSATION_ID)) {
            if (event.getMessageStatus().equals("error")) {
                showToast(R.string.net_connect_error);
            } else if (event.getMessageStatus().equals("right")) {
                String response = event.getResponse();
                //解析数据
                analysisConversationDate(response);
            } else if (event.getMessageStatus().equals("message")) {
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
        } else {
            finish();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            return;
        }

    }

    /**
     * 加载聊天记录到内存和数据库
     * @param list_conver
     */
    private void uploadDatasToStorage(final List<String> list_conver) {
        new Thread() {
            @Override
            public void run() {
                try {
                    for (final String str : list_conver) {
                        number++;
                        LCIMConversationItemCache.getInstance().insertConversation(str);
                        pdDialog.setProgress(number);
                        Thread.sleep(100);
                        if (number == list_conver.size()) {
                            finish();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
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
     * @param max
     */
    private void showProgressDialog(int max) {
        Log.e("max", max + "");
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
