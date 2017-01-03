package com.maibo.lvyongsheng.xianhui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.entity.Task;
import com.maibo.lvyongsheng.xianhui.entity.TaskInfomation;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.implement.MyProgressDialog;
import com.maibo.lvyongsheng.xianhui.view.HPEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2016/11/23.
 */

public class NewBuildTaskActivity extends BaseActivity implements View.OnClickListener {
    TextView back, tv_save, tv_range, tv_type, tv_start, tv_finish, tv_publish_time,
            tv_joiner, tv_update, tv_cancel, tv_update_to_save, tv_delete, tv_last, cus_name;
    LinearLayout ll_range, ll_type, ll_target,ll_start, ll_finish, ll_joiner, ll_two;
    EditText et_remark;
    HPEditText et_target;
    ImageView iv_range, iv_type, iv_joiner;

    MyProgressDialog myDialog;
    SharedPreferences sp;
    String apiURL;
    String token;
    int year, month, day;
    int start_year, start_month, start_day;
    int finish_year, finish_month, finish_day;

    String range_save = "";//待上传范围值
    String type_save = "";//待上传类型值
    String joiner_save = "";//待上传操作对象（此处为多值）
    String start_time = "";//带上传开始时间
    String finish_time = "";//待上传结束时间

    String range_text = "";//填充范围数据
    String type_text = "";//填充类型数据
    String joiner_text = "";//填充参与者数据

    String remark = "";//待上传备注数据
    String target = "";//待上传目标数据
    String task_id = "";

    List<Task> task_type, task_range, list_user;
    TaskInfomation taskInfomation;
    int isFirstIn = 0;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.scroll_view)
    ScrollView scroll_view;
    @Bind(R.id.spacing1)
    TextView spacing1;
    @Bind(R.id.spacing2)
    TextView spacing2;
    @Bind(R.id.spacing3)
    TextView spacing3;
    @Bind(R.id.spacing4)
    TextView spacing4;
    @Bind(R.id.spacing5)
    TextView spacing5;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //任务类型列表
                    task_type = (List<Task>) msg.obj;
                    break;
                case 1:
                    //任务范围
                    task_range = (List<Task>) msg.obj;
                    break;
                case 2:
                    //参与者列表
                    list_user = (List<Task>) msg.obj;
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        getTaskListFromService();
        getUserList();
    }

    private void initView() {
        setContentView(R.layout.activity_new_build_task);

        adapterLitterBar(ll_head);
        scroll_view.smoothScrollTo(0,20);
        CloseAllActivity.getScreenManager().pushActivity(this);
        myDialog = new MyProgressDialog(this);
        myDialog.show();
        back = (TextView) findViewById(R.id.back);
        tv_save = (TextView) findViewById(R.id.tv_save);

        tv_range = (TextView) findViewById(R.id.tv_range);
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_start = (TextView) findViewById(R.id.tv_start);
        tv_finish = (TextView) findViewById(R.id.tv_finish);
        tv_publish_time = (TextView) findViewById(R.id.tv_publish_time);
        tv_joiner = (TextView) findViewById(R.id.tv_joiner);
        tv_update = (TextView) findViewById(R.id.tv_update);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_update_to_save = (TextView) findViewById(R.id.tv_update_to_save);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_last = (TextView) findViewById(R.id.tv_last);
        iv_range = (ImageView) findViewById(R.id.iv_range);
        iv_type = (ImageView) findViewById(R.id.iv_type);
        iv_joiner = (ImageView) findViewById(R.id.iv_joiner);
        cus_name = (TextView) findViewById(R.id.cus_name);

        ll_range = (LinearLayout) findViewById(R.id.ll_range);
        ll_type = (LinearLayout) findViewById(R.id.ll_type);
        ll_target= (LinearLayout) findViewById(R.id.ll_target);
        ll_start = (LinearLayout) findViewById(R.id.ll_start);
        ll_finish = (LinearLayout) findViewById(R.id.ll_finish);
        ll_joiner = (LinearLayout) findViewById(R.id.ll_joiner);
        ll_two = (LinearLayout) findViewById(R.id.ll_two);

        et_target = (HPEditText) findViewById(R.id.et_target);
        et_remark = (EditText) findViewById(R.id.et_remark);

        //适配
        setHeightAndWidth();


        back.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        tv_update.setOnClickListener(this);
        tv_update_to_save.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        ll_range.setOnClickListener(this);
        ll_type.setOnClickListener(this);
        ll_joiner.setOnClickListener(this);
        ll_start.setOnClickListener(this);
        ll_finish.setOnClickListener(this);
        tv_delete.setOnClickListener(this);

        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);

        //初始化日历时间
        Calendar ca = Calendar.getInstance();
        year = ca.get(Calendar.YEAR);
        month = ca.get(Calendar.MONTH);
        day = ca.get(Calendar.DAY_OF_MONTH);
        start_day = day;
        start_month = month;
        start_year = year;
