package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import cn.leancloud.chatkit.LCChatKit;
import okhttp3.Call;

/**
 * Created by LYS on 2016/9/23.
 */
public class IDNumberActivity extends Activity {
    TextView phoneNumber,time;
    EditText IDNumber;
    TextView btn;
    MyCountDownTimer mc;
    int isLogin=0;
    String number;
    String password;
    private SharedPreferences sp;
    private final String APP_ID  = "wciU4iW0lEVmc9EJ9WzmhyGw-gzGzoHsz";
    private final String APP_KEY = "eXUtSMYSxVCJhE4IHOiGWabv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_number);
        phoneNumber= (TextView) findViewById(R.id.tv_pnNmber);
        time= (TextView) findViewById(R.id.tv_daojishi);
        sp=getSharedPreferences("baseDate",MODE_PRIVATE);
        IDNumber=(EditText)findViewById(R.id.et_yanzhengma);
        IDNumber.setHintTextColor(Color.rgb(186,186,188));
        btn=(TextView) findViewById(R.id.btn);
        mc = new MyCountDownTimer(60000, 1000);
        mc.start();

        Intent intent=getIntent();
        number=intent.getStringExtra("phoneNumber");
        password=intent.getStringExtra("password");
        phoneNumber.setText(number);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ID=IDNumber.getText().toString().trim();
                getData(ID,number);
            }
        });
    }
    public void getData(String ID,final String number){
        OkHttpUtils
                .post()
                .url("http://sso.sosys.cn:8080/mybook/rest/loginsmsverify")
                .addParams("mobile",number)
                .addParams("sms_code",ID)
                .addParams("type","employee")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //Log.e("登录成功",response);
                        SharedPreferences.Editor editor=sp.edit();
                        JsonObject jsonObject= new JsonParser().parse(response).getAsJsonObject();
                        String message=jsonObject.get("message").getAsString();
                        String status = jsonObject.get("status").getAsString();
                        App.showToast(IDNumberActivity.this,message);
                        if (status.equals("ok")){
                            isLogin=1;
                            //登录成功，跳转到MainActivity
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            String token =data.get("token").getAsString();
                            String apiURL =data.get("api_url").getAsString();
                            String guID =data.get("guid").getAsString();
                            String avator_url = data.get("avator_url").getAsString();
                            int init_login_password=data.get("init_login_password").getAsInt();
                            String displayname=data.get("display_name").getAsString();
                            //保存登录成功状态、基础数据
                            editor.putString("userName", number);
                            editor.putInt("success",1);
                            editor.putString("token", token);
                            editor.putString("apiURL",apiURL);
                            editor.putString("guid",guID);
                            editor.putString("avator_url",avator_url);
                            editor.putString("displayname",displayname);
                            editor.commit();
                            if (init_login_password==0){
                                //预加载
                                LCChatKit.getInstance().setProfileProvider(CustomUserProvider.getInstance(IDNumberActivity.this));
                                //CustomUserProvider customUserProvider=new CustomUserProvider(IDNumberActivity.this);
                                //LCChatKit.getInstance().setProfileProvider(customUserProvider);
                                AVOSCloud.setDebugLogEnabled(true);
                                AVIMClient.setMessageQueryCacheEnable(true);
                                LCChatKit.getInstance().init(getApplicationContext(), APP_ID, APP_KEY);
                                LCChatKit.getInstance().open(guID, new AVIMClientCallback() {
                                    @Override
                                    public void done(AVIMClient avimClient, AVIMException e) {}});
                                Intent intent = new Intent(IDNumberActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Intent intent=new Intent(IDNumberActivity.this, UpdataPasswordActivity.class);
                                intent.putExtra("userName",displayname);
                                intent.putExtra("password",password);
                                startActivity(intent);
                                finish();
                            }

                        }
                    }
                });
    }

    class MyCountDownTimer extends CountDownTimer {
        /**
         *
         * @param millisInFuture
         *      表示以毫秒为单位 倒计时的总数
         *
         *      例如 millisInFuture=1000 表示1秒
         *
         * @param countDownInterval
         *      表示 间隔 多少微秒 调用一次 onTick 方法
         *
         *      例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         *
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            time.setText("");
            if (isLogin==0){
                //进行语音验证
                getData(number);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            time.setText("" + millisUntilFinished / 1000+"秒" );
        }
    }

    //语音获取验证码
    public void getData(String number){
        OkHttpUtils
                .post()
                .url("http://sso.sosys.cn:8080/mybook/rest/loginsmsgot")
                .addParams("mobile",number)
                .addParams("sms_type","voice")
                .addParams("type","employee")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                    }
                });
    }
}
