package com.maibo.lvyongsheng.xianhui.serviceholdermessage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    /**
     * 上传当前client对象的会话ID
     * @param conv_id
     */
    public void uploadConversationIdToService(String conv_id) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/setconversationlist")
                .addParams("token", token)
                .addParams("conv_id", conv_id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        EventDatas eventDatas=new EventDatas(Constants.UPLOAD_CONVERSAYION_ID,"error");
                        EventBus.getDefault().post(eventDatas);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject=new JsonParser().parse(response).getAsJsonObject();
                        String status="";String message="";
                        if (!jsonObject.get("status").isJsonNull())
                            status=jsonObject.get("status").getAsString();
                        if (!jsonObject.get("message").isJsonNull())
                            message=jsonObject.get("message").getAsString();
                        if (status.equals("ok")){
                            EventDatas eventDatas=new EventDatas(Constants.UPLOAD_CONVERSAYION_ID,"right");
                            EventBus.getDefault().post(eventDatas);
                        }else{
                            EventDatas eventDatas=new EventDatas(Constants.UPLOAD_CONVERSAYION_ID,message);
                            EventBus.getDefault().post(eventDatas);
                        }
                    }
                });
    }

    /**
     * 获取当前client对象的会话ID
     */
    public void getConversationIDFromService() {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getconversationlist")
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                            EventDatas eventDatas=new EventDatas(Constants.GET_CONVERSATION_ID,"error","");
                            EventBus.getDefault().post(eventDatas);

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("Service",response);
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String status = "";
                        String message = "";
                        if (!jsonObject.get("status").isJsonNull())
                            status = jsonObject.get("status").getAsString();
                        if (!jsonObject.get("message").isJsonNull())
                            message = jsonObject.get("message").getAsString();
                        if (status.equals("ok")) {
                                EventDatas eventDatas=new EventDatas(Constants.GET_CONVERSATION_ID,"right",response);
                                EventBus.getDefault().post(eventDatas);

                        } else {
                                EventDatas eventDatas=new EventDatas(Constants.GET_CONVERSATION_ID,"message",message);
                                EventBus.getDefault().post(eventDatas);
                        }
                    }
                });
    }

}
