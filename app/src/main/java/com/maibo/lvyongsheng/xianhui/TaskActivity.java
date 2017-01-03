package com.maibo.lvyongsheng.xianhui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.adapter.CommonAdapter;
import com.maibo.lvyongsheng.xianhui.adapter.ViewHolder;
import com.maibo.lvyongsheng.xianhui.entity.TaskInfomation;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2016/11/23.
 */

public class TaskActivity extends BaseActivity implements View.OnClickListener{
    TextView back,tv_new_build;
    ListView lv_progress;
    SharedPreferences sp;
    String apiURL;
    String token;
    ProgressDialog dialog;
    List<TaskInfomation> list_task_info;

    @Bind(R.id.ll_head)
    LinearLayout ll_head;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    list_task_info=(List<TaskInfomation>) msg.obj;
                    //开启Adapter
                    setMyAdapte();
                    break;
            }
        }
    };

    /**
     * 可侧滑Adapter
     */
    private void setMyAdapte() {
        lv_progress.setAdapter(new CommonAdapter<TaskInfomation>(TaskActivity.this,list_task_info, R.layout.style_task_progress) {
            @Override
            public void convert(ViewHolder holder, final TaskInfomation tkInfor, int position, View convertView) {
                LinearLayout ll_all=holder.getView(R.id.ll_all);
                ViewGroup.LayoutParams params=ll_all.getLayoutParams();
                params.height=viewHeight/9;
                ll_all.setLayoutParams(params);

                ImageView iv_left_task=holder.getView(R.id.iv_left_task);
                ViewGroup.LayoutParams params1=iv_left_task.getLayoutParams();
                params1.height=viewHeight/9;
                iv_left_task.setLayoutParams(params1);

                String values="范围:"+tkInfor.getRange_name()+"  类型:"+tkInfor.getType_name()+"  截止:"+tkInfor.getEnd_date().substring(5);
                holder.setText(R.id.tv_progress_cotent,values);
                int progress=Integer.parseInt(tkInfor.getPercentage());
                holder.setText(R.id.tv_progress_value,progress+"%");
                holder.setProgress(R.id.pb_progressbar,progress);

                //点击条目跳转到查看界面
                holder.setOnClickListener(R.id.ll_all, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //跳转前带上相关参数
                        Intent intent=new Intent(TaskActivity.this,TaskProgressDetailActivity.class);
                        intent.putExtra("task_id",tkInfor.getTask_id());
                        startActivityForResult(intent,11);

                    }
                });
                if (tkInfor.getIs_update().equals("0")){
                    holder.setVisible(R.id.tv_isupdate,false);
                }else if (tkInfor.getIs_update().equals("1")){
                    holder.setVisible(R.id.tv_isupdate,true);
                }

                //点击置顶
                final String task_id=tkInfor.getTask_id();
                holder.setOnClickListener(R.id.bt_top, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //目前只有置顶任务，刷新适配器
                         setTopTask(task_id);

                    }
                });

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        getTaskListFromService();
    }
    private void initView(){
        setContentView(R.layout.activity_progress);
        adapterLitterBar(ll_head);

        CloseAllActivity.getScreenManager().pushActivity(this);

        dialog=new ProgressDialog(this);
        dialog.setMessage("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
        dialog.show();

        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);

        back= (TextView) findViewById(R.id.back);
        tv_new_build= (TextView) findViewById(R.id.tv_new_build);
        lv_progress= (ListView) findViewById(R.id.lv_progress);
        back.setOnClickListener(this);
        tv_new_build.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.tv_new_build:
                //跳转到新建界面
                Intent intent=new Intent(this,NewBuildTaskActivity.class);
                startActivityForResult(intent,6);
                //控制Activity出现方式
//                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
                break;
        }
    }

    /**
     * 从服务器获取任务列表
     */
    private void getTaskListFromService(){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/gettasklist")
                .addParams("token",token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject=new JsonParser().parse(response).getAsJsonObject();
                        String status="";
                        String message="";
                        if (!jsonObject.get("status").isJsonNull()){
                            status=jsonObject.get("status").getAsString();
                        }
                        if (!jsonObject.get("message").isJsonNull()){
                            message=jsonObject.get("message").getAsString();
                        }
                        if (status.equals("ok")){
                            JsonObject data=jsonObject.get("data").getAsJsonObject();
                            if (data!=null){
                                JsonArray rows=data.get("rows").getAsJsonArray();
                                List<TaskInfomation> list=new ArrayList<TaskInfomation>();
                                for (JsonElement je:rows){
                                    JsonObject jo=je.getAsJsonObject();
                                    String task_id="";
                                    String start_date="";
                                    String end_date="";
                                    String publish_date="";
                                    String type="";
                                    String range="";
                                    String percentage="";
                                    String is_update="";
                                    String target="";
                                    String range_name="";
                                    String type_name="";
                                    if (!jo.get("task_id").isJsonNull())
                                        task_id=jo.get("task_id").getAsString();
                                    if (!jo.get("start_date").isJsonNull())
                                        start_date=jo.get("start_date").getAsString();
                                    if (!jo.get("end_date").isJsonNull())
                                        end_date=jo.get("end_date").getAsString();
                                    if (!jo.get("publish_date").isJsonNull())
                                        publish_date=jo.get("publish_date").getAsString();
                                    if (!jo.get("type").isJsonNull())
                                        type=jo.get("type").getAsString();
                                    if (!jo.get("range").isJsonNull())
                                        range=jo.get("range").getAsString();
                                    if (!jo.get("percentage").isJsonNull())
                                        percentage=jo.get("percentage").getAsString();
                                    if (!jo.get("is_update").isJsonNull())
                                        is_update=jo.get("is_update").getAsString();
                                    if (!jo.get("target").isJsonNull())
                                        target=jo.get("target").getAsString();
                                    if (!jo.get("range_name").isJsonNull())
                                        range_name=jo.get("range_name").getAsString();
                                    if (!jo.get("type_name").isJsonNull())
                                        type_name=jo.get("type_name").getAsString();
                                    list.add(new TaskInfomation(task_id,start_date,end_date,publish_date,type,
                                            range,percentage,is_update,target,range_name,type_name));
                                }
                                //回传数据
                                Message msg=Message.obtain();
                                msg.what=0;
                                msg.obj=list;
                                handler.sendMessage(msg);
                            }
                            dialog.dismiss();

                        }else{
                            App.showToast(getApplicationContext(),message);
                            dialog.dismiss();
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 7:
                getTaskListFromService();
                break;
            case 12:
                getTaskListFromService();
                break;
            case  17:
                //首先判断有没有update为一的值
                for (int i=0;i<list_task_info.size();i++){
                    if (list_task_info.get(i).getIs_update().equals("1")){
                        getTaskListFromService();
                        return;
                    }
                }
                break;
        }
    }

    /**
     * 上传置顶任务
     * @param task_id
     */
    private void setTopTask(String task_id){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/settoptask")
                .addParams("token",token)
                .addParams("task_id",task_id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject=new JsonParser().parse(response).getAsJsonObject();
                        String status="";
                        String message="";
                        if (!jsonObject.get("status").isJsonNull())
                            status=jsonObject.get("status").getAsString();
                        if (!jsonObject.get("message").isJsonNull())
                            message=jsonObject.get("message").getAsString();
                        if (status.equals("ok")){
                            //重新请求服务器
                            getTaskListFromService();
                        }else{
                            App.showToast(getApplicationContext(),message);
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
