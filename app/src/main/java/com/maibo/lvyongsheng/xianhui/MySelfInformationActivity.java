package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.userclass.AvatarImageView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by LYS on 2016/11/14.
 */
public class MySelfInformationActivity extends BaseActivity implements View.OnClickListener{
    TextView tv_name,back;
    AvatarImageView avatarImageView;
    SharedPreferences sp,sp1;
    String apiURL;
    String token;
    String avator_url;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_information);
        CloseAllActivity.getScreenManager().pushActivity(this);
        initView();
    }

    private void initView() {
        adapterLitterBar(ll_head);
        tv_name= (TextView) findViewById(R.id.tv_name);
        back= (TextView) findViewById(R.id.back);
        avatarImageView= (AvatarImageView) findViewById(R.id.avatarIv);
        avatarImageView.setTitleColor("#353535");
        avatarImageView.setLineColor("#EBEBEB");
        avatarImageView.setBtnTextColor("#30A2F0");
        avatarImageView.setTitleLineColor("#989898");
        //获取到了图片的正确位置
        sp= getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        sp1=getSharedPreferences("cropPicturnPath",Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
        avator_url = sp.getString("avator_url",null);
        String  myname=sp.getString("displayname",null);
        if (!TextUtils.isEmpty(myname))
            tv_name.setText(myname);
        //下载头像
        if (!TextUtils.isEmpty(avator_url)){
            OkHttpUtils
                    .get()
                    .url(avator_url)
                    .build()
                    .execute(new BitmapCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }
                        @Override
                        public void onResponse(Bitmap response, int id) {
                            Drawable drawable =new BitmapDrawable(response);
                            avatarImageView.setImageDrawable(drawable);
                        }
                    });
        }

        setAfterCrop();
        back.setOnClickListener(this);
    }

    private void setAfterCrop() {
        avatarImageView.setAfterCropListener(new AvatarImageView.AfterCropListener() {
            @Override
            public void afterCrop(Bitmap photo) {
                SharedPreferences.Editor editor=sp.edit();
                editor.putInt("myName",1);
                editor.commit();
                Toast.makeText(MySelfInformationActivity.this,"设置新的头像成功",Toast.LENGTH_SHORT).show();
                String path=sp1.getString("path",null);
                File file=new File(path);
                String filename = file.getName();

                String url=apiURL+"/rest/employee/uploadavator";
                Map<String, Object> map=new HashMap<String, Object>();
                map.put("token",token);
                //上传头像
                //file(url,filename,file,token);
                post_file(url,map,file);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //在拍照、选取照片、裁剪Activity结束后，调用的方法
        if(avatarImageView != null){
            avatarImageView.onActivityResult(requestCode,resultCode,data);
        }
    }

    //带参数上传图片
    protected void post_file(final String url, final Map<String, Object> map, File file) {


        Log.e("上传结果:","URL:"+url+"  MAP:"+map.get("token")+"  File:"+file.toString());
        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(file != null){
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("avator", filename, body);
        }
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                requestBody.addFormDataPart(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }
        Request request = new Request.Builder().url(url).post(requestBody.build()).tag(this).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("上传结果:","失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.e("上传结果:",str);
            }
        });

    }
    public void file(String url,String fileName,File file,String token){
        OkHttpUtils.post()
                .addFile("avator",fileName, file)
                .url(url)
                .addParams("token",token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("上传失败:",e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("上传结果:",response);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
