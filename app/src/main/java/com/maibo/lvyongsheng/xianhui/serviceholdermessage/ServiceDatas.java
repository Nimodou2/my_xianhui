package com.maibo.lvyongsheng.xianhui.serviceholdermessage;

import android.content.Context;
import android.content.SharedPreferences;

import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import de.greenrobot.event.EventBus;
import okhttp3.Call;

/**
 * Created by LYS on 2016/12/15.
 */

public class ServiceDatas {

    Context context;
    SharedPreferences sp;
    String apiURL;
    String token;

    public ServiceDatas(Context context){
        this.context=context;
        sp = context.getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
    }

    /**
     * 获取新手通知
     */
    public void getSendNoviceTutorialNotice(){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/sendnovicetutorialnotice")
                .addParams("token",token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        EventDatas eventDatas=new EventDatas(Constants.SEND_NOVICE_TUTORIAL_NOTICE,response);
                        EventBus.getDefault().post(eventDatas);
                    }
                });
    }
}
