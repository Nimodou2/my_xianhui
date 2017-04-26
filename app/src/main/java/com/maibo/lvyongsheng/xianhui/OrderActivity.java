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
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
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

import static com.zhy.http.okhttp.OkHttpUtils.post;


/**
 * Created by LYS on 2016/10/9.
 */
public class OrderActivity extends BaseActivity implements View.OnClickListener {
    private android.support.v7.app.AlertDialog alertDialog;
    private int isAllMCom;
    private static final String TAG="OrderActivity";
    ListView lv_order;
    TextView cus_name, back;
    List<Order> productOrder;
    int project_id;
    int tag;
    int user_id;
    int customer_id;
    int org_id;
    SharedPreferences sp;
    String token, apiURL;
    int clickPosition = -1;
    boolean isClick = false;
    int fromWhere = 0;//用于区分订单数据来源 1、首次进入获取数据 2、内部操作订单时获取数据
    int haveDatas = 0;//用于判断当前界面是否已经展示
    List<Order> orderDatas;//为了点击变色使用方便
    Order orders;//记录当前正在操作的订单
    boolean footIsShow = false;//用于表示底部操作框是否显示
    int schedule_id = -1;//被点击的订单id
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
    @Bind(R.id.tv_strip_bg)
    TextView tvStripBg;
    private TextView text_tv_start_or_finish;
    OrderNewAdapter adapter;
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
                        setMyAdapter(customerOrder, clickPosition);//设置适配器
                        //更改按钮样式
                        int p = 0;
                        for (int i = 0; i < customerOrder.size(); i++) {
                            if (customerOrder.get(i).getSchedule_id() == schedule_id) {
                                changeBtnStyle(customerOrder.get(i).getRaw_status(), CUSTOMER_ORDER);
                                Log.e(TAG,"返回的rawstatus    "+customerOrder.get(i).getRaw_status()+"CUSTOMER_ORDER");
                                if (isClick) {
                                    EventDatas eventDatas = new EventDatas(Constants.ORDER_STATUS, customerOrder.get(i).getRaw_status() + "");
                                    EventBus.getDefault().post(eventDatas);
                                    isClick = false;
                                }
                                p = 1;
                            }
                        }
                        if (p == 0) {
                            if (footIsShow)
                                hideBottomMenu();
                        }
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

