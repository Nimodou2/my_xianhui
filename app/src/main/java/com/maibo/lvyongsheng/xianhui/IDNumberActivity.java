package com.maibo.lvyongsheng.xianhui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import de.greenrobot.event.EventBus;
import okhttp3.Call;

/**
 * Created by LYS on 2016/9/23.
 */
public class IDNumberActivity extends BaseActivity {
    private static final String TAG="IDNumberActivity";
    TextView phoneNumber, time;
    EditText IDNumber;
    TextView btn, back;
    MyCountDownTimer mc;
    int isLogin = 0;
    String number;
    String password;
    ProgressDialog dialog;
    private SharedPreferences sp, sp1;
    private final String APP_ID = "wciU4iW0lEVmc9EJ9WzmhyGw-gzGzoHsz";
    private final String APP_KEY = "eXUtSMYSxVCJhE4IHOiGWabv";
    ProgressDialog pdDialog;
    int number_conversation = 0;

    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.ll_yanzheng)
    LinearLayout ll_yanzheng;
    @Bind(R.id.ll_time)
    LinearLayout ll_time;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Log.e(TAG,"执行到了这里handleMessage");
                    final String guID = (String) msg.obj;
                    loginApp(guID);
                    break;
            }
        }
    };

    /**
     * 登录应用并初始化LeanCloud相关数据
     * @param guID
     */
    private void loginApp(final String guID) {
        Log.e(TAG,"执行到了这里loginApp");
        //预加载
        LCChatKit.getInstance().setProfileProvider(CustomUserProvider.getInstance());
       // AVIMClient currentClient = AVIMClient.getInstance(guID, "Mobile");
        AVIMClient currentClient = AVIMClient.getInstance(guID);
        currentClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    LCChatKit.getInstance().open(guID, new AVIMClientCallback() {
                        @Override
                        public void done(AVIMClient avimClient, AVIMException e) {
                            if (e == null) {
                                Log.e(TAG,"执行了预加载中的");
                                ServiceDatas serviceDatas = new ServiceDatas(getApplicationContext());
                                serviceDatas.getConversationIDFromService();
                            } else {
                                App.showToast(getApplicationContext(), e.getMessage() + "");
                            }
                        }
                    });

                } else {
                    Log.e(TAG,"执行了错误loginApp");
                    App.showToast(getApplicationContext(), e.getMessage() + "");
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_number);
        //adapterLitterBar(ll_head);
        dialog = new ProgressDialog(this);
        dialog.setMessage("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
        EventBus.getDefault().register(this);
        phoneNumber = (TextView) findViewById(R.id.tv_pnNmber);
        time = (TextView) findViewById(R.id.tv_daojishi);
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sp = getSharedPreferences("baseDate", MODE_PRIVATE);
        sp1 = getSharedPreferences("changeAccount", MODE_PRIVATE);
        IDNumber = (EditText) findViewById(R.id.et_yanzhengma);
        IDNumber.setHintTextColor(Color.rgb(186, 186, 188));
        btn = (TextView) findViewById(R.id.btn);
        mc = new MyCountDownTimer(60000, 1000);
        mc.start();

        //适配界面
        setHeightAndWeidth();

        Intent intent = getIntent();
        number = intent.getStringExtra("phoneNumber");
        password = intent.getStringExtra("password");
        phoneNumber.setText(number);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ID = IDNumber.getText().toString().trim();
                if (!TextUtils.isEmpty(ID)) {
                    dialog.show();
                    getData(ID, number);
                } else {
                    App.showToast(getApplicationContext(), "验证码不能为空！");
                }


            }
        });
    }

    /**
     * 适配
     */
    private void setHeightAndWeidth() {
        View views[] = {phoneNumber, ll_yanzheng, ll_time};
        int ht = viewHeight * 20 / 255;
        int heights[] = {ht, ht, ht};
        int widths[] = null;
        setViewHeightAndWidth(views, heights, widths);
    }

    public void getData(String ID, final String number) {
        OkHttpUtils
                .post()
                .url("http://sso.sosys.cn:8080/mybook/rest/loginsmsverify")
                .addParams("mobile", number)
                .addParams("sms_code", ID)
                .addParams("type", "employee")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("登录成功",response);
                        SharedPreferences.Editor editor = sp.edit();
                        SharedPreferences.Editor editor1 = sp1.edit();
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String message = jsonObject.get("message").getAsString();
                        String status = jsonObject.get("status").getAsString();
                        if (status.equals("ok")) {
                            isLogin = 1;
                            //登录成功，跳转到MainActivity
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            String token = data.get("token").getAsString();
                            String apiURL = data.get("api_url").getAsString();
                            String guID = data.get("guid").getAsString();
                            String avator_url = data.get("avator_url").getAsString();
                            int init_login_password = data.get("init_login_password").getAsInt();
                            String displayname = data.get("display_name").getAsString();
                            JsonObject agent_info=data.get("agent_info").getAsJsonObject();
                            String agent_id=agent_info.get("agent_id").getAsString();
                            String agent_name=agent_info.get("agent_name").getAsString();
                            Log.e(TAG,"执行到了这里");
                           // editor.commit();
                            if (init_login_password == 0) {
                                //保存登录成功状态、基础数据
                                editor.putString("userName", number);
//                                editor.putString("password", password);
                                editor1.putString("userName", number);
//                                editor1.putString("password", password);
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
                                Log.e(TAG,"执行到了这里getMyCustomerList");
                                getMyCustomerList(apiURL, token, displayname, guID);

                            } else {
                                Intent intent = new Intent(IDNumberActivity.this, UpdataPasswordActivity.class);
                                intent.putExtra("userName", displayname);
                                intent.putExtra("password", password);
                                intent.putExtra("tag", "normalPassword");
                                startActivity(intent);
                                CloseAllActivity.getScreenManager().clearAllActivity();
                                finish();
                            }
                        } else {
                            App.showToast(IDNumberActivity.this, message);
                        }
                        dialog.dismiss();

                    }
                });
    }

    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    表示以毫秒为单位 倒计时的总数
         *                          <p>
         *                          例如 millisInFuture=1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick 方法
         *                          <p>
         *                          例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            time.setText("");
            if (isLogin == 0) {
                //进行语音验证
                getData(number);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            time.setText("" + millisUntilFinished / 1000 + "秒");
        }
    }

    /**
     * 语音获取验证码
     *
     * @param number
     */
    public void getData(String number) {
        OkHttpUtils
                .post()
                .url("http://sso.sosys.cn:8080/mybook/rest/loginsmsgot")
                .addParams("mobile", number)
                .addParams("sms_type", "voice")
                .addParams("type", "employee")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
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

                        } else {
                            App.showToast(getApplicationContext(), message);
                        }
                    }
                });
    }

    /**
     * 获取用户体系
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
                        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //获取员工信息
                        Log.e("getUserList:", response);
                        CustomUserProvider.getInstance().partUsers.clear();
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        if (object.get("status").getAsString().equals("ok")) {
                            Log.e(TAG,"执行到了这里getMyCustomerListobject.get(\"status\").getAsString().equals(\"ok\")");
                            JsonArray array = object.get("data").getAsJsonArray();
                            for (JsonElement jsonElement : array) {
                                JsonObject jObject = jsonElement.getAsJsonObject();
                                String names = jObject.get("display_name").getAsString();
                                String guid = jObject.get("guid").getAsString();
                                String avator_url = jObject.get("avator_url").getAsString();
                                CustomUserProvider.getInstance().partUsers.add(new LCChatKitUser(guid, names, avator_url));
                            }
                            Log.e(TAG,"执行到了这里Message");
                            Message msg = Message.obtain();
                            msg.what = 0;
                            msg.arg1 = 0;
                            msg.obj = guid;
                            handler.sendMessage(msg);

                        }else{
                            Log.e(TAG,"获取1员工信息出错");
                        }
                    }
                });
    }


    /**
     * 获取会话ID
     *
     * @param event
     */
    public void onEvent(EventDatas event) {
        Log.e("IDNumber","event");
        if (event.getTag().equals(Constants.GET_CONVERSATION_ID)) {
            if (event.getMessageStatus().equals("error")) {
                Log.e("IDNUmber","error");
                showToast(R.string.net_connect_error);
            } else if (event.getMessageStatus().equals("right")) {
                Log.e("IDNUmber","error");
                String response = event.getResponse();
                //解析数据
                analysisConversationDate(response);
            } else if (event.getMessageStatus().equals("message")) {
                Log.e("IDNUmber","error");
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
            dialog.dismiss();
            finish();
            Intent intent = new Intent(IDNumberActivity.this, MainActivity.class);
            startActivity(intent);
            return;
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
                try {
                    for (final String str : list_conver) {
                        number_conversation++;
                        LCIMConversationItemCache.getInstance().insertConversation(str);
                        pdDialog.setProgress(number_conversation);
                        Thread.sleep(100);
                        if (number_conversation == list_conver.size()) {
                            finish();
                            Intent intent = new Intent(IDNumberActivity.this, MainActivity.class);
                            startActivity(intent);
                            pdDialog.cancel();
                            //此处要将栈中所有Activity清空
                            CloseAllActivity.getScreenManager().clearAllActivity();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
