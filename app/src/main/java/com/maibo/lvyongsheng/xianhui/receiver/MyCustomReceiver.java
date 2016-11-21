package com.maibo.lvyongsheng.xianhui.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.maibo.lvyongsheng.xianhui.MainActivity;
import com.maibo.lvyongsheng.xianhui.R;

import org.json.JSONObject;



public class MyCustomReceiver extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";
    SharedPreferences sp;
    final int mNotificationId = 1314;
    @Override
    public void onReceive(Context context, Intent intent) {
        sp=context.getSharedPreferences("baseDate",context.MODE_PRIVATE);
            try {
                String action = intent.getAction();
                String channel = intent.getExtras().getString("com.avos.avoscloud.Channel");
                //获取消息内容
                JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));
                //Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
               // Log.e("消息：","Action: " + action + " ;channel: " + channel+" data: "+json.getString("alert"));
                //1、解析消息体
                String jsonBody=intent.getExtras().getString("com.avos.avoscloud.Data");
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("jsonBody",jsonBody);
                editor.commit();
                String alert=json.getString("alert");
                String notice_type=json.getString("notice_type");
                String notice_time=json.getString("notice_time");
                String notice_id=json.getString("notice_id");
                //根据不同的notice_type，发送不同通知
                if (notice_type.equals("daily_report")){
                    //日报表
                    sendNotifacation(context, alert, notice_type, notice_id, MainActivity.class);
                }else if (notice_type.equals("project_plan")){
                    //项目计划
                    sendNotifacation(context, alert, notice_type, notice_id, MainActivity.class);
                }else if (notice_type.equals("common_notice")){
                    sendNotifacation(context, alert, notice_type, notice_id, MainActivity.class);
                }
                //2、发通知

            } catch (Exception e) {
                Log.d(TAG, "JSONException: " + e.getMessage());
            }
        }

    private void sendNotifacation(Context context, String alert, String notice_type, String notice_id,Class clazz) {
        Intent resultIntent = new Intent(context, clazz);
        resultIntent.putExtra("notice_id",notice_id);
        resultIntent.putExtra("notice_type",notice_type);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(alert)
                        .setTicker("您有一条新消息!")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
        NotificationManager mNotifyMgr = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(0, mBuilder.build());
    }
}