                        int p = 0;
                        for (int i = 0; i < colleagueOrder.size(); i++) {
                            if (colleagueOrder.get(i).getSchedule_id() == schedule_id) {
                                changeBtnStyle(colleagueOrder.get(i).getRaw_status(), COLLEAGUE_ORDER);
                                Log.e(TAG,"返回的rawstatus    "+colleagueOrder.get(i).getRaw_status()+"COLLEAGUE_ORDER");
                                p = 1;
                            }
                        }
                        if (p == 0) {
                            if (footIsShow)
                                hideBottomMenu();
                        }
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
                        int p = 0;
                        for (int i = 0; i < projectOrder.size(); i++) {
                            if (projectOrder.get(i).getSchedule_id() == schedule_id) {
                                changeBtnStyle(projectOrder.get(i).getRaw_status(), PROJECT_ORDER);
                                Log.e(TAG,"返回的rawstatus    "+projectOrder.get(i).getRaw_status()+"PROJECT_ORDER");
                                p = 1;
                            }
                        }
                        if (p == 0) {
                            if (footIsShow)
                                hideBottomMenu();
                        }
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
                    //从未完成界面进入
                    getCustomerScheduleList(date, customer_id, "undone");
                } else if (tag == 5) {
                    //从已完成界面进入
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
            if (rawStatus == 0) {//以预约
                ivCancle.setImageResource(R.mipmap.order_cancel_off);
                ivStartOrFinish.setImageResource(R.mipmap.order_begin_in);
                text_tv_start_or_finish.setText("开始");
            } else if (rawStatus == 1) {//进行中
                ivCancle.setImageResource(R.mipmap.order_cancel_in);
                ivStartOrFinish.setImageResource(R.mipmap.order_begin_in);
                text_tv_start_or_finish.setText("结束");
            }else if(rawStatus==2){//已结束
                ivCancle.setImageResource(R.mipmap.order_cancel_off);
                ivStartOrFinish.setImageResource(R.mipmap.order_begin_stop_cancle);
                text_tv_start_or_finish.setText("结束");
                text_tv_start_or_finish.setAlpha(0.5f);
            }
        } else if (fromWhere == COLLEAGUE_ORDER) {
            if (rawStatus == 0) {
                ivCancle.setImageResource(R.mipmap.order_cancel_off);
                ivStartOrFinish.setImageResource(R.mipmap.order_begin_in);
            } else if (rawStatus == 1) {
                ivCancle.setImageResource(R.mipmap.order_cancel_in);
                ivStartOrFinish.setImageResource(R.mipmap.order_begin_stop);
            }
        } else if (fromWhere == PROJECT_ORDER) {
            if (rawStatus == 0) {
                ivCancle.setImageResource(R.mipmap.order_cancel_off);
                ivStartOrFinish.setImageResource(R.mipmap.order_begin_in);
            } else if (rawStatus == 1) {
                ivCancle.setImageResource(R.mipmap.order_cancel_in);
                ivStartOrFinish.setImageResource(R.mipmap.order_begin_stop);
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
            if (orderDatas.get(i).getSchedule_id() == schedule_id) {
                map.put(i, 1);
                orders = orderDatas.get(i);
            } else map.put(i, 0);
        }
        adapter.setDatas(orderDatas, map);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Intent intent = getIntent();
        isAllMCom=intent.getIntExtra("allmessage",-1);
        initView();
        Bundle bundle = intent.getExtras();
        initDatas(intent, bundle);
        showBaseData(intent, bundle);
        initListener();
        if (tag != 5) {
            initTimerListener();
            handler.post(runnable);
        }
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
                    } else if (tag == 4) {
                        //从未完成界面进入
                        getCustomerScheduleList(date, customer_id, "undone");
                    } else if (tag == 5) {
                        //从已完成界面进入
                        getCustomerScheduleList(date, customer_id, "done");
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
        org_id=intent.getIntExtra("org_id",-1);
        if (!TextUtils.isEmpty(intent.getStringExtra("date")))
            date = intent.getStringExtra("date");
        adapter = new OrderNewAdapter(getApplicationContext());
        lv_order.setAdapter(adapter);
    }

    /**
     * 初始化View
     */
    private void initView() {
        text_tv_start_or_finish= (TextView) findViewById(R.id.tv_start_or_finish);
        //adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token = sp.getString("token", null);
        apiURL = sp.getString("apiURL", null);
        lv_order = (ListView) findViewById(R.id.lv_order);
        cus_name = (TextView) findViewById(R.id.cus_name);
        back = (TextView) findViewById(R.id.back);
        //界面适配
       /* View[] views = {rl_foot, tvStripBg, ivCancle, ivStartOrFinish, ivDetail};
        int[] heights = {(viewHeight * 2 / 15) + 10, viewHeight / 10, viewHeight * 3 / 40, viewHeight * 7 / 60, viewHeight * 3 / 40};
        setViewHeightAndWidth(views, heights, null);*/
        cus_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customer_id!=-1) {
                    Log.e(TAG,"这个是tag"+tag);
                    if(tag==3){
                        return;
                    }else {
                        Intent intent = new Intent(OrderActivity.this, PeopleMessageActivity.class);
                        intent.putExtra("customer_id", customer_id);
                        intent.putExtra("tag",tag);
                        startActivity(intent);
                    }
                }
            }
        });
    }

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
        lv_order.setSelected(true);
        lv_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if(position==adapter.getListSize()){
                    return;
                }
                clickPosition = position;
                schedule_id = orderDatas.get(position).getSchedule_id();
                setMyAdapter(orderDatas, position);
                footRawStatus = orderDatas.get(position).getRaw_status();
                //改变底部按钮的颜色
                changeBtnStyle(footRawStatus, CUSTOMER_ORDER);
