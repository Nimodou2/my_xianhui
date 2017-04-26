package com.maibo.lvyongsheng.xianhui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2016/10/16.
 */
public class UpdataPasswordActivity extends BaseActivity {

    EditText et_password,et_password_again;
    TextView btn_submit,tv_remind;
    SharedPreferences sp,sp1;
    String apiURL;
    String token;
    ProgressDialog dialog;
    String tag;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updata_password);
        //adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        initView();
        Intent intent=getIntent();
        final String userName=intent.getStringExtra("userName");
        tag=intent.getStringExtra("tag");
        if (tag.equals("changePassword")){
            tv_remind.setVisibility(View.GONE);
        }

        final String publicKey="1addfcf4296d60f0f8e0c81cea87a099";
        final String type="employee";

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str=et_password.getText().toString().trim();
                String str_again=et_password_again.getText().toString().trim();
                if (!str.equals(str_again)){
                    App.showToast(getApplicationContext(),"两次输入内容不相同!");
                }else if (!TextUtils.isEmpty(str)){
                    //提交密码
                    dialog.show();
                    String md5=getSign(userName,type,str,publicKey);
                    setPassword(userName,type,str,md5);
                }else{
                    App.showToast(getApplicationContext(),"不能提交空内容");
                }
            }
        });
    }

    private void initView() {
        et_password= (EditText) findViewById(R.id.et_password);
        btn_submit= (TextView) findViewById(R.id.btn_submit);
        et_password_again= (EditText) findViewById(R.id.et_password_again);
        et_password.setHintTextColor(Color.rgb(186,186,188));
        et_password_again.setHintTextColor(Color.rgb(186,186,188));
        tv_remind= (TextView) findViewById(R.id.tv_remind);
        //适配界面
        setHeightAndWidth();

        sp=getSharedPreferences("baseDate",MODE_PRIVATE);
        sp1=getSharedPreferences("changeAccount",MODE_PRIVATE);

        dialog=new ProgressDialog(this);
        dialog.setMessage("提交中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
    }

    /**
     * 适配界面
     */
    private void setHeightAndWidth() {
        View views[]={et_password,et_password_again,btn_submit};
        int ht=viewHeight*20/255;
        int height[]={ht,ht,ht};
        int widths[]=null;
        setViewHeightAndWidth(views,height,widths);
    }

    public void setPassword(final String username, String type, final String str, String sign){
        OkHttpUtils
                .post()
                .url("http://sso.sosys.cn:8080/mybook/rest/changeloginpassword")
                .addParams("username",username)
                .addParams("type",type)
                .addParams("password",str)
                .addParams("sign",sign)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        SharedPreferences.Editor editor=sp.edit();
                        SharedPreferences.Editor editor1=sp1.edit();
                        JsonObject jsonObject=new JsonParser().parse(response).getAsJsonObject();
                        String msg_status=jsonObject.get("status").getAsString();
                        String message=jsonObject.get("message").getAsString();
                        if (msg_status.equals("ok")){
                            //保存账号和密码
                            editor.putString("userName", username);
                            editor.putString("password", str);
                            editor1.putString("userName", username);
                            editor1.putString("password", str);
                            editor.commit();
                            editor1.commit();
                            //normalPassword:当第一次激活账号时
                            //changePassword:当在应用内修改密码时
                            if (tag.equals("normalPassword")){
                                App.showToast(getApplicationContext(),"请重新登录");
                                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            }else if (tag.equals("changePassword")){
                                App.showToast(getApplicationContext(),"修改成功");
                            }

                            finish();
                        }else{
                            App.showToast(getApplicationContext(),message);
                        }
                        dialog.dismiss();

                    }
                });

    }

    //md5加密
    //md5加密
    //算法：md5(用户名-用户类型-登录密码-date(Ymd)-公钥)
    public String getSign(String userName,String type,String password,String publicKey){
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
        String dateTime = sf.format(new Date());
        String plainText=userName+"-"+type+"-"+password+"-"+dateTime+"-"+publicKey;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //32位加密
            Log.e("组合：",plainText);
            Log.e("md5:",buf.toString());
            return buf.toString();
            // 16位的加密
            //return buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
