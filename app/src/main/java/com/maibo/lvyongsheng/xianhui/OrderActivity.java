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
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.adapter.OrderNewAdapter;
import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.maibo.lvyongsheng.xianhui.entity.Order;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import de.greenrobot.event.EventBus;
import okhttp3.Call;


/**
 * Created by LYS on 2016/10/9.
 */
public class OrderActivity extends BaseActivity implements View.OnClickListener {
    ListView lv_order;
    TextView cus_name, back;
    List<Order> productOrder;
    int project_id;
    int tag;
    int user_id;
    int customer_id;
    SharedPreferences sp;
    String token, apiURL;
    int clickPosition;
    boolean isClick = false;
    int fromWhere = 0;//用于区分订单数据来源 1、首次进入获取数据 2、内部操作订单时获取数据
    int haveDatas = 0;//用于判断当前界面是否已经展示
    List<Order> orderDatas;//为了点击变色使用方便
    boolean footIsShow = false;//用于表示底部操作框是否显示
    int schedule_id;//被点击的订单id
    int footRawStatus;//被点击条目的订单状态
    String date = "";//顾客订单查询所需日期

    //用于判断是否有新的订单加入
    int newSize = 0;
    int oldSize = 0;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.in_no_datas)
    LinearLayout in_no_datas;
    @Bind(R.id.in_loading_error)
    LinearLayout in_loading_error;
    //    @Bind(R.id.swip_refresh)
