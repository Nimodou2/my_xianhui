package com.maibo.lvyongsheng.xianhui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.utils.PhoneFormatCheckUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2016/9/23.
 */
public class IdentifyActivity extends BaseActivity {

    EditText et;
    TextView tv,back;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    private static final String TAG="IdentifyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        //adapterLitterBar(ll_head);

        CloseAllActivity.getScreenManager().pushActivity(this);

        et=(EditText) findViewById(R.id.et_phone_number);
        et.setHintTextColor(Color.rgb(186,186,188));
        setSingleViewHeightAndWidth(et,viewHeight*20/255,0);
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
                    //首先判断是否为电话号码
                    Boolean isPhone= PhoneFormatCheckUtils.isPhoneLegal(number);
                    if (isPhone){
                        getData(number);
                        Intent intent=new Intent(IdentifyActivity.this, IDNumberActivity.class);
                        intent.putExtra("phoneNumber",number);
                        intent.putExtra("password",password);
                        startActivity(intent);
//                        finish();
                    }else{
                        App.showToast(getApplication(),"号码错误！");
                    }
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
                        Log.e(TAG," 短信验证的返回结果"+response);
                    }
               });
    }
}