//                schedule_id = orderDatas.get(position).getSchedule_id();
                rl_foot.setVisibility(View.VISIBLE);
                //因为滚动操作需要做很多工作，所以放在子线程中进行
                lv_order.post(new Runnable() {
                    @Override
                    public void run() {
                        lv_order.setSelection(position);
                        lv_order.smoothScrollToPosition(position);
                    }
                });
                if (!footIsShow) {
                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_to_top);
                    rl_foot.startAnimation(anim);
                    footIsShow = true;
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                         /*   //当底部菜单出现时ListView加上pdddingTop
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lv_order.getLayoutParams();
                            params.bottomMargin = viewHeight * 2 / 15;
                            lv_order.setLayoutParams(params);*/
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

            }
        });

        lv_order.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //当出现最后两个条目，再滑动Listview时底部菜单不消失
                if (scrollState == 0 && lv_order.getLastVisiblePosition() < lv_order.getCount() - 1) {
                    if (footIsShow) hideBottomMenu();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
                }else {
                    showDeleteDialog();
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
                if (orders != null)
                    initPopupWindow(orders);
                else Log.e("order", "为空");
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
        post()
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
        post()
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
        post()
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
        post()
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
                .addParams("org_id",org_id+"")
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
                    hideBottomMenu();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 隐藏底部菜单
     */
    private void hideBottomMenu() {
        Animation anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_to_bottom);
        rl_foot.startAnimation(anim2);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lv_order.getLayoutParams();
        params.bottomMargin = 0;
        lv_order.setLayoutParams(params);
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

    /**
     * 展示订单详情popupwindow
     *
     * @param orders
     */
    private void initPopupWindow(Order orders) {
//        Log.e("Popup:", "sss");
        View popView = getLayoutInflater().inflate(R.layout.pop_style_order_detail, null);
        ListView orderDetail = (ListView) popView.findViewById(R.id.lv_order_detail);
        TextView tv_close = (TextView) popView.findViewById(R.id.tv_close);
        List<Map<String, String>> mapList = new ArrayList<>();
        String[] name = {"门店", "床位", "项目名", "技师", "开始时间"};
        String orgName = "缺失";
        String bedName = "缺失";
        String projectName = "缺失";
        String engineerName = "缺失";
        String startTime = "缺失";
        if (!TextUtils.isEmpty(orders.getOrg_name()))
            orgName = orders.getOrg_name();
        if (!TextUtils.isEmpty(orders.getBed_name()))
            bedName = orders.getBed_name();
        if (!TextUtils.isEmpty(orders.getProject_name()))
            projectName = orders.getProject_name();
        if (!TextUtils.isEmpty(orders.getEngineer_name()))
            engineerName = orders.getEngineer_name();
        if (!TextUtils.isEmpty(orders.getStart_time()))
            startTime = orders.getStart_time();
        String[] content = {orgName, bedName, projectName, engineerName, startTime};
        Map<String, String> map;
        for (int i = 0; i < name.length; i++) {
            map = new HashMap<>();
            map.put("name", name[i]);
            map.put("content", content[i]);
            mapList.add(map);
        }
        final PopupWindow popupWindow = new PopupWindow(popView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.AnimationBottonAndTop);
        popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.fragment_unfinished_order, null), Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.5f);
        popupWindow.setOnDismissListener(new popupDismissListener()); //关闭事件
        orderDetail.setAdapter(new SimpleAdapter(this, mapList, R.layout.style_order_detail, new String[]{"name", "content"}, new int[]{R.id.tv_name, R.id.tv_content}));
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    class popupDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }

    }
    public void showDeleteDialog(){
        android.support.v7.app.AlertDialog.Builder alertBuilder=new android.support.v7.app.AlertDialog.Builder(this);
        alertBuilder.setTitle("提示:");
        alertBuilder.setIcon(R.mipmap.testlogo);
        alertBuilder.setMessage("该项目已经结束或未开始不能取消...");
        alertBuilder.create();//创建这个对话框
        //为对话框设置消极、积极、与中立 的按钮（是接口自定义的我们直接用）
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog=alertBuilder.show();//显示这个对话框
    }
}