//    SwipeRefreshLayout refreshLayout;
    //与操作项目状态相关
    @Bind(R.id.rl_foot)
    RelativeLayout rl_foot;
    @Bind(R.id.iv_cancle)
    ImageView ivCancle;
    @Bind(R.id.iv_start_or_finish)
    ImageView ivStartOrFinish;
    @Bind(R.id.iv_detail)
    ImageView ivDetail;

    OrderNewAdapter adapter;
    //    OrderAdapter adapter;
    final int CUSTOMER_ORDER = 0, COLLEAGUE_ORDER = 1, PROJECT_ORDER = 2,
            START_PROJECT = 3, CANCLE_PROJECT = 4, FINISH_PROJECT = 5,
            STATUS_CHANGED = 6, TIMER_LISTENER = 7;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dealHandMsg(msg);
        }
    };

    /**
     * 消息处理
     *
     * @param msg
     */
    private void dealHandMsg(Message msg) {
        switch (msg.what) {
            case CUSTOMER_ORDER:
                List<Order> customerOrder = (List<Order>) msg.obj;
                if (customerOrder.size() > 0) {
                    if (fromWhere == 1) {
                        //刷新Adapter
                        if (isClick) {
                            EventDatas eventDatas = new EventDatas(Constants.ORDER_STATUS, customerOrder.get(clickPosition).getRaw_status() + "");
                            EventBus.getDefault().post(eventDatas);
                            isClick = false;
                        }
                        setMyAdapter(customerOrder, clickPosition);//设置适配器
                        //更改按钮样式
                        changeBtnStyle(customerOrder.get(clickPosition).getRaw_status(), CUSTOMER_ORDER);
                    } else {
                        haveDatas = 1;
                        setMyAdapter(customerOrder, -1);

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
                        setMyAdapter(colleagueOrder, clickPosition);
                        changeBtnStyle(colleagueOrder.get(clickPosition).getRaw_status(), COLLEAGUE_ORDER);
                    } else {
                        haveDatas = 1;
                        setMyAdapter(colleagueOrder, -1);
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
                        setMyAdapter(projectOrder, clickPosition);
                        changeBtnStyle(projectOrder.get(clickPosition).getRaw_status(), PROJECT_ORDER);
                    } else {
                        haveDatas = 1;
                        setMyAdapter(projectOrder, -1);

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
                    getCustomerOrder(customer_id, date);
                } else if (tag == 4) {
                    getCustomerScheduleList(date, customer_id, "undone");
                } else if (tag == 5) {
                    getCustomerScheduleList(date, customer_id, "done");
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
                    getCustomerOrder(customer_id, date);
                } else if (tag == 4) {
                    getCustomerScheduleList(date, customer_id, "undone");
                } else if (tag == 5) {
                    getCustomerScheduleList(date, customer_id, "done");
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
                    getCustomerOrder(customer_id, date);
                } else if (tag == 4) {
                    getCustomerScheduleList(date, customer_id, "undone");
                } else if (tag == 5) {
                    getCustomerScheduleList(date, customer_id, "done");
                }
                showToast("项目已结束");
                break;
            case STATUS_CHANGED:
                if (tag == 1) {
                    //项目
                    getProjectOrder(project_id);
                } else if (tag == 2) {
                    //同事
                    getColleagueOrder(user_id);
                } else if (tag == 3) {
                    //顾客
                    getCustomerOrder(customer_id, date);
                } else if (tag == 4) {
                    getCustomerScheduleList(date, customer_id, "undone");
                } else if (tag == 5) {
                    getCustomerScheduleList(date, customer_id, "done");
                }
                break;
        }
    }

    /**
     * 改变底部操作栏中按钮的状态
     *
     * @param rawStatus
     * @param fromWhere
     */
    private void changeBtnStyle(int rawStatus, int fromWhere) {
        footRawStatus = rawStatus;
        if (fromWhere == CUSTOMER_ORDER) {
            if (rawStatus == 0) {
                ivCancle.setImageResource(R.mipmap.cancelbtn_disabl);
                ivStartOrFinish.setImageResource(R.mipmap.begain_btn);

            } else if (rawStatus == 1) {
                ivCancle.setImageResource(R.mipmap.cancel_btn_normal);
                ivStartOrFinish.setImageResource(R.mipmap.end_btn);
            }
        } else if (fromWhere == COLLEAGUE_ORDER) {
            if (rawStatus == 0) {
                ivCancle.setImageResource(R.mipmap.cancelbtn_disabl);
                ivStartOrFinish.setImageResource(R.mipmap.begain_btn);
            } else if (rawStatus == 1) {
                ivCancle.setImageResource(R.mipmap.cancel_btn_normal);
                ivStartOrFinish.setImageResource(R.mipmap.end_btn);
            }
        } else if (fromWhere == PROJECT_ORDER) {
            if (rawStatus == 0) {
                ivCancle.setImageResource(R.mipmap.cancelbtn_disabl);
                ivStartOrFinish.setImageResource(R.mipmap.begain_btn);
            } else if (rawStatus == 1) {
                ivCancle.setImageResource(R.mipmap.cancel_btn_normal);
                ivStartOrFinish.setImageResource(R.mipmap.end_btn);
            }
        }
    }

    /**
     * 设置适配器
     *
     * @param orderDatas
     * @param position
     */
    private void setMyAdapter(List<Order> orderDatas, int position) {
        this.orderDatas = orderDatas;
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < orderDatas.size(); i++) {
            if (position == i) map.put(i, 1);
            else map.put(i, 0);
        }
        adapter.setDatas(orderDatas, map);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        initDatas(intent, bundle);
        showBaseData(intent, bundle);
        initListener();
//        initListener();
//        if (tag != 4) {
//            initTimerListener();
//            handler.post(runnable);
//        }
    }


    /**
     * 启动时间监听
     */
    Runnable runnable;

    private void initTimerListener() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (haveDatas == 1) {
                    fromWhere = 1;
                    if (tag == 1) {
                        //项目
                        getProjectOrder(project_id);
                    } else if (tag == 2) {
                        //同事
                        getColleagueOrder(user_id);
                    } else if (tag == 3) {
                        //顾客
                        getCustomerOrder(customer_id, date);
                    }
                }
                handler.postDelayed(this, 3000);
            }
        };
    }

    /**
     * 初始化数据
     *
     * @param intent
     * @param bundle
     */
    private void initDatas(Intent intent, Bundle bundle) {
        productOrder = (List<Order>) bundle.get("productOrder");
        customer_id = intent.getIntExtra("customer_id", -1);
        tag = intent.getIntExtra("tag", -1);
        project_id = intent.getIntExtra("project_id", -1);
        user_id = intent.getIntExtra("user_id", -1);
        if (!TextUtils.isEmpty(intent.getStringExtra("date")))
            date = intent.getStringExtra("date");
        adapter = new OrderNewAdapter(getApplicationContext());
        lv_order.setAdapter(adapter);
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
//        //刷新页面
//        refreshLayout.setEnabled(true);
//        //设置卷内的颜色
//        refreshLayout.setColorSchemeResources(R.color.colorLightYellow,
//                R.color.colorLightYellow, R.color.colorLightYellow, R.color.colorLightYellow);
//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                fromWhere = 0;
//                if (tag == 1) {
//                    getProjectOrder(project_id);
//                } else if (tag == 2) {
//                    getColleagueOrder(user_id);
//                } else if (tag == 3) {
//                    getCustomerOrder(customer_id);
//                }
//                refreshLayout.setRefreshing(false);
//            }
//        });
    }

    /**
     * 监听按钮点击事件
     */
