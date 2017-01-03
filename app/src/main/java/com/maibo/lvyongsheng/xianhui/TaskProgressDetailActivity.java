package com.maibo.lvyongsheng.xianhui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.adapter.TaskProgressDetailAdapter;
import com.maibo.lvyongsheng.xianhui.entity.People;
import com.maibo.lvyongsheng.xianhui.entity.Project;
import com.maibo.lvyongsheng.xianhui.entity.Task;
import com.maibo.lvyongsheng.xianhui.entity.TaskInfomation;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2016/11/25.
 */

public class TaskProgressDetailActivity extends BaseActivity implements View.OnClickListener {
    SharedPreferences sp;
    String apiURL;
    String token;
    ProgressDialog dialog;

    TextView back, tv_see, tv_progress_cotent, tv_progress_value;
    ProgressBar pb_progressbar;
    ListView lv_item_list;

    RadioGroup rg_menu;
    RadioButton rb_adviser, rb_technician, rb_customer;

    List<TaskInfomation> taskInfor_list;
    TaskInfomation tkInfor, toNewBulidTask;

    int isHaveData = 0;
    String task_ids;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.ll_rg_bg)
    LinearLayout ll_rg_bg;
    @Bind(R.id.ll_all)
    LinearLayout ll_all;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //此处为完整数据
                    taskInfor_list = (List<TaskInfomation>) msg.obj;
                    tkInfor = taskInfor_list.get(0);
                    isHaveData = 1;
                    //此处在Listview中展示数据
                    //第一次进入时展示的是顾问相关数据
                    lv_item_list.setAdapter(new TaskProgressDetailAdapter(getApplicationContext(), TaskProgressDetailActivity.this, tkInfor.getAdviser_list(), -1,viewHeight));
                    showDatas();
                    break;
                case 1:
                    //此处缺少最后三项数据
                    taskInfor_list = (List<TaskInfomation>) msg.obj;
                    tkInfor = taskInfor_list.get(0);
                    isHaveData = 0;
                    showDatas();
                    break;
                case 2:
                    toNewBulidTask = (TaskInfomation) msg.obj;
                    //跳转到新建项目界面，必须带上基本数据
                    Intent intent = new Intent(TaskProgressDetailActivity.this, NewBuildTaskActivity.class);
                    //准备参数
                    Bundle bundle = new Bundle();
                    bundle.putString("tag", "update");
                    bundle.putSerializable("toNewBulidTask", toNewBulidTask);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 8);
                    break;
            }
        }
    };

    /**
     * 首次展示数据
     */
    private void showDatas() {
        //首先展示任务进度相关数据
        String values = "范围:" + tkInfor.getRange_name() + "  类型:" + tkInfor.getType_name() + "  截止:" + tkInfor.getEnd_date().substring(5);
        tv_progress_cotent.setText(values);
        int progress = Integer.parseInt(tkInfor.getPercentage());
        tv_progress_value.setText(progress + "%");
        pb_progressbar.setProgress(progress);
        dialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_progress_detail);
        adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        initView();
        getTaskListDetailFromService(task_ids);
    }

    private void initView() {

        dialog = new ProgressDialog(this);
        dialog.setMessage("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
        dialog.show();

        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);

        back = (TextView) findViewById(R.id.back);
        tv_see = (TextView) findViewById(R.id.tv_see);
        tv_progress_cotent = (TextView) findViewById(R.id.tv_progress_cotent);
        tv_progress_value = (TextView) findViewById(R.id.tv_progress_value);
        pb_progressbar = (ProgressBar) findViewById(R.id.pb_progressbar);
        lv_item_list = (ListView) findViewById(R.id.lv_item_list);


        rg_menu = (RadioGroup) findViewById(R.id.rg_menu);
        rb_adviser = (RadioButton) findViewById(R.id.rb_adviser);
        rb_technician = (RadioButton) findViewById(R.id.rb_technician);
        rb_customer = (RadioButton) findViewById(R.id.rb_customer);
        //进行界面的适配
        setHeightAndWidth();

        back.setOnClickListener(this);
        tv_see.setOnClickListener(this);
        myCheckedChange();
        Intent intent = getIntent();
        task_ids = intent.getStringExtra("task_id");
    }

    private void setHeightAndWidth() {
        View[] views=new View[3];
        int height[]=new int[3];
        int width[] =null;
        views[0]=ll_all;
        views[1]=ll_rg_bg;
        views[2]=rg_menu;
        height[0]=viewHeight/9;
        height[1]=viewHeight/9;
        height[2]=viewHeight*4/63;
        setViewHeightAndWidth(views,height,width);
    }

    /**
     * 获取任务信息
     *
     * @param task_id
     */
    private void getTaskListDetailFromService(String task_id) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/gettasklistdetail")
                .addParams("token", token)
                .addParams("task_id", task_id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();

                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.e("response111",response);
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        List<TaskInfomation> task_info_list = new ArrayList<TaskInfomation>();
                        String status = "";
                        String message = "";
                        if (!jsonObject.get("status").isJsonNull())
                            status = jsonObject.get("status").getAsString();
                        if (!jsonObject.get("message").isJsonNull())
                            message = jsonObject.get("message").getAsString();
                        if (status.equals("ok")) {
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            if (data != null) {
                                String task_id = "";
                                String start_date = "";
                                String end_date = "";
                                String publish_date = "";
                                String type = "";
                                String range = "";
                                String percentage = "";
                                String is_update = "";
                                String target = "";
                                String range_name = "";
                                String type_name = "";
                                String org_id = "";
                                String reality = "";
                                if (!data.get("task_id").isJsonNull())
                                    task_id = data.get("task_id").getAsString();
                                if (!data.get("org_id").isJsonNull())
                                    org_id = data.get("org_id").getAsString();
                                if (!data.get("start_date").isJsonNull())
                                    start_date = data.get("start_date").getAsString();
                                if (!data.get("end_date").isJsonNull())
                                    end_date = data.get("end_date").getAsString();
                                if (!data.get("publish_date").isJsonNull())
                                    publish_date = data.get("publish_date").getAsString();
                                if (!data.get("type").isJsonNull())
                                    type = data.get("type").getAsString();
                                if (!data.get("range").isJsonNull())
                                    range = data.get("range").getAsString();
                                if (!data.get("percentage").isJsonNull())
                                    percentage = data.get("percentage").getAsString();
                                if (!data.get("is_update").isJsonNull())
                                    is_update = data.get("is_update").getAsString();
                                if (!data.get("target").isJsonNull())
                                    target = data.get("target").getAsString();
                                if (!data.get("reality").isJsonNull())
                                    reality = data.get("reality").getAsString();
                                if (!data.get("range_name").isJsonNull())
                                    range_name = data.get("range_name").getAsString();
                                if (!data.get("type_name").isJsonNull())
                                    type_name = data.get("type_name").getAsString();

                                if (data.has("adviser_list")) {
                                    //此处代表完成任务
                                    JsonArray adviser_list = data.get("adviser_list").getAsJsonArray();
                                    JsonArray engineer_list = data.get("engineer_list").getAsJsonArray();
                                    JsonArray customer_list = data.get("customer_list").getAsJsonArray();
                                    //解析adviser_list
                                    List<People> adviser_people_list = new ArrayList<People>();
                                    for (JsonElement je0 : adviser_list) {
                                        JsonObject jo0 = je0.getAsJsonObject();

                                        String ids = "";
                                        String name = "";
                                        String amount = "";
                                        if (!jo0.get("id").isJsonNull())
                                            ids = jo0.get("id").getAsString();
                                        if (!jo0.get("name").isJsonNull())
                                            name = jo0.get("name").getAsString();
                                        if (!jo0.get("amount").isJsonNull())
                                            amount = jo0.get("amount").getAsString();
                                        JsonArray detail = jo0.get("detail").getAsJsonArray();
                                        List<Project> project_list = new ArrayList<Project>();
                                        for (JsonElement je1 : detail) {
                                            JsonObject jo1 = je1.getAsJsonObject();
                                            Project project = new Project();
                                            int ids1 = -1;
                                            String name1 = "";
                                            String amount1 = "";
                                            if (!jo1.get("id").isJsonNull())
                                                ids1 = jo1.get("id").getAsInt();
                                            project.setItem_id(ids1);
                                            if (!jo1.get("name").isJsonNull())
                                                name1 = jo1.get("name").getAsString();
                                            project.setFullname(name1);
                                            if (!jo1.get("amount").isJsonNull())
                                                amount1 = jo1.get("amount").getAsString();
                                            project.setAmount(amount1);
                                            project_list.add(project);
                                        }
                                        adviser_people_list.add(new People(ids, name, amount, project_list));

                                    }
                                    //解析engineer_list
                                    List<People> engineer_people_list = new ArrayList<People>();
                                    for (JsonElement je0 : engineer_list) {
                                        JsonObject jo0 = je0.getAsJsonObject();
                                        String ids = "";
                                        String name = "";
                                        String amount = "";
                                        if (!jo0.get("id").isJsonNull())
                                            ids = jo0.get("id").getAsString();
                                        if (!jo0.get("name").isJsonNull())
                                            name = jo0.get("name").getAsString();
                                        if (!jo0.get("amount").isJsonNull())
                                            amount = jo0.get("amount").getAsString();
                                        JsonArray detail = jo0.get("detail").getAsJsonArray();
                                        List<Project> project_list = new ArrayList<Project>();
                                        for (JsonElement je1 : detail) {
                                            JsonObject jo1 = je1.getAsJsonObject();
                                            Project project = new Project();
                                            int ids1 = -1;
                                            String name1 = "";
                                            String amount1 = "";
                                            if (!jo1.get("id").isJsonNull())
                                                ids1 = jo1.get("id").getAsInt();
                                            project.setItem_id(ids1);
                                            if (!jo1.get("name").isJsonNull())
                                                name1 = jo1.get("name").getAsString();
                                            project.setFullname(name1);
                                            if (!jo1.get("amount").isJsonNull())
                                                amount1 = jo1.get("amount").getAsString();
                                            project.setAmount(amount1);
                                            project_list.add(project);
                                        }
                                        engineer_people_list.add(new People(ids, name, amount, project_list));
                                    }
                                    //解析customer_list
                                    List<People> customer_people_list = new ArrayList<People>();
                                    for (JsonElement je0 : customer_list) {
                                        JsonObject jo0 = je0.getAsJsonObject();
                                        String ids = "";
                                        String name = "";
                                        String amount = "";
                                        if (!jo0.get("id").isJsonNull())
                                            ids = jo0.get("id").getAsString();
                                        if (!jo0.get("name").isJsonNull())
                                            name = jo0.get("name").getAsString();
                                        if (!jo0.get("amount").isJsonNull())
                                            amount = jo0.get("amount").getAsString();
                                        JsonArray detail = jo0.get("detail").getAsJsonArray();
                                        List<Project> project_list = new ArrayList<Project>();
                                        for (JsonElement je1 : detail) {
                                            JsonObject jo1 = je1.getAsJsonObject();
                                            Project project = new Project();
                                            int ids1 = -1;
                                            String name1 = "";
                                            String amount1 = "";
                                            if (!jo1.get("id").isJsonNull())
                                                ids1 = jo1.get("id").getAsInt();
                                            project.setItem_id(ids1);
                                            if (!jo1.get("name").isJsonNull())
                                                name1 = jo1.get("name").getAsString();
                                            project.setFullname(name1);
                                            if (!jo1.get("amount").isJsonNull())
                                                amount1 = jo1.get("amount").getAsString();
                                            project.setAmount(amount1);
                                            project_list.add(project);
                                        }
                                        customer_people_list.add(new People(ids, name, amount, project_list));
                                    }
                                    //此处上传完整数据
                                    task_info_list.add(new TaskInfomation(task_id, start_date, end_date, publish_date, type,
                                            range, percentage, is_update, target, range_name, type_name, org_id, reality,
                                            adviser_people_list, engineer_people_list, customer_people_list));
                                    Message msg = Message.obtain();
                                    msg.what = 0;
                                    msg.obj = task_info_list;
                                    handler.sendMessage(msg);

                                } else {
                                    //此处代表没有完成任务
                                    task_info_list.add(new TaskInfomation(task_id, start_date, end_date, publish_date, type,
                                            range, percentage, is_update, target, range_name, type_name, org_id, reality));
                                    Message msg1 = Message.obtain();
                                    msg1.what = 1;
                                    msg1.obj = task_info_list;
                                    handler.sendMessage(msg1);
                                }
                            } else {
                                App.showToast(getApplicationContext(), "暂无数据");
                                dialog.dismiss();
                            }
                        } else {
                            App.showToast(getApplicationContext(), message);
                            dialog.dismiss();
                        }
                    }
                });
    }

    /**
     * RadioButton
     */
    private void myCheckedChange() {
        rg_menu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_adviser:
                        rb_adviser.setTextColor(Color.WHITE);
                        rb_technician.setTextColor(Color.rgb(35, 108, 81));
                        rb_customer.setTextColor(Color.rgb(35, 108, 81));
                        if (isHaveData == 1) {
                            if (tkInfor.getAdviser_list().size() > 0) {
                                lv_item_list.setAdapter(new TaskProgressDetailAdapter(getApplicationContext(), TaskProgressDetailActivity.this, tkInfor.getAdviser_list(), 0,viewHeight));
                            }
                        }

                        break;
                    case R.id.rb_technician:
                        rb_adviser.setTextColor(Color.rgb(35, 108, 81));
                        rb_technician.setTextColor(Color.WHITE);
                        rb_customer.setTextColor(Color.rgb(35, 108, 81));
                        if (isHaveData == 1) {
                            if (tkInfor.getEngineer_list().size() > 0) {
                                lv_item_list.setAdapter(new TaskProgressDetailAdapter(getApplicationContext(), TaskProgressDetailActivity.this, tkInfor.getEngineer_list(), 1,viewHeight));
                            }
                        }

                        break;
                    case R.id.rb_customer:
                        rb_adviser.setTextColor(Color.rgb(35, 108, 81));
                        rb_technician.setTextColor(Color.rgb(35, 108, 81));
                        rb_customer.setTextColor(Color.WHITE);
                        if (isHaveData == 1) {
                            if (tkInfor.getCustomer_list().size() > 0) {
                                lv_item_list.setAdapter(new TaskProgressDetailAdapter(getApplicationContext(), TaskProgressDetailActivity.this, tkInfor.getCustomer_list(), 2,viewHeight));
                            }
                        }

                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                setResult(17);
                finish();
                break;
            case R.id.tv_see:
                getTaskDetailFromService(task_ids);
                break;
        }
    }


    /**
     * 从服务器获取要传入到新建界面的数据
     *
     * @param task_id
     */
    private void getTaskDetailFromService(String task_id) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/gettaskdetail")
                .addParams("token", token)
                .addParams("task_id", task_id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String status = "";
                        String message = "";
                        if (!jsonObject.get("status").isJsonNull())
                            status = jsonObject.get("status").getAsString();
                        if (!jsonObject.get("message").isJsonNull())
                            message = jsonObject.get("message").getAsString();
                        if (status.equals("ok")) {
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            if (!data.isJsonNull()) {
                                String task_id = "";

                                String type_value = "";
                                String type_text = "";
                                String range_value = "";
                                String range_text = "";

                                String target = "";
                                String start_data = "";
                                String end_data = "";

                                String publish_data = "";
                                String note = "";
                                if (!data.get("task_id").isJsonNull())
                                    task_id = data.get("task_id").getAsString();
                                JsonObject type = data.get("type").getAsJsonObject();
                                if (!type.get("value").isJsonNull())
                                    type_value = type.get("value").getAsString();
                                if (!type.get("text").isJsonNull())
                                    type_text = type.get("text").getAsString();
                                JsonObject range = data.get("range").getAsJsonObject();
                                if (!range.get("value").isJsonNull())
                                    range_value = range.get("value").getAsString();
                                if (!range.get("text").isJsonNull())
                                    range_text = range.get("text").getAsString();
                                if (!data.get("target").isJsonNull())
                                    target = data.get("target").getAsString();
                                if (!data.get("start_date").isJsonNull())
                                    start_data = data.get("start_date").getAsString();
                                if (!data.get("end_date").isJsonNull())
                                    end_data = data.get("end_date").getAsString();
                                JsonArray user_list = data.get("user_list").getAsJsonArray();

                                List<Task> user_list_task = new ArrayList<Task>();
                                for (JsonElement je : user_list) {
                                    JsonObject jo = je.getAsJsonObject();
                                    String user_value = "";
                                    String user_text = "";
                                    if (!jo.get("value").isJsonNull())
                                        user_value = jo.get("value").getAsString();
                                    if (!jo.get("text").isJsonNull())
                                        user_text = jo.get("text").getAsString();
                                    user_list_task.add(new Task(user_text, user_value));
                                }
                                if (!data.get("publish_date").isJsonNull())
                                    publish_data = data.get("publish_date").getAsString();
                                if (!data.get("note").isJsonNull())
                                    note = data.get("note").getAsString();
                                TaskInfomation taskInfomation = new TaskInfomation(task_id, new Task(type_text,
                                        type_value), new Task(range_text, range_value), target, start_data,
                                        end_data, user_list_task, publish_data, note);
                                Message msg = Message.obtain();
                                msg.what = 2;
                                msg.obj = taskInfomation;
                                handler.sendMessage(msg);

                            }
                        } else {
                            App.showToast(getApplicationContext(), message);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 9:
                //刷新界面
                getTaskListDetailFromService(task_ids);
                break;
            case 10:
                //立刻结束当前界面，并跳转到TaskActivity界面

                setResult(12);
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(17);
            finish();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
