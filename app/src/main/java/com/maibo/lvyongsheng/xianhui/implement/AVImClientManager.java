package com.maibo.lvyongsheng.xianhui.implement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientEventHandler;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.maibo.lvyongsheng.xianhui.CustomUserProvider;
import com.maibo.lvyongsheng.xianhui.LoginActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by LYS on 2016/12/20.
 */

public class AVImClientManager extends AVIMClientEventHandler {
    Activity mow_activity;
    String apiURL;
    String token;
    SharedPreferences sp1, sp2, sp3, sp4, sp5,sp6;
    Context appContext;

    public AVImClientManager(Context appContext) {
        this.appContext=appContext;
    }

    @Override
    public void onConnectionPaused(AVIMClient avimClient) {

    }

    @Override
    public void onConnectionResume(AVIMClient avimClient) {

    }

    @Override
    public void onClientOffline(AVIMClient avimClient, int i) {
        sp1 = appContext.getSharedPreferences("baseDate", MODE_PRIVATE);
        sp2 = appContext.getSharedPreferences("checkBoxProduct", MODE_PRIVATE);
        sp3 = appContext.getSharedPreferences("checkBox", MODE_PRIVATE);
        sp4 = appContext.getSharedPreferences("changeAccount", MODE_PRIVATE);
        sp5 = appContext.getSharedPreferences("app", MODE_PRIVATE);
        sp6 = appContext.getSharedPreferences("dataBase", Context.MODE_PRIVATE);

        if (i == 4111) {
            if (!sp1.getBoolean("isDestroyMainActivity", false)) {
                mow_activity = CloseAllActivity.getScreenManager().getFirstActivity();
                apiURL = sp1.getString("apiURL", null);
                token = sp1.getString("token", null);

                if (mow_activity != null) {
                    final AlertDialog ad = new AlertDialog.Builder(mow_activity).create();
                    ad.setCancelable(false);
                    ad.setTitle("提示");
                    ad.setMessage("帐号在别处登录");
                    ad.setButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ad.dismiss();
                            //退出应用
                            quitApplication(mow_activity);
                        }
                    });
                    ad.setButton2("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ad.dismiss();
                            //退出应用
                            quitApplication(mow_activity);
                        }
                    });
                    ad.show();
                } else {

                }
            }
        }

    }

    /**
     * 退出应用
     */
    private void quitApplication(Activity mow_activity) {
        //关闭推送服务
        String guid_close = sp1.getString("guid", null);
        if (guid_close != null)
            closeLeancloud(guid_close);
        //退出应用,清除所用缓存
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
//                        Log.e("AVIMClient:", response);
                    }
                });

        CloseAllActivity.getScreenManager().clearAllActivity();
        //跳转到SplashActivity界面
        mow_activity.startActivity(new Intent(mow_activity, LoginActivity.class));

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
                            }
                        }
                    });
                }
            }
        });
    }

}
