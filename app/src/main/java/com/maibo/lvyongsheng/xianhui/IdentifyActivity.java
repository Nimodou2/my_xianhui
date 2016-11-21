package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by LYS on 2016/9/23.
 */
public class IdentifyActivity extends Activity {

    EditText et;
    TextView tv,back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        et=(EditText) findViewById(R.id.et_phone_number);
        et.setHintTextColor(Color.rgb(186,186,188));
        tv=(TextView) findViewById(R.id.tv_get_identy);
        back= (TextView) findViewById(R.id.back);
        Intent intent=getIntent();
        final String password=intent.getStringExtra("password");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number=et.getText().toString().trim();
                //请求服务器进行短信验证
                if (number!=null){
                    getData(number);
                    Intent intent=new Intent(IdentifyActivity.this, IDNumberActivity.class);
                    intent.putExtra("phoneNumber",number);
                    intent.putExtra("password",password);
                    startActivity(intent);
                    finish();
                }else{
                    App.showToast(getApplicationContext(),"请输入内容!");
                }

            }
        });
    }
    public void getData(String number){
        OkHttpUtils
                .post()
                .url("http://sso.sosys.cn:8080/mybook/rest/loginsmsgot")
                .addParams("mobile",number)
                .addParams("sms_type","sms")
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