//    private void initListener() {
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//        adapter.setOnMyButtonClickListener(new OrderStateListener() {
//            @Override
//            public void startOrder(int position, int schedule_id) {
//                showDialog("确定开始项目吗？", 0, schedule_id);
//                clickPosition = position;
//                fromWhere = 1;
//            }
//
//            @Override
//            public void cancelOrder(int position, int schedule_id) {
//                showDialog("确定取消项目吗？", 1, schedule_id);
//                clickPosition = position;
//                fromWhere = 1;
//            }
//
//            @Override
//            public void finishOrder(int position, int schedule_id) {
//                showDialog("确定结束项目吗？", 2, schedule_id);
//                clickPosition = position;
//                fromWhere = 1;
//            }
//        });
//
//        lv_order.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem == 0)
//                    refreshLayout.setEnabled(true);
//                else
//                    refreshLayout.setEnabled(false);
//            }
//        });
//
//    }

    /**
     * 展示不同订单
     *
     * @param intent
     * @param bundle
     */
    private void showBaseData(Intent intent, Bundle bundle) {
        if (tag == 0) {
            if (productOrder != null && productOrder.size() > 0) {
                cus_name.setText(productOrder.get(0).getItem_name());
                setMyAdapter(productOrder, -1);
            } else {
                cus_name.setText(intent.getStringExtra("productName"));
                in_no_datas.setVisibility(View.VISIBLE);
            }

        } else if (tag == 1) {
            showShortDialog();
            getProjectOrder(project_id);
            cus_name.setText(intent.getStringExtra("projectName"));
        } else if (tag == 2) {
            showShortDialog();
            getColleagueOrder(user_id);
            cus_name.setText(intent.getStringExtra("collName"));
        } else if (tag == 3) {
            showShortDialog();
            getCustomerOrder(customer_id, date);
            cus_name.setText(intent.getStringExtra("customer_name"));
        } else if (tag == 4) {
            getCustomerScheduleList(intent.getStringExtra("date"), customer_id, "undone");
            if (!TextUtils.isEmpty(intent.getStringExtra("customer_name")))
                cus_name.setText(intent.getStringExtra("customer_name"));
        } else if (tag == 5) {
            getCustomerScheduleList(intent.getStringExtra("date"), customer_id, "done");
            if (!TextUtils.isEmpty(intent.getStringExtra("customer_name")))
                cus_name.setText(intent.getStringExtra("customer_name"));
        }
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        back.setOnClickListener(this);
        ivCancle.setOnClickListener(this);
        ivStartOrFinish.setOnClickListener(this);
        ivDetail.setOnClickListener(this);
        lv_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickPosition = position;
                setMyAdapter(orderDatas, position);
                footRawStatus = orderDatas.get(position).getRaw_status();
                if (footRawStatus == 2 || footRawStatus == 3) return;
                //改变底部按钮的颜色
                changeBtnStyle(footRawStatus, CUSTOMER_ORDER);
                schedule_id = orderDatas.get(position).getSchedule_id();
                rl_foot.setVisibility(View.VISIBLE);
                if (!footIsShow) {
                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_to_top);
                    rl_foot.startAnimation(anim);
                    footIsShow = true;
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.iv_cancle:
                //取消
                if (footRawStatus == 1) {
                    showLongDialog();
                    dealWithOrder(1, 0, schedule_id);
                    isClick = true;
                    fromWhere = 1;
                }
                break;
            case R.id.iv_start_or_finish:

                if (footRawStatus == 0) {
                    //开始
                    showLongDialog();
                    dealWithOrder(0, 1, schedule_id);
                } else if (footRawStatus == 1) {
                    //结束
                    showLongDialog();
                    dealWithOrder(1, 2, schedule_id);
                }
                isClick = true;
                fromWhere = 1;
                break;
            case R.id.iv_detail:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
        handler.removeCallbacks(runnable);
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
                    isClick = true;

                } else if (what == 1) {
                    //取消
                    dealWithOrder(1, 0, schedule_id);
                    isClick = true;

                } else if (what == 2) {
                    //结束
                    dealWithOrder(1, 2, schedule_id);
                    isClick = true;

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
    public void getCustomerOrder(int customer_id, String date) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/gethelpercustomerschedulelist")
                .addParams("token", token)
                .addParams("customer_id", customer_id + " token:" + token)
                .addParams("date", date)
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
                            Message msg = Message.obtain();
                            msg.what = STATUS_CHANGED;
                            handler.sendMessage(msg);
                        }
                    }
                });
    }

    /**
     * 按日期获取预约列表
     *
     * @param date
     * @param customerID
     */
    private void getCustomerScheduleList(String date, int customerID, String type) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getcustomerschedulelist")
                .addParams("token", token)
                .addParams("customer_id", customerID + "")
                .addParams("date", date)
                .addParams("type", type)
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
                        JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                        String statuss = jo.get("status").getAsString();
                        String message = jo.get("message").getAsString();
                        JsonArray data = jo.get("data").getAsJsonArray();
                        if (statuss.equals("ok") && jo.get("data").isJsonArray()) {
                            List<Order> list = new ArrayList<Order>();
                            for (JsonElement je : data) {
                                JsonObject jsob = je.getAsJsonObject();
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
                                if (!jsob.get("schedule_id").isJsonNull())
                                    schedule_id = jsob.get("schedule_id").getAsInt();
                                if (!jsob.get("start_time").isJsonNull())
                                    status = jsob.get("status").getAsString();
                                if (!jsob.get("start_time").isJsonNull())
                                    start_time = jsob.get("start_time").getAsString();
                                if (!jsob.get("end_time").isJsonNull())
                                    end_time = jsob.get("end_time").getAsString();
                                if (!jsob.get("engineer_id").isJsonNull())
                                    engineer_id = jsob.get("engineer_id").getAsString();
                                if (!jsob.get("bed_name").isJsonNull())
                                    bed_name = jsob.get("bed_name").getAsString();
                                if (!jsob.get("project_code").isJsonNull())
                                    project_code = jsob.get("project_code").getAsString();
                                if (!jsob.get("project_name").isJsonNull())
                                    project_name = jsob.get("project_name").getAsString();
                                if (!jsob.get("avator_url").isJsonNull())
                                    avator_url = jsob.get("avator_url").getAsString();
                                if (!jsob.get("guid").isJsonNull())
                                    guid = jsob.get("guid").getAsString();
                                if (!jsob.get("customer_id").isJsonNull())
                                    customer_id = jsob.get("customer_id").getAsInt();
                                if (!jsob.get("customer_name").isJsonNull())
                                    customer_name = jsob.get("customer_name").getAsString();
                                if (!jsob.get("engineer_name").isJsonNull())
                                    engineer_name = jsob.get("engineer_name").getAsString();
                                if (!jsob.get("org_name").isJsonNull())
                                    org_name = jsob.get("org_name").getAsString();
                                if (!jsob.get("raw_status").isJsonNull())
                                    raw_status = jsob.get("raw_status").getAsInt();
                                list.add(new Order(schedule_id, status, start_time, end_time, engineer_id,
                                        bed_name, project_code, project_name, avator_url, guid, customer_id,
                                        customer_name, engineer_name, org_name, raw_status));
                            }
                            Message msg = Message.obtain();
                            msg.what = CUSTOMER_ORDER;
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
            getCustomerOrder(customer_id, date);
        }
    }

    /**
     * 监听当前界面空白处是否被点击
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (this.getCurrentFocus() != null) {
                if (this.getCurrentFocus().getWindowToken() != null) {
                    if (!footIsShow) return super.onTouchEvent(event);
                    Animation anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_to_bottom);
                    rl_foot.startAnimation(anim2);
                    anim2.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            rl_foot.setVisibility(View.GONE);
                            footIsShow = false;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }
        }
        return super.onTouchEvent(event);
    }

}
