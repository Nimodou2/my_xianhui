package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by LYS on 2016/11/11.
 */
public class QuitPCActivity extends Activity implements View.OnClickListener{
    TextView back,tv_quite;
    SharedPreferences sp;
    String apiURL;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quit_pc);
        initView();
    }
    private void initView(){
        back= (TextView) findViewById(R.id.back);
        back.setOnClickListener(this);
        tv_quite= (TextView) findViewById(R.id.tv_quite);
        tv_quite.setOnClickListener(this);

        sp= getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.tv_quite:
                String webToken=sp.getString("webtoken",null);
                if (!TextUtils.isEmpty(webToken))  quitPC(webToken);
                else App.showToast(this,"数据异常!");
                break;
        }
    }

    /**
     * 退出PC端
     * @param webtoken
     */
    private void quitPC(final String webtoken){
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("闲惠");
        dialog.setMessage("确定退出PC端吗？");
        dialog.setButton("确定",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                OkHttpUtils
                        .post()
                        .url(apiURL+"/rest/employee/logoutqrdo")
                        .addParams("webtoken",webtoken)
                        .addParams("token",token)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("webtoken",null);
                                editor.commit();
                                App.showToast(QuitPCActivity.this,"退出成功!");
                                finish();
                            }
                        });
            }
        });
        dialog.setButton2("取消",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }
}