//        Log.e("month",month+"");

        //下四句为刚进入Activity时，初始化“开始”、“结束”、“发布时间”的值。
        tv_start.setText((month + 1) + "月" + day + "日");

        dealLittleTen(year, month, day, "start");
        dealSpelTime(year, month, day);
        tv_publish_time.setText((month + 1) + "月" + day + "日");
        initUpdateDatas();
    }

    /**
     * 适配当前界面
     */
    private void setHeightAndWidth() {
        View views[]={spacing1,spacing2,spacing3,spacing4,spacing5,tv_last,ll_range,
                ll_type, ll_target,ll_start, ll_finish, ll_joiner, ll_two,et_remark};
        int ht1=viewHeight*15/255;
        int ht2=viewHeight*20/255;
        int ht3=viewHeight*40/255;
        int heights[]={ht1,ht1,ht1,ht1,ht1,ht1,ht2,ht2,ht2,ht2,ht2,ht2,ht2,ht3};
        int widths[]=null;
        setViewHeightAndWidth(views,heights,widths);
    }

    /**
     * 通过查看进入的
     */
    private void initUpdateDatas() {
        //通过查看进入该界面的
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String tag = bundle.getString("tag");
            taskInfomation = (TaskInfomation) bundle.get("toNewBulidTask");
            //除了删除外，整个界面处于不可点击状态
            task_id = taskInfomation.getTask_id();
            cus_name.setText("任务");

            tv_save.setVisibility(View.GONE);
            tv_update.setVisibility(View.VISIBLE);
            tv_delete.setVisibility(View.VISIBLE);
            tv_last.setVisibility(View.VISIBLE);

            ll_range.setClickable(false);
            tv_range.setText(taskInfomation.getRange_task().getText());
            iv_range.setVisibility(View.GONE);
            range_save = taskInfomation.getRange_task().getValue();
            range_text = taskInfomation.getRange_task().getText();

            ll_type.setClickable(false);
            tv_type.setText(taskInfomation.getType_task().getText());
            iv_type.setVisibility(View.GONE);
            type_save = taskInfomation.getType_task().getValue();
            type_text = taskInfomation.getType_task().getText();

            et_target.setText(taskInfomation.getTarget());
            et_target.setEnabled(false);
            target = taskInfomation.getTarget();

            ll_start.setClickable(false);
            initDate(tv_start, taskInfomation.getStart_date());
            start_time = taskInfomation.getStart_date();

            ll_finish.setClickable(false);
            initDate(tv_finish, taskInfomation.getEnd_date());
            finish_time = taskInfomation.getEnd_date();

            initDate(tv_publish_time, taskInfomation.getPublish_date());

            ll_joiner.setClickable(false);
            StringBuffer buffer_text = new StringBuffer();
            String text_two = "";
            StringBuffer buffer_value = new StringBuffer();
            for (int i = 0; i < taskInfomation.getUser_list().size(); i++) {
                if (i == 0) {
                    buffer_text.append(taskInfomation.getUser_list().get(i).getText());
                    text_two = taskInfomation.getUser_list().get(i).getText();
                    buffer_value.append(taskInfomation.getUser_list().get(i).getValue());
                } else if (i == 1) {
                    buffer_text.append("," + taskInfomation.getUser_list().get(i).getText());
                    buffer_value.append("," + taskInfomation.getUser_list().get(i).getValue());
                    text_two += "," + taskInfomation.getUser_list().get(i).getText();
                } else {
                    buffer_text.append("," + taskInfomation.getUser_list().get(i).getText());
                    buffer_value.append("," + taskInfomation.getUser_list().get(i).getValue());
                }
            }
            if (taskInfomation.getUser_list().size() > 2) {
                tv_joiner.setText(text_two + "...");
                joiner_save = buffer_value.toString();
                joiner_text = buffer_text.toString();
            } else {
                tv_joiner.setText(text_two);
                joiner_save = buffer_value.toString();
                joiner_text = buffer_text.toString();
            }
            iv_joiner.setVisibility(View.GONE);

            et_remark.setEnabled(false);
            et_remark.setText(taskInfomation.getNote());
            remark = taskInfomation.getNote();
        }
    }

    private void initDate(TextView tv, String dates) {
        String date_month = dates.substring(5, 7);
        String date_day = dates.substring(8);

        int haveZeroDay = 0;
        int haveZeroMonth = 0;

        if (date_month.equals("10")) {
            haveZeroMonth = 1;
        }
        if (date_day.equals("10") || date_day.equals("20") || date_day.equals("30")) {
            haveZeroDay = 1;
        }
        if (haveZeroDay == 1 && haveZeroMonth == 1) {
            tv.setText(date_month + "月" + date_day + "日");
        } else if (haveZeroDay == 1 && haveZeroMonth == 0) {
            String month_new = date_month.replace("0", "").trim();
            tv.setText(month_new + "月" + date_day + "日");
        } else if (haveZeroDay == 0 && haveZeroMonth == 1) {
            String day_new = date_day.replace("0", "").trim();
            tv.setText(date_month + "月" + day_new + "日");
        } else {
            String month_new = date_month.replace("0", "").trim();
            String day_new = date_day.replace("0", "").trim();
            tv.setText(month_new + "月" + day_new + "日");

        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
//                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);
                break;
            case R.id.tv_save:
                //保存任务到服务器
                String isRight = checkData();//上传前对数据进行检查
                if (TextUtils.isEmpty(isRight.trim())) {
                    //上传数据到服务器
                    myDialog.show();
                    uploadDataToService();
                    setResult(7);
                    finish();
                } else {
                    App.showToast(getApplicationContext(), "请补全:" + isRight);
                }
                break;
            case R.id.tv_update:
                tv_update.setVisibility(View.GONE);
                ll_two.setVisibility(View.VISIBLE);
                //重置所有组件
                ll_range.setClickable(true);
                iv_range.setVisibility(View.VISIBLE);

                ll_type.setClickable(true);
                iv_type.setVisibility(View.VISIBLE);

                et_target.setEnabled(true);
                et_target.setClickable(true);
                ll_start.setClickable(true);
                ll_finish.setClickable(true);

                ll_joiner.setClickable(true);
                iv_joiner.setVisibility(View.VISIBLE);
                et_remark.setEnabled(true);

                break;
            case R.id.tv_update_to_save:
                //提交更新内容
                String isRight1 = checkData();//上传前对数据进行检查
                if (TextUtils.isEmpty(isRight1.trim())) {
                    //上传数据到服务器
                    myDialog.show();
                    uploadDataToService();
                    setResult(9);
                    finish();
                } else {
                    App.showToast(getApplicationContext(), "请补全:" + isRight1);
                }

                break;
            case R.id.tv_cancel:
                ll_two.setVisibility(View.GONE);
                tv_update.setVisibility(View.VISIBLE);
                initUpdateDatas();
                break;
            case R.id.ll_range:
                myStartActivity(task_range, range_save, range_text, "range", 0);
                break;
            case R.id.ll_type:
                myStartActivity(task_type, type_save, type_text, "type", 1);
                break;
            case R.id.ll_joiner:
                myStartActivity(list_user, joiner_save, joiner_text, "joiner", 2);
                break;
            case R.id.ll_start:
                //显示日历
                setDateTime("start");
                break;
            case R.id.ll_finish:
                //显示日历
                setDateTime("finish");
                break;
            case R.id.tv_delete:
                //删除任务
                showInDialog();
                break;
        }
    }

    private void myStartActivity(List<Task> task, String save, String text, String tag, int ma) {
        Intent intent = new Intent(this, TaskChooseDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", (Serializable) task);
        bundle.putString("save", save);
        bundle.putString("text", text);
        bundle.putString("tag", tag);
        intent.putExtras(bundle);
        startActivityForResult(intent, ma);
    }

    /**
     * 设置开始和结束时间
     */
    private void setDateTime(final String tags) {
        DatePickerDialog data = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                if (tags.equals("start")) {
                    isFirstIn = 1;
                    tv_start.setText((month + 1) + "月" + dayOfMonth + "日");
                    dealLittleTen(year, month, dayOfMonth, tags);
                    dealSpelTime(year, month, dayOfMonth);
                    start_day = dayOfMonth;
                    start_month = month;
                    start_year = year;

                } else if (tags.equals("finish")) {
                    isFirstIn = 1;
                    tv_finish.setText((month + 1) + "月" + dayOfMonth + "日");
                    dealLittleTen(year, month, dayOfMonth, tags);
                    finish_year = year;
                    finish_month = month;
                    finish_day = dayOfMonth;
                }

            }
        }, tags.equals("start") ? start_year : finish_year,
                tags.equals("start") ? start_month : finish_month,
                tags.equals("start") ? start_day : finish_day);
        data.show();
    }

    /**
     * 对小于10的天数和月份前面加0
     *
     * @param year
     * @param month
     * @param dayOfMonth
     */
    private void dealLittleTen(int year, int month, int dayOfMonth, String tag) {

        if (month + 1 < 10 && dayOfMonth < 10) {
            if (tag.equals("start")) {
                start_time = year + "-" + "0" + (month + 1) + "-" + "0" + dayOfMonth;
            } else if (tag.equals("finish")) {
                finish_time = year + "-" + "0" + (month + 1) + "-" + "0" + dayOfMonth;
            }

        } else if (month + 1 < 10 && dayOfMonth > 9) {
            if (tag.equals("start")) {
                start_time = year + "-" + "0" + (month + 1) + "-" + dayOfMonth;
            } else if (tag.equals("finish")) {
                finish_time = year + "-" + "0" + (month + 1) + "-" + dayOfMonth;
            }

        } else if (month + 1 > 9 && dayOfMonth < 10) {
            if (tag.equals("start")) {
                start_time = year + "-" + (month + 1) + "-" + "0" + dayOfMonth;
            } else if (tag.equals("finish")) {
                finish_time = year + "-" + (month + 1) + "-" + "0" + dayOfMonth;
            }

        } else {
            if (tag.equals("start")) {
                start_time = year + "-" + (month + 1) + "-" + dayOfMonth;
            } else if (tag.equals("finish")) {
                finish_time = year + "-" + (month + 1) + "-" + dayOfMonth;
            }

        }
    }

    /**
     * 当选择开始时间后结束时间默加一天,但是针对一个月最后一天要做特殊处理
     *
     * @param year
     * @param month
     * @param dayOfMonth
     */
    private void dealSpelTime(int year, int month, int dayOfMonth) {
        //首先判断结束时间是否在开始时间之后

        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
        if (isFirstIn == 1) {
            try {
                Date date_start = dfm.parse(start_time);
                Date date_finish = dfm.parse(finish_time);
                if (date_finish.getTime() <= date_start.getTime()) {

                    if ((month + 1 == 1 || month + 1 == 3 || month + 1 == 5 || month + 1 == 7 || month + 1 == 8 || month + 1 == 10 || month + 1 == 12) && dayOfMonth == 31) {
                        if (month + 1 == 12) {
                            tv_finish.setText(1 + "月" + 1 + "日");
                            dealLittleTen(year + 1, 0, 1, "finish");
                            finish_day = 1;
                            finish_month = 0;
                            finish_year = year + 1;
                        } else {
                            tv_finish.setText((month + 2) + "月" + 1 + "日");
                            dealLittleTen(year, (month + 1), 1, "finish");
                            finish_day = 1;
                            finish_month = month + 1;
                            finish_year = year;
                        }

                    } else if ((month + 1 == 2 || month + 1 == 4 || month + 1 == 6 || month + 1 == 9 || month + 1 == 11) && dayOfMonth == 30) {
                        tv_finish.setText((month + 2) + "月" + 1 + "日");
                        dealLittleTen(year, (month + 1), 1, "finish");
                        finish_day = 1;
                        finish_month = month + 1;
                        finish_year = year;
                    } else {
                        tv_finish.setText((month + 1) + "月" + (dayOfMonth + 1) + "日");
                        dealLittleTen(year, (month), (dayOfMonth + 1), "finish");
                        finish_day = dayOfMonth + 1;
                        finish_month = month;
                        finish_year = year;
                    }
                }
            } catch (Exception e) {

            }
        } else if (isFirstIn == 0) {
            if ((month + 1 == 1 || month + 1 == 3 || month + 1 == 5 || month + 1 == 7 || month + 1 == 8 || month + 1 == 10 || month + 1 == 12) && dayOfMonth == 31) {
                if (month + 1 == 12) {
                    tv_finish.setText(1 + "月" + 1 + "日");
                    dealLittleTen(year + 1, 0, 1, "finish");
                    finish_day = 1;
                    finish_month = 0;
                    finish_year = year + 1;
                } else {
                    tv_finish.setText((month + 2) + "月" + 1 + "日");
                    dealLittleTen(year, (month + 1), 1, "finish");
                    finish_day = 1;
                    finish_month = month + 1;
                    finish_year = year;
                }

            } else if ((month + 1 == 2 || month + 1 == 4 || month + 1 == 6 || month + 1 == 9 || month + 1 == 11) && dayOfMonth == 30) {
                tv_finish.setText((month + 2) + "月" + 1 + "日");
                dealLittleTen(year, (month + 1), 1, "finish");
                finish_day = 1;
                finish_month = month + 1;
                finish_year = year;
            } else {
                tv_finish.setText((month + 1) + "月" + (dayOfMonth + 1) + "日");
                dealLittleTen(year, (month), (dayOfMonth + 1), "finish");
                finish_day = dayOfMonth + 1;
                finish_month = month;
                finish_year = year;
            }
        }
    }

    /**
     * 从服务器获取任务数据
     */
    private void getTaskListFromService() {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/gettaskaddinfo")
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myDialog.dismiss();
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
                            if (data != null) {
                                //解析任务类型列表
                                JsonArray task_type = data.get("task_type").getAsJsonArray();
                                List<Task> list_type = new ArrayList<Task>();
                                List<Task> list_range = new ArrayList<Task>();
                                for (JsonElement je : task_type) {
                                    JsonObject jo = je.getAsJsonObject();
                                    String text = "";
                                    String value = "";
                                    if (!jo.get("text").isJsonNull()) {
                                        text = jo.get("text").getAsString();
                                        list_type.add(new Task(text, value, 0, 0));
                                    }
                                    JsonArray children = jo.get("children").getAsJsonArray();
                                    for (JsonElement je2 : children) {
                                        JsonObject jo2 = je2.getAsJsonObject();
                                        String value2 = "";
                                        String text2 = "";
                                        if (!jo2.get("value").isJsonNull()) {
                                            value2 = jo2.get("value").getAsString();
                                        }
                                        if (!jo2.get("text").isJsonNull()) {
                                            text2 = jo2.get("text").getAsString();
                                        }
                                        list_type.add(new Task(text2, value2, 1, 0));
                                    }
                                }
                                //传递任务类型列表
                                Message msg = Message.obtain();
                                msg.what = 0;
                                msg.obj = list_type;
                                handler.sendMessage(msg);

                                //解析任务范围
                                JsonArray task_range = data.get("task_range").getAsJsonArray();
                                for (JsonElement je3 : task_range) {
                                    JsonObject jo3 = je3.getAsJsonObject();
                                    String value3 = "";
                                    String text3 = "";
                                    if (!jo3.get("value").isJsonNull()) {
                                        value3 = jo3.get("value").getAsString();
                                    }
                                    if (!jo3.get("text").isJsonNull()) {
                                        text3 = jo3.get("text").getAsString();
                                    }
                                    list_range.add(new Task(text3, value3, 1, 0));
                                }
                                //传递任务范围
                                Message msg2 = Message.obtain();
                                msg2.what = 1;
                                msg2.obj = list_range;
                                handler.sendMessage(msg2);
                            }

                        } else {
                            App.showToast(getApplicationContext(), message);
                        }
                        myDialog.dismiss();
                    }
                });
    }

    /**
     * 接收回传过来的数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        dealResultData(resultCode, data);
    }

    private void dealResultData(int resultCode, Intent data) {
        switch (resultCode) {
            case 3:
                if (!TextUtils.isEmpty(data.getStringExtra("range"))) {
                    range_save = data.getStringExtra("range");
                    range_text = data.getStringExtra("range_text");
                    tv_range.setText(range_text);
                } else {
                    tv_range.setText("请选择");
                }
                break;
            case 4:
                if (!TextUtils.isEmpty(data.getStringExtra("type"))) {
                    type_save = data.getStringExtra("type");
                    type_text = data.getStringExtra("type_text");
                    tv_type.setText(type_text);
                } else {
                    tv_type.setText("请选择");
                }
                break;
            case 5:
                if (!TextUtils.isEmpty(data.getStringExtra("joiner"))) {
                    joiner_save = data.getStringExtra("joiner");
                    joiner_text = data.getStringExtra("joiner_text");
                    String[] joiner_deal_text = joiner_text.split(",");
                    if (joiner_deal_text.length < 3) {
                        tv_joiner.setText(joiner_text);
                    } else {
                        tv_joiner.setText(joiner_deal_text[0] + "," + joiner_deal_text[1] + "...");
                    }

                } else {
                    tv_joiner.setText("请选择");
                }
                break;
        }
    }

    //检查数据是否完整
    private String checkData() {
        target = et_target.getText().toString();
        remark = et_remark.getText().toString();
        if (target.indexOf(",") != -1) {
            if (target.length() > 4) {
                //截取数据
                String[] str = target.split(",");
                target = "";
                for (int i = 0; i < str.length; i++) {
                    target += str[i];
                }
            }
        }

        String[] str_datas = {range_save, type_save, target, start_time, finish_time, joiner_save};
        String[] strings = {"范围", "类型", "目标", "开始时间", "结束时间", "参与者"};
        List<Integer> list_index = new ArrayList<>();
        String null_data = "";
        for (int i = 0; i < str_datas.length; i++) {
            if (TextUtils.isEmpty(str_datas[i])) {
                list_index.add(i);
            }
        }
        for (int i = 0; i < list_index.size(); i++) {
            null_data += " " + strings[list_index.get(i)];
        }
        return null_data;

//        if (!TextUtils.isEmpty(range_save)&&!TextUtils.isEmpty(type_save)&&!TextUtils.isEmpty(target)
//                &&!TextUtils.isEmpty(start_time)&&!TextUtils.isEmpty(finish_time)&&!TextUtils.isEmpty(joiner_save)){
//            return true;
//        }else{
//            App.showToast(getApplicationContext(),"数据不完整");
//            return false;
//        }
    }

    /**
     * 上传数据到服务器
     */
    private void uploadDataToService() {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/savetask")
                .addParams("token", token)
                .addParams("task_id", task_id)
                .addParams("type", type_save)
                .addParams("range", range_save)
                .addParams("target", target)
                .addParams("start_date", start_time)
                .addParams("end_date", finish_time)
                .addParams("user_list", joiner_save)
                .addParams("note", remark)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myDialog.dismiss();
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
                            App.showToast(getApplicationContext(), message);
                            finish();
                        } else {
                            App.showToast(getApplicationContext(), message);
                        }
                        myDialog.dismiss();
                    }
                });
    }

    /**
     * 获取用户体系
     */
    public void getUserList() {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getuserlist")
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //获取员工信息
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        String status = "";
                        String message = "";
                        List<Task> list_user = new ArrayList<Task>();
                        if (!object.get("status").isJsonNull())
                            status = object.get("status").getAsString();
                        if (!object.get("message").isJsonNull())
                            message = object.get("message").getAsString();
                        if (status.equals("ok")) {
                            JsonArray array = object.get("data").getAsJsonArray();
                            for (JsonElement jsonElement : array) {
                                JsonObject jObject = jsonElement.getAsJsonObject();
                                String names = jObject.get("display_name").getAsString();
                                String user_id = jObject.get("user_id").getAsString();
                                if (!sp.getString("displayname", null).equals(names))
                                    list_user.add(new Task(names, user_id, 1, 0));

                            }
                            Message msg = Message.obtain();
                            msg.what = 2;
                            msg.obj = list_user;
                            handler.sendMessage(msg);
                        } else {
                            App.showToast(getApplicationContext(), message);
                        }
                    }
                });
    }

    private void showInDialog() {
        AlertDialog ad = new AlertDialog.Builder(NewBuildTaskActivity.this).create();
        ad.setTitle("闲惠");
        ad.setMessage("确定删除任务吗？");
        ad.setButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteTask();
                setResult(10);
                finish();
            }
        });
        ad.setButton2("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        ad.show();
    }

    /**
     * 删除任务
     */
    private void deleteTask() {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/deletetask")
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
                            App.showToast(getApplicationContext(), message);
                        } else {
                            App.showToast(getApplicationContext(), message);
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
