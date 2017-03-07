package com.maibo.lvyongsheng.xianhui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.adapter.OrderAdapter;
import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.Employee;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.maibo.lvyongsheng.xianhui.entity.Order;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.myinterface.OrderStateListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import de.greenrobot.event.EventBus;
import okhttp3.Call;


/**
 * Created by LYS on 2016/10/9.
 */
public class OrderActivity extends BaseActivity {
    ListView lv_order;
    TextView cus_name, back;
    List<Order> productOrder, projectOrder, collOrder, customerOrder;
    int project_id;
    int tag;
    int user_id;
    Employee employee;
    int customer_id;
    SharedPreferences sp;
    String token, apiURL;
    int clickPosition;
    int fromWhere = 0;//用于区分订单数据来源

    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.in_no_datas)
    LinearLayout in_no_datas;
    @Bind(R.id.in_loading_error)
    LinearLayout in_loading_error;
    @Bind(R.id.swip_refresh)
    SwipeRefreshLayout refreshLayout;

    OrderAdapter adapter;
    final int CUSTOMER_ORDER = 0, COLLEAGUE_ORDER = 1, PROJECT_ORDER = 2,
            START_PROJECT = 3, CANCLE_PROJECT = 4, FINISH_PROJECT = 5;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CUSTOMER_ORDER:
                    List<Order> customerOrder = (List<Order>) msg.obj;
                    if (customerOrder.size() > 0) {
                        if (fromWhere == 1) {
                            //刷新Adapter
                            EventDatas eventDatas = new EventDatas(Constants.ORDER_STATUS, customerOrder.get(clickPosition).getRaw_status() + "");
                            EventBus.getDefault().post(eventDatas);
                            adapter.setDatas(customerOrder);
                        } else {
                            adapter.setDatas(OrderActivity.this, customerOrder, 1, viewHeight);
                            lv_order.setAdapter(adapter);
                        }
                    } else {
                        in_no_datas.setVisibility(View.VISIBLE);
                    }
                    in_loading_error.setVisibility(View.GONE);
                    break;
                case COLLEAGUE_ORDER:
                    List<Order> colleagueOrder = (List<Order>) msg.obj;
                    if (colleagueOrder.size() > 0) {
                        if (fromWhere == 1) {
                            adapter.setDatas(colleagueOrder);
                        } else {
                            adapter.setDatas(OrderActivity.this, colleagueOrder, 1, viewHeight);
                            lv_order.setAdapter(adapter);
                        }
                    } else {
                        in_no_datas.setVisibility(View.VISIBLE);
                    }
                    in_loading_error.setVisibility(View.GONE);
                    break;
                case PROJECT_ORDER:
                    List<Order> projectOrder = (List<Order>) msg.obj;
                    if (projectOrder.size() > 0) {
                        if (fromWhere == 1) {
                            adapter.setDatas(projectOrder);
                        } else {
                            adapter.setDatas(OrderActivity.this, projectOrder, 1, viewHeight);
                            lv_order.setAdapter(adapter);
                        }
                    } else {
                        in_no_datas.setVisibility(View.VISIBLE);
                    }
                    in_loading_error.setVisibility(View.GONE);
                    break;
                case START_PROJECT:
                    if (tag == 1) {
                        //项目
                        getProjectOrder(project_id);
                    } else if (tag == 2) {
                        //同事
                        getColleagueOrder(user_id);
                    } else if (tag == 3) {
                        //顾客
                        getCustomerOrder(customer_id);
                    }
                    showToast("项目已开始");
                    break;
                case CANCLE_PROJECT:
                    if (tag == 1) {
                        //项目
                        getProjectOrder(project_id);
                    } else if (tag == 2) {
                        //同事
                        getColleagueOrder(user_id);
                    } else if (tag == 3) {
                        //顾客
                        getCustomerOrder(customer_id);
                    }
                    showToast("项目已取消");
                    break;
                case FINISH_PROJECT:
                    if (tag == 1) {
                        //项目
                        getProjectOrder(project_id);
                    } else if (tag == 2) {
                        //同事
                        getColleagueOrder(user_id);
                    } else if (tag == 3) {
                        //顾客
                        getCustomerOrder(customer_id);
                    }
                    showToast("项目已结束");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        initDatas(intent, bundle);
        showAdapter(intent, bundle);
        initListener();
    }

    /**
     * 初始化数据
     *
     * @param intent
     * @param bundle
     */
    private void initDatas(Intent intent, Bundle bundle) {
        productOrder = (List<Order>) bundle.get("productOrder");
        projectOrder = (List<Order>) bundle.get("projectOrder");
        collOrder = (List<Order>) bundle.get("collOrder");
        customerOrder = (List<Order>) bundle.get("customerOrder");
        customer_id = intent.getIntExtra("customer_id", -1);
        employee = (Employee) bundle.get("Employee");
        tag = intent.getIntExtra("tag", -1);
        project_id = intent.getIntExtra("project_id", -1);
        user_id = intent.getIntExtra("user_id", -1);
        adapter = new OrderAdapter();
    }

    /**
     * 初始化View
     */
    private void initView() {
        adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token = sp.getString("token", null);
        apiURL = sp.getString("apiURL", null);
        lv_order = (ListView) findViewById(R.id.lv_order);
        cus_name = (TextView) findViewById(R.id.cus_name);
        back = (TextView) findViewById(R.id.back);
        //刷新页面
        refreshLayout.setEnabled(true);
        //设置卷内的颜色
        refreshLayout.setColorSchemeResources(R.color.colorLightYellow,
                R.color.colorLightYellow, R.color.colorLightYellow, R.color.colorLightYellow);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fromWhere=0;
                if (tag == 1) {
                    getProjectOrder(project_id);
                } else if (tag == 2) {
                    getColleagueOrder(user_id);
                } else if (tag == 3) {
                    getCustomerOrder(customer_id);
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 监听按钮点击事件
     */
    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        adapter.setOnMyButtonClickListener(new OrderStateListener() {
            @Override
            public void startOrder(int position, int schedule_id) {
                showDialog("确定开始项目吗？", 0, schedule_id);
                clickPosition = position;
                fromWhere = 1;
            }

            @Override
            public void cancelOrder(int position, int schedule_id) {
                showDialog("确定取消项目吗？", 1, schedule_id);
                clickPosition = position;
                fromWhere = 1;
            }

            @Override
            public void finishOrder(int position, int schedule_id) {
                showDialog("确定结束项目吗？", 2, schedule_id);
                clickPosition = position;
                fromWhere = 1;
            }
        });

        lv_order.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    refreshLayout.setEnabled(true);
                else
                    refreshLayout.setEnabled(false);
            }
        });

    }

    /**
     * 展示不同订单
     *
     * @param intent
     * @param bundle
     */
    private void showAdapter(Intent intent, Bundle bundle) {
        if (tag == 0) {
            if (productOrder != null && productOrder.size() > 0) {
                cus_name.setText(productOrder.get(0).getItem_name());
                lv_order.setAdapter(adapter = new OrderAdapter(this, productOrder, 0, viewHeight));
            } else {
                cus_name.setText(intent.getStringExtra("productName"));
                in_no_datas.setVisibility(View.VISIBLE);
            }

        } else if (tag == 1) {
            showShortDialog();
            getProjectOrder(project_id);
            if (projectOrder != null && projectOrder.size() > 0) {
                cus_name.setText(projectOrder.get(0).getProject_name());
            } else {
                cus_name.setText(intent.getStringExtra("projectName"));
            }
        } else if (tag == 2) {
            showShortDialog();
            getColleagueOrder(user_id);
            if (collOrder != null && collOrder.size() > 0) {
                cus_name.setText(intent.getStringExtra("collName"));
            } else {
                cus_name.setText(intent.getStringExtra("collName"));
            }
        } else if (tag == 3) {
            showShortDialog();
            getCustomerOrder(customer_id);
            if (customerOrder != null && customerOrder.size() > 0) {
                cus_name.setText(bundle.getString("customer_name"));
            } else {
                cus_name.setText(bundle.getString("customer_name"));
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }

    /**
     * 用于提醒订单执行状态
     */
    private void showDialog(String msg, final int what, final int schedule_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("闲惠");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLongDialog();
                if (what == 0) {
                    //开始
                    dealWithOrder(0, 1, schedule_id);

                } else if (what == 1) {
                    //取消
                    dealWithOrder(1, 0, schedule_id);

                } else if (what == 2) {
                    //结束
                    dealWithOrder(1, 2, schedule_id);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.rgb(5, 122, 240));
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(5, 122, 240));
    }

    /**
     * 获取客户订单
     *
     * @param customer_id
     */
    public void getCustomerOrder(int customer_id) {
//        Log.e("customer_id222",customer_id+"");
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/gethelpercustomerschedulelist")
                .addParams("token", token)
                .addParams("customer_id", customer_id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissShortDialog();
                        dismissLongDialog();
                        showToast(R.string.net_connect_error);
                        in_loading_error.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.e("客户订单", response);
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String statuss = jsonObject.get("status").getAsString();
                        String message = jsonObject.get("message").getAsString();
                        if (statuss.equals("ok")) {
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            if (!data.get("rows").isJsonNull()) {
                                JsonArray rows = data.get("rows").getAsJsonArray();
                                List<Order> list = new ArrayList<Order>();
                                for (JsonElement jsonElement : rows) {
                                    JsonObject jo = jsonElement.getAsJsonObject();
                                    int schedule_id = -1;
                                    String status = "";
                                    String start_time = "";
                                    String end_time = "";
                                    String engineer_id = "";
                                    String bed_name = "";
                                    String project_code = "";
                                    String project_name = "";
                                    int customer_id = -1;
                                    String customer_name = "";
                                    String avator_url = "";
                                    String guid = "";
                                    String engineer_name = "";
                                    String org_name = "";
                                    int raw_status = -1;
                                    if (!jo.get("schedule_id").isJsonNull())
                                        schedule_id = jo.get("schedule_id").getAsInt();
                                    if (!jo.get("start_time").isJsonNull())
                                        status = jo.get("status").getAsString();
                                    if (!jo.get("start_time").isJsonNull())
                                        start_time = jo.get("start_time").getAsString();
                                    if (!jo.get("end_time").isJsonNull())
                                        end_time = jo.get("end_time").getAsString();
                                    if (!jo.get("engineer_id").isJsonNull())
                                        engineer_id = jo.get("engineer_id").getAsString();
                                    if (!jo.get("bed_name").isJsonNull())
                                        bed_name = jo.get("bed_name").getAsString();
                                    if (!jo.get("project_code").isJsonNull())
                                        project_code = jo.get("project_code").getAsString();
                                    if (!jo.get("project_name").isJsonNull())
                                        project_name = jo.get("project_name").getAsString();
                                    if (!jo.get("avator_url").isJsonNull())
                                        avator_url = jo.get("avator_url").getAsString();
                                    if (!jo.get("guid").isJsonNull())
                                        guid = jo.get("guid").getAsString();
                                    if (!jo.get("customer_id").isJsonNull())
                                        customer_id = jo.get("customer_id").getAsInt();
                                    if (!jo.get("customer_name").isJsonNull())
                                        customer_name = jo.get("customer_name").getAsString();
                                    if (!jo.get("engineer_name").isJsonNull())
                                        engineer_name = jo.get("engineer_name").getAsString();
                                    if (!jo.get("org_name").isJsonNull())
                                        org_name = jo.get("org_name").getAsString();
                                    if (!jo.get("raw_status").isJsonNull())
                                        raw_status = jo.get("raw_status").getAsInt();
                                    list.add(new Order(schedule_id, status, start_time, end_time, engineer_id,
                                            bed_name, project_code, project_name, avator_url, guid, customer_id,
                                            customer_name, engineer_name, org_name, raw_status));
                                }
                                Message msg = Message.obtain();
                                msg.what = CUSTOMER_ORDER;
                                msg.obj = list;
                                handler.sendMessage(msg);
                            }
                        } else {
                            showToast(message);
                        }
                        dismissShortDialog();
                        dismissLongDialog();
                    }
                });
    }

    /**
     * 获取同事订单
     *
     * @param user_id
     */
    public void getColleagueOrder(int user_id) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/gethelperworkerschedulelist")
                .addParams("token", token)
                .addParams("user_id", user_id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissShortDialog();
                        dismissLongDialog();
                        showToast(R.string.net_connect_error);
                        in_loading_error.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.e("同事",response);
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String statuss = jsonObject.get("status").getAsString();
                        String message = jsonObject.get("message").getAsString();
                        if (statuss.equals("ok")) {
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            JsonArray rows = data.get("rows").getAsJsonArray();
                            List<Order> list = new ArrayList<Order>();
                            int schedule_id = 0;
                            int customer_id = 0;
                            String status = "";
                            String start_time = "";
                            String end_time = "";
                            String engineer_id = "";
                            String bed_name = "";
                            String project_code = "";
                            String project_name = "";
                            String customer_name = "";
                            String engineer_name = "";
                            String org_name = "";
                            int raw_status = -1;
                            for (JsonElement jsonElement : rows) {
                                JsonObject jo = jsonElement.getAsJsonObject();
                                if (!jo.get("schedule_id").isJsonNull())
                                    schedule_id = jo.get("schedule_id").getAsInt();
                                if (!jo.get("customer_id").isJsonNull())
                                    customer_id = jo.get("customer_id").getAsInt();
                                if (!jo.get("status").isJsonNull())
                                    status = jo.get("status").getAsString();
                                if (!jo.get("start_time").isJsonNull())
                                    start_time = jo.get("start_time").getAsString();
                                if (!jo.get("end_time").isJsonNull())
                                    end_time = jo.get("end_time").getAsString();
                                if (!jo.get("engineer_id").isJsonNull())
                                    engineer_id = jo.get("engineer_id").getAsString();
                                if (!jo.get("bed_name").isJsonNull())
                                    bed_name = jo.get("bed_name").getAsString();
                                if (!jo.get("project_code").isJsonNull())
                                    project_code = jo.get("project_code").getAsString();
                                if (!jo.get("project_name").isJsonNull())
                                    project_name = jo.get("project_name").getAsString();
                                if (!jo.get("customer_name").isJsonNull())
                                    customer_name = jo.get("customer_name").getAsString();
                                if (!jo.get("engineer_name").isJsonNull())
                                    engineer_name = jo.get("engineer_name").getAsString();
                                if (!jo.get("org_name").isJsonNull())
                                    org_name = jo.get("org_name").getAsString();
                                if (!jo.get("raw_status").isJsonNull())
                                    raw_status = jo.get("raw_status").getAsInt();

                                list.add(new Order(schedule_id, customer_id, status, start_time, end_time, engineer_id,
                                        bed_name, project_code, project_name, customer_name, engineer_name, org_name, raw_status));
                            }
                            Message msg = Message.obtain();
                            msg.what = COLLEAGUE_ORDER;
                            msg.obj = list;
                            handler.sendMessage(msg);

                        } else {
                            showToast(message);
                        }
                        dismissShortDialog();
                        dismissLongDialog();
                    }
                });
    }

    /**
     * 获取项目订单列表
     *
     * @param project_id
     */
    public void getProjectOrder(int project_id) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/gethelperprojectorders")
                .addParams("token", token)
                .addParams("project_id", project_id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissShortDialog();
                        dismissLongDialog();
                        showToast(R.string.net_connect_error);
                        in_loading_error.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.e("项目222",response);
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String statuss = jsonObject.get("status").getAsString();
                        String message = jsonObject.get("message").getAsString();
                        if (statuss.equals("ok")) {
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            JsonArray rows = data.get("rows").getAsJsonArray();
                            int schedule_id = 0;
                            int customer_id = 0;
                            String status = "";
                            String start_time = "";
                            String end_time = "";
                            String engineer_id = "";
                            String bed_name = "";
                            String project_code = "";
                            String project_name = "";
                            String customer_name = "";
                            String engineer_name = "";
                            String org_name = "";
                            int raw_status = -1;
                            List<Order> list = new ArrayList<Order>();
                            for (JsonElement jsonElement : rows) {
                                JsonObject jo = jsonElement.getAsJsonObject();
                                if (!jo.get("schedule_id").isJsonNull())
                                    schedule_id = jo.get("schedule_id").getAsInt();
                                if (!jo.get("customer_id").isJsonNull())
                                    customer_id = jo.get("customer_id").getAsInt();
                                if (!jo.get("status").isJsonNull())
                                    status = jo.get("status").getAsString();
                                if (!jo.get("start_time").isJsonNull())
                                    start_time = jo.get("start_time").getAsString();
                                if (!jo.get("end_time").isJsonNull())
                                    end_time = jo.get("end_time").getAsString();
                                if (!jo.get("engineer_id").isJsonNull())
                                    engineer_id = jo.get("engineer_id").getAsString();
                                if (!jo.get("bed_name").isJsonNull())
                                    bed_name = jo.get("bed_name").getAsString();
                                if (!jo.get("project_code").isJsonNull())
                                    project_code = jo.get("project_code").getAsString();
                                if (!jo.get("project_name").isJsonNull())
                                    project_name = jo.get("project_name").getAsString();
                                if (!jo.get("customer_name").isJsonNull())
                                    customer_name = jo.get("customer_name").getAsString();
                                if (!jo.get("engineer_name").isJsonNull())
                                    engineer_name = jo.get("engineer_name").getAsString();
                                if (!jo.get("org_name").isJsonNull())
                                    org_name = jo.get("org_name").getAsString();
                                if (!jo.get("raw_status").isJsonNull())
                                    raw_status = jo.get("raw_status").getAsInt();

                                list.add(new Order(schedule_id, customer_id, status, start_time, end_time, engineer_id,
                                        bed_name, project_code, project_name, customer_name, engineer_name, org_name, raw_status));
                            }
                            Message msg = Message.obtain();
                            msg.what = PROJECT_ORDER;
                            msg.obj = list;
                            handler.sendMessage(msg);
                        }
                        dismissShortDialog();
                        dismissLongDialog();
                    }
                });
    }

    /**
     * 处理订单
     *
     * @param oldState
     * @param newState
     */
    public void dealWithOrder(final int oldState, final int newState, final int schedule_id) {
//        Log.e("token",token+"  old:"+oldState+" new:"+newState+" apiUrl:"+apiURL);
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/changeschedulestatus")
                .addParams("token", token)
                .addParams("old_status", oldState + "")
                .addParams("new_status", newState + "")
                .addParams("schedule_id", schedule_id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissLongDialog();
                        showToast(R.string.net_connect_error);
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.e("变更预约状态", response);
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String message = jsonObject.get("message").getAsString();
                        String status = jsonObject.get("status").getAsString();
                        if (status.equals("ok")) {
                            Message msg = Message.obtain();
                            if (newState == 0) {
                                //撤销
                                msg.what = CANCLE_PROJECT;
                            } else if (newState == 1) {
                                //开始
                                msg.what = START_PROJECT;
                            } else if (newState == 2) {
                                //结束
                                msg.what = FINISH_PROJECT;
                            }
                            handler.sendMessage(msg);
                        } else {
                            showToast(message);
                            dismissLongDialog();
                        }
                    }
                });
    }

    /**
     * 网络异常时使用
     *
     * @param view
     */
    public void loadingMore(View view) {
        showShortDialog();
        if (tag == 1) {
            getProjectOrder(project_id);
        } else if (tag == 2) {
            getColleagueOrder(user_id);
        } else if (tag == 3) {
            getCustomerOrder(customer_id);
        }
    }
}
