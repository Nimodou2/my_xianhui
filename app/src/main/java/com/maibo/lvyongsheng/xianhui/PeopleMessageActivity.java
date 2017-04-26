package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.maibo.lvyongsheng.xianhui.entity.HelperCustomer;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.utils.NetWorkUtils;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import de.greenrobot.event.EventBus;
import okhttp3.Call;

/**
 * Created by LYS on 2016/10/6.
 */
public class PeopleMessageActivity extends BaseActivity implements View.OnClickListener {
    private int tag;
    SharedPreferences sp;
    String token, apiURL;
    int customer_id = -1;
    String customer_name;
    List<HelperCustomer> list1;
    //    List<Order> orderList;
    ImageView cus_head;
    LinearLayout ll_cards, tv_consume_record, ll_plan_project, ll_yuyue, ll_kefu_manager, ll_order,ll_beizhu;
    TextView cus_name, cus_grade, cus_files_num, cus_manager,
            cus_cards_num, cus_record, cus_plan, cus_order, back, tv_files, cus_dingdan,cus_beizhunew;

    @Bind(R.id.ll_head)
    LinearLayout ll_head;

    @Bind(R.id.in_no_datas)
    LinearLayout in_no_datas;
    @Bind(R.id.in_loading_error)
    LinearLayout in_loading_error;
    @Bind(R.id.ll_all_data)
    LinearLayout ll_all_data;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    dealCustomerHandleMessage(msg);
                    break;
            }
        }
    };

    /**
     * 处理顾客基本信息
     *
     * @param msg
     */
    private void dealCustomerHandleMessage(Message msg) {
        list1 = (List<HelperCustomer>) msg.obj;
        HelperCustomer customer = list1.get(0);
        //下载头像
        Picasso.with(this).load(customer.getAvator_url()).into(cus_head);
        customer_name = customer.getFullname();
        cus_name.setText(customer.getFullname());
        cus_grade.setText("会员等级: " + customer.getVip_star());
        cus_files_num.setText("档案号: " + customer.getCert_no());
        cus_manager.setText(customer.getManager());
        cus_cards_num.setText("共" + customer.getCard_total() + "张卡");
        if (!TextUtils.isEmpty(customer.getLast_consume_time())) {
            cus_record.setText(customer.getLast_consume_time().replace(".", "-"));
        } else {
            cus_record.setText("暂无");
        }
        if (!customer.getSchedule_date().equals("false")) {
            cus_order.setText(customer.getSchedule_date());
        } else {
            cus_order.setText("暂无");
        }

        if (customer.getPlaned() == 0)
            cus_plan.setText("未计划");
        else if (customer.getPlaned() == 1) {
            cus_plan.setText("已计划");
        }
        if (customer.getSchedule_status().equals("0")) cus_dingdan.setText("未开始");
        else if (customer.getSchedule_status().equals("1")) cus_dingdan.setText("进行中");
        else if (customer.getSchedule_status().equals("2")) cus_dingdan.setText("已结束");
        else if (customer.getSchedule_status().equals("3")) cus_dingdan.setText("已结单");
        else cus_dingdan.setText("暂无");
        /*假数据 显示备注的最新反馈*/
        cus_beizhunew.setText("最新");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        EventBus.getDefault().register(this);
        //判断当前网络状态
        if (NetWorkUtils.isNetworkConnected(this)) {
            initListener();
            if (customer_id != -1) {
                getServicerData(customer_id);
            } else {
                showToast("数据异常");
                dismissShortDialog();
            }
        } else {
            ll_all_data.setVisibility(View.GONE);
            in_loading_error.setVisibility(View.VISIBLE);
            showToast("网络连接异常");
            dismissShortDialog();
        }

    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        tv_files.setOnClickListener(this);
        ll_cards.setOnClickListener(this);
        tv_consume_record.setOnClickListener(this);
        ll_plan_project.setOnClickListener(this);
        ll_yuyue.setOnClickListener(this);
        ll_kefu_manager.setOnClickListener(this);
        ll_order.setOnClickListener(this);
        ll_beizhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(PeopleMessageActivity.this,CustomerAddBeizhu_Activity.class);
                intent.putExtra("customer_id",customer_id);
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化view
     */
    private void initView() {
        setContentView(R.layout.activity_people_message);
        Intent intent = getIntent();
        tag=intent.getIntExtra("tag",-1);
        customer_id = intent.getIntExtra("customer_id", -1);
//        Log.e("customer_id1111",customer_id+"");
        //adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token = sp.getString("token", null);
        apiURL = sp.getString("apiURL", null);

        showShortDialog();

        cus_head = (ImageView) findViewById(R.id.cus_head);
        //动态设置图片的宽和高
        setPictureHeightAndWidth();

        cus_name = (TextView) findViewById(R.id.cus_name);
        cus_grade = (TextView) findViewById(R.id.cus_grade);
        cus_files_num = (TextView) findViewById(R.id.cus_files_num);
        cus_manager = (TextView) findViewById(R.id.cus_manager);
        cus_cards_num = (TextView) findViewById(R.id.cus_cards_num);
        cus_record = (TextView) findViewById(R.id.cus_record);
        cus_plan = (TextView) findViewById(R.id.cus_plan);
        cus_order = (TextView) findViewById(R.id.cus_order);
        cus_dingdan = (TextView) findViewById(R.id.cus_dingdan);
        /*新增加的备注*/
        cus_beizhunew=(TextView)findViewById(R.id.cus_beizhunew);
        ll_beizhu=(LinearLayout)findViewById(R.id.ll_beizhu);

        ll_cards = (LinearLayout) findViewById(R.id.ll_cards);
        tv_consume_record = (LinearLayout) findViewById(R.id.tv_consume_record);
        ll_plan_project = (LinearLayout) findViewById(R.id.ll_plan_project);
        ll_yuyue = (LinearLayout) findViewById(R.id.ll_yuyue);
        ll_kefu_manager = (LinearLayout) findViewById(R.id.ll_kefu_manager);
        ll_order = (LinearLayout) findViewById(R.id.ll_order);
        back = (TextView) findViewById(R.id.back);
        tv_files = (TextView) findViewById(R.id.tv_files);
        if(tag!=-1){
            tv_files.setVisibility(View.INVISIBLE);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 动态设置头像的宽和高
     */
    private void setPictureHeightAndWidth() {
        View views[] = new View[1];
        views[0] = cus_head;
        int heights[] = new int[1];
        heights[0] = viewHeight * 30 / 255;
        int widths[] = new int[1];
        widths[0] = viewHeight * 30 / 255;
        setViewHeightAndWidth(views, heights, widths);
    }

    /**
     * 获取顾客基本资料
     */
    public void getServicerData(int customer_id) {

        Log.e("PeopleMessageActivity",apiURL+"    "+customer_id+"    "+token);

        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getcustomerdetail")
                .addParams("token", token)
                .addParams("customer_id", "" + customer_id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissShortDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.e("顾客资料:", response);
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String status = jsonObject.get("status").getAsString();
                        String message = jsonObject.get("message").getAsString();
                        if (status.equals("ok")) {
                            analysisJson(jsonObject);

                        } else {
                            showToast(message);
                        }
                        dismissShortDialog();

                    }
                });
    }

    /**
     * 解析Json
     *
     * @param jsonObject
     */
    private void analysisJson(JsonObject jsonObject) {
        JsonObject data = jsonObject.get("data").getAsJsonObject();
        JsonObject basic = data.get("basic").getAsJsonObject();
        List<HelperCustomer> list = new ArrayList<HelperCustomer>();
        int customer_id = 0;
        String fullname = "";
        String vip_star = "";
        String cert_no = "";
        String avator_url = "";
        String guid = "";
        int planed = -1;
        String schedule_date = "";
        String last_consume_date = "";
        int card_total = 0;
        String customer_manager = "";
        String schedule_status = "";
        if (!basic.get("customer_id").isJsonNull())
            customer_id = basic.get("customer_id").getAsInt();
        if (!basic.get("fullname").isJsonNull())
            fullname = basic.get("fullname").getAsString();
        if (!basic.get("vip_star").isJsonNull())
            vip_star = basic.get("vip_star").getAsString();
        if (!basic.get("cert_no").isJsonNull())
            cert_no = basic.get("cert_no").getAsString();
        if (!basic.get("avator_url").isJsonNull())
            avator_url = basic.get("avator_url").getAsString();
        if (!basic.get("guid").isJsonNull())
            guid = basic.get("guid").getAsString();
        if (!data.get("planed").isJsonNull())
            planed = data.get("planed").getAsInt();
        if (!data.get("schedule_date").isJsonNull())
            schedule_date = data.get("schedule_date").getAsString();
        if (!data.get("last_consume_date").isJsonNull())
            last_consume_date = data.get("last_consume_date").getAsString();
        if (!data.get("card_total").isJsonNull())
            card_total = data.get("card_total").getAsInt();
        if (!data.get("customer_manager").isJsonNull())
            customer_manager = data.get("customer_manager").getAsString();
        if (!data.get("schedule_status").isJsonNull())
            schedule_status = data.get("schedule_status").getAsString();
        list.add(new HelperCustomer(cert_no, vip_star, customer_id, fullname, avator_url, guid, schedule_date,
                planed, card_total, customer_manager, last_consume_date, schedule_status));
        Message msg = Message.obtain();
        msg.what = 0;
        msg.obj = list;
        handler.sendMessage(msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String adviserName = sp.getString("adviserName", null);
        if (!TextUtils.isEmpty(adviserName))
            cus_manager.setText(adviserName);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_cards:
                //跳转到它的卡包界面
                Intent intent = new Intent(PeopleMessageActivity.this, CustomerDetailsActivity.class);
                intent.putExtra("tag", 0);
                intent.putExtra("customer_id", list1.get(0).getCustomer_id());
                intent.putExtra("customer_name", list1.get(0).getFullname());
                startActivity(intent);
                break;
            case R.id.tv_consume_record:
                //跳转到消费记录界面
                Intent intent1 = new Intent(PeopleMessageActivity.this, CustomerConsumeActivity.class);
                intent1.putExtra("tag", 1);
                intent1.putExtra("customer_id", list1.get(0).getCustomer_id());
                intent1.putExtra("customer_name", list1.get(0).getFullname());
                startActivity(intent1);
                break;
            case R.id.ll_plan_project:
                //跳转项目计划界面
                Intent intent2 = new Intent(PeopleMessageActivity.this, ProjectPlanActivity.class);
                intent2.putExtra("customer_id", customer_id);
                intent2.putExtra("customer_name", list1.get(0).getFullname());
                startActivity(intent2);
                break;
            case R.id.ll_yuyue:
                //跳转顾客预约信息界面
                Intent intent3 = new Intent(PeopleMessageActivity.this, ReservationInformationActivity.class);
                intent3.putExtra("customer_id", customer_id);
                intent3.putExtra("tag", 12);
                startActivity(intent3);
                break;
            case R.id.ll_kefu_manager:
                //跳转到客服经理界面
                Intent intent4 = new Intent(PeopleMessageActivity.this, CustomerManagerActivity.class);
                intent4.putExtra("customer_id", customer_id);
                startActivity(intent4);
                break;
            case R.id.tv_files:
                //跳到订单界面
                if(tag==-1){
                    Intent intent5 = new Intent(this, OrderActivity.class);
                    if(list1!=null){
                        intent5.putExtra("customer_name", list1.get(0).getFullname());
                        intent5.putExtra("customer_id", customer_id);
                        intent5.putExtra("tag", 3);
                        startActivity(intent5);
                    }
                }
                break;
            case R.id.ll_order:
                //跳到订单界面
                Intent intent6 = new Intent(this, OrderActivity.class);
                intent6.putExtra("customer_name", list1.get(0).getFullname());
                intent6.putExtra("customer_id", customer_id);
                intent6.putExtra("tag", 3);
                startActivity(intent6);
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        CloseAllActivity.getScreenManager().popActivity(this);
    }

    /**
     * 网络问题，重新加载
     *
     * @param view
     */
    public void loadingMore(View view) {
        showShortDialog();
        if (NetWorkUtils.isNetworkConnected(this)) {
            ll_all_data.setVisibility(View.VISIBLE);
            in_loading_error.setVisibility(View.GONE);
            initListener();
            if (customer_id != -1) {
                getServicerData(customer_id);
//                getCustomerOrder(customer_id);
            } else {
                showToast("数据异常");
                dismissShortDialog();
            }
        } else {
            ll_all_data.setVisibility(View.GONE);
            in_loading_error.setVisibility(View.VISIBLE);
            showToast("网络连接异常");
            dismissShortDialog();

        }
    }

    //
    public void onEvent(EventDatas event) {
        if (event.getTag().equals(Constants.ORDER_STATUS)) {
            String new_status = event.getResponse();
            if (new_status.equals("0")) cus_dingdan.setText("未开始");
            else if (new_status.equals("1")) cus_dingdan.setText("进行中");
            else if (new_status.equals("2")) cus_dingdan.setText("已结束");
            else if (new_status.equals("3")) cus_dingdan.setText("已结单");
            else cus_dingdan.setText("暂无");
        }
    }
}
