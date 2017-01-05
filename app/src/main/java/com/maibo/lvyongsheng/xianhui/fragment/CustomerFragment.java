package com.maibo.lvyongsheng.xianhui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.App;
import com.maibo.lvyongsheng.xianhui.PeopleMessageActivity;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.WorkActivity;
import com.maibo.lvyongsheng.xianhui.adapter.CustomerAdapter;
import com.maibo.lvyongsheng.xianhui.adapter.MyGridViewAdapter;
import com.maibo.lvyongsheng.xianhui.entity.HelperCustomer;
import com.maibo.lvyongsheng.xianhui.entity.Order;
import com.maibo.lvyongsheng.xianhui.entity.SelectEntity;
import com.maibo.lvyongsheng.xianhui.entity.SelectEntitys;
import com.maibo.lvyongsheng.xianhui.implement.MyProgressDialog;
import com.maibo.lvyongsheng.xianhui.implement.Util;
import com.maibo.lvyongsheng.xianhui.view.WorkRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by LYS on 2016/9/29.
 */
public class CustomerFragment extends Fragment implements WorkRefreshListView.OnRefreshListener {
    WorkRefreshListView lv_customer_list;
    SharedPreferences sp;
    String token, apiURL;
    CustomerAdapter myAdapter;
    List<HelperCustomer> list1;
    String customer_name;
    int customer_id1;
    List<Order> customerOrder;
    TextView tv_open_close;
    List<SelectEntitys> ses;
    List<SelectEntity> seTwoList;
    ProgressDialog dialog;
    MyProgressDialog myDialog;
    //标记是否展开
    int isOpen = 0;
    //记录点击状态
    String buffer1 = "";
    String buffer2 = "";
    String buffer3 = "";
    String buffer4 = "";
    MyGridViewAdapter adapter1, adapter2, adapter3, adapter4;
    GridView gd_list, gd_list2, gd_list3, gd_list4;
    TextView tv_choose_tesult;
    Boolean isLoadingMore = false;
    int currentPageNum;
    int totalPage;

    int screenHeight;
    ////////////////////////////////////////////////

    private Context context = null;
    //private PopupWindow popupWindow;

    /////////////////////////////////////////////////

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    List<HelperCustomer> list = (List<HelperCustomer>) msg.obj;
                    currentPageNum = msg.arg1;
                    totalPage = msg.arg2;
                    if (isLoadingMore && list != null) {
                        list1.addAll(list);
                        myAdapter.notifyDataSetChanged();
                    } else {
                        list1.clear();
                        list1 = list;
                        lv_customer_list.setAdapter(myAdapter = new CustomerAdapter(getContext(), list1, screenHeight));
                    }
                    lv_customer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            customer_id1 = list1.get(i - 1).getCustomer_id();
                            Intent intent = new Intent(getActivity(), PeopleMessageActivity.class);
                            intent.putExtra("customer_id", customer_id1);
                            startActivity(intent);
                        }
                    });
                    lv_customer_list.completeRefresh();
                    break;
                case 2:
                    //此处暂时无用
                    customerOrder = (List<Order>) msg.obj;
                    Intent intent = new Intent(getActivity(), PeopleMessageActivity.class);
                    intent.putExtra("customer_id", customer_id1);
                    startActivity(intent);
                    break;
                case 3:
                    ses = (List<SelectEntitys>) msg.obj;
                    //用于展开和闭合
                    SelectEntitys seTwo = ses.get(1);
                    seTwoList = new ArrayList<>();
                    //用于显示筛选结果
                    if (msg.arg1 == 1) {
                        tv_choose_tesult.setText("筛选结果：" + ses.get(0).getFilterResult() + "人");
                    }
                    if (ses.get(1).getList().size() > 6) {
                        for (int i = 0; i < 6; i++) {
                            seTwoList.add(seTwo.getList().get(i));
                        }
                    } else {
                        seTwoList = seTwo.getList();
                    }
                    if (gd_list != null)
                        gd_list.setAdapter(adapter1 = new MyGridViewAdapter(getActivity(), ses.get(0).getList()));
                    if (gd_list2 != null) {
                        if (isOpen == 0) {
                            gd_list2.setAdapter(adapter2 = new MyGridViewAdapter(getActivity(), seTwoList));
                        } else {
                            gd_list2.setAdapter(adapter2 = new MyGridViewAdapter(getActivity(), ses.get(1).getList()));
                        }
                    }
                    if (gd_list3 != null)
                        gd_list3.setAdapter(adapter1 = new MyGridViewAdapter(getActivity(), ses.get(2).getList()));
                    if (gd_list4 != null)
                        gd_list4.setAdapter(adapter1 = new MyGridViewAdapter(getActivity(), ses.get(3).getList()));
                    break;
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        list1 = new ArrayList<>();
        lv_customer_list = (WorkRefreshListView) view.findViewById(R.id.lv_customer_list);
        lv_customer_list.setOnRefreshListener(this);
//        myAdapter=new CustomerAdapter(getContext(),list1);
        //屏幕高度
        WorkActivity parentActivity = (WorkActivity) getActivity();
        screenHeight = Util.getScreenHeight(getContext()) - parentActivity.getStatusBarHeight();

        sp = getActivity().getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token = sp.getString("token", null);
        apiURL = sp.getString("apiURL", null);
        myDialog = new MyProgressDialog(getActivity());
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
        dialog.show();
        getServiceData("", "", "", "", 1);
        getChooseData("", "", "", "", 0);
        return view;
    }

    @Override
    public void onPullRefresh() {
        //下拉刷新
        isLoadingMore = false;
        getServiceData("", "", "", "", 1);
    }

    @Override
    public void onLoadingMore() {
        //上滑加载更多
        isLoadingMore = true;
        if (currentPageNum != totalPage) {
            getServiceData("", "", "", "", currentPageNum + 1);
        } else {
            App.showToast(getActivity(), "已加载全部!");
            lv_customer_list.completeRefresh();
        }
    }

    //获取客户订单,此处无用
    public void getCustomerOrder(int customer_id) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/gethelpercustomerschedulelist")
                .addParams("token", token)
                .addParams("customer_id", customer_id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String statuss = jsonObject.get("status").getAsString();
                        String message = jsonObject.get("message").getAsString();
                        if (statuss.equals("ok")) {
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            JsonArray rows = data.get("rows").getAsJsonArray();
                            List<Order> list = new ArrayList<Order>();
                            for (JsonElement jsonElement : rows) {
                                JsonObject jo = jsonElement.getAsJsonObject();
                                int schedule_id = jo.get("schedule_id").getAsInt();
                                //int customer_id=jo.get("customer_id").getAsInt();
                                String status = jo.get("status").getAsString();
                                String start_time = jo.get("start_time").getAsString();
                                String end_time = jo.get("end_time").getAsString();
                                String engineer_id = jo.get("engineer_id").getAsString();
                                String bed_name = jo.get("bed_name").getAsString();
                                String project_code = jo.get("project_code").getAsString();
                                String project_name = jo.get("project_name").getAsString();
                                //String customer_name=jo.get("customer_name").getAsString();
                                String engineer_name = jo.get("engineer_name").getAsString();

                                list.add(new Order(schedule_id, customer_id1, status, start_time, end_time, engineer_id,
                                        bed_name, project_code, project_name, customer_name, engineer_name));
                            }
                            Message msg = Message.obtain();
                            msg.what = 2;
                            msg.obj = list;
                            handler.sendMessage(msg);

                        } else {
                            App.showToast(getActivity(), message);
                        }
                    }
                });
    }

    //获取客户详细资料
    public void getServiceData(String org_id, String adviser_id, String vip_star, String arrival_time, int pageNum) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/gethelpercustomerlist")
                .addParams("token", token)
                .addParams("org_id", org_id)
                .addParams("adviser_id", adviser_id)
                .addParams("vip_star", vip_star)
                .addParams("arrival_time", arrival_time)
                .addParams("pageNumber", pageNum + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        App.showToast(getActivity(), "网络连接异常");
                        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // Log.e("顾客:",response);
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String msg_status = jsonObject.get("status").getAsString();
                        String message = jsonObject.get("message").getAsString();
                        if (msg_status.equals("ok")) {
                            analysisJson(jsonObject);
                        } else {
                            App.showToast(getActivity(), message);
                        }
                        dialog.dismiss();
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
        int pageNumber = -1;
        int totalPage = -1;
        if (!data.get("pageNumber").isJsonNull())
            pageNumber = data.get("pageNumber").getAsInt();
        if (!data.get("totalPage").isJsonNull())
            totalPage = data.get("totalPage").getAsInt();
        JsonArray rows = data.get("rows").getAsJsonArray();
        if (rows != null) {
            List<HelperCustomer> list = new ArrayList<HelperCustomer>();
            for (JsonElement jsonElement : rows) {
                JsonObject job = jsonElement.getAsJsonObject();
                String org_name = "";
                int org_id = -1;
                String vip_star = "";
                int customer_id = -1;
                String fullname = "";
                String avator_url = "";
                String last_consume_time = "";
                String guid = "";
                String days = "";
                int project_total = 0;
                String schedule_date = "";
                String schedule_time = "";
                int status = -1;
                if (!job.get("org_name").isJsonNull())
                    org_name = job.get("org_name").getAsString();
                if (!job.get("org_id").isJsonNull())
                    org_id = job.get("org_id").getAsInt();
                if (!job.get("vip_star").isJsonNull())
                    vip_star = job.get("vip_star").getAsString();
                if (!job.get("customer_id").isJsonNull())
                    customer_id = job.get("customer_id").getAsInt();
                if (!job.get("fullname").isJsonNull())
                    fullname = job.get("fullname").getAsString();
                if (!job.get("avator_url").isJsonNull())
                    avator_url = job.get("avator_url").getAsString();
                if (!job.get("last_consume_time").isJsonNull())
                    last_consume_time = job.get("last_consume_time").getAsString();
                if (!job.get("guid").isJsonNull())
                    guid = job.get("guid").getAsString();
                if (!job.get("days").isJsonNull())
                    days = job.get("days").getAsString();
                if (!job.get("project_total").isJsonNull())
                    project_total = job.get("project_total").getAsInt();
                if (!job.get("schedule_date").isJsonNull())
                    schedule_date = job.get("schedule_date").getAsString();
                if (!job.get("schedule_time").isJsonNull())
                    schedule_time = job.get("schedule_time").getAsString();
                if (!job.get("status").isJsonNull())
                    status = job.get("status").getAsInt();
                list.add(new HelperCustomer(org_name, vip_star, customer_id, fullname, avator_url, days, project_total, status));
            }
            Message msg = Message.obtain();
            msg.what = 1;
            msg.obj = list;
            msg.arg1 = pageNumber;
            msg.arg2 = totalPage;
            handler.sendMessage(msg);
        }
    }

    //获取筛选数据
    public void getChooseData(String org_id, String adviser_id, String vip_star, String arrival_time, final int isDialog) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/gethelpercustomerlistsearchinfo")
                .addParams("token", token)
                .addParams("org_id", org_id)
                .addParams("adviser_id", adviser_id)
                .addParams("vip_star", vip_star)
                .addParams("arrival_time", arrival_time)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        JsonArray data = jsonObject.get("data").getAsJsonArray();
                        List<SelectEntitys> entitys_list = new ArrayList<SelectEntitys>();
                        for (JsonElement je : data) {
                            String name = "";
                            String param = "";
                            String filterResult = "";
                            JsonObject jo = je.getAsJsonObject();
                            if (!jo.get("name").isJsonNull()) {
                                name = jo.get("name").getAsString();
                            }
                            if (!jo.get("param").isJsonNull()) {
                                param = jo.get("param").getAsString();
                            }
                            if (!jo.get("filterResult").isJsonNull()) {
                                filterResult = jo.get("filterResult").getAsString();
                            }
                            JsonArray list = jo.get("list").getAsJsonArray();
                            List<SelectEntity> entity_list = new ArrayList<SelectEntity>();
                            for (JsonElement je2 : list) {
                                String value = "";
                                String text = "";
                                Boolean selected = false;
                                Boolean disabled = false;
                                JsonObject jo2 = je2.getAsJsonObject();
                                if (!jo2.get("value").isJsonNull()) {
                                    value = jo2.get("value").getAsString();
                                }
                                if (!jo2.get("text").isJsonNull()) {
                                    text = jo2.get("text").getAsString();
                                }
                                if (jo2.has("selected")) {
                                    if (!jo2.get("selected").isJsonNull()) {
                                        selected = jo2.get("selected").getAsBoolean();
                                    }
                                } else if (jo2.has("disabled")) {
                                    if (!jo2.get("disabled").isJsonNull()) {
                                        disabled = jo2.get("disabled").getAsBoolean();
                                    }
                                }

                                entity_list.add(new SelectEntity(value, text, selected, disabled));
                            }
                            entitys_list.add(new SelectEntitys(name, param, filterResult, entity_list));

                        }
                        Message msg = Message.obtain();
                        msg.what = 3;
                        msg.obj = entitys_list;
                        msg.arg1 = 0;

                        if (isDialog == 1) {
                            msg.arg1 = 1;
                            myDialog.dismiss();
                        }
                        handler.sendMessage(msg);
                    }
                });

    }


    public void initPopupWindow() {
        View popupWindowView = getLayoutInflater(getArguments()).inflate(R.layout.style_pop, null);
        //设置popwindow的宽和高
        PopupWindow popupWindow = new PopupWindow(popupWindowView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT, true);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        //设置popwindow出现和消失的动画效果
        popupWindow.setAnimationStyle(R.style.AnimationRightFade);
        //菜单背景色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(dw);
        //显示位置
        //宽度
        popupWindow.setWidth(width * 6 / 7);
        //高度
        popupWindow.setHeight(height - getStatusBarHeight());
        popupWindow.showAtLocation(getLayoutInflater(getArguments()).inflate(R.layout.fragment_customer, null), Gravity.RIGHT, 0, 0);
        //设置背景半透明
        backgroundAlpha(0.5f);
        //关闭事件
        popupWindow.setOnDismissListener(new popupDismissListener());

        popupWindowView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*if( popupWindow!=null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                    popupWindow=null;
                }*/
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });
        contralPopWindowData(popupWindowView, popupWindow);
    }

    private void contralPopWindowData(View popupWindowView, final PopupWindow popupWindow) {
        //获取popwindow里面的组件
        gd_list = (GridView) popupWindowView.findViewById(R.id.gd_list);
        gd_list2 = (GridView) popupWindowView.findViewById(R.id.gd_list2);
        gd_list3 = (GridView) popupWindowView.findViewById(R.id.gd_list3);
        gd_list4 = (GridView) popupWindowView.findViewById(R.id.gd_list4);
        TextView tv_name1 = (TextView) popupWindowView.findViewById(R.id.tv_name1);
        TextView tv_name2 = (TextView) popupWindowView.findViewById(R.id.tv_name2);
        TextView tv_name3 = (TextView) popupWindowView.findViewById(R.id.tv_name3);
        TextView tv_name4 = (TextView) popupWindowView.findViewById(R.id.tv_name4);
        tv_open_close = (TextView) popupWindowView.findViewById(R.id.tv_open_close);
        tv_choose_tesult = (TextView) popupWindowView.findViewById(R.id.tv_choose_tesult);
        if (ses.size() > 0)
            tv_choose_tesult.setText("筛选结果：" + ses.get(0).getFilterResult());
        gd_list.setAdapter(adapter1 = new MyGridViewAdapter(getActivity(), ses.get(0).getList()));
        tv_name1.setText(ses.get(0).getName());
        gd_list2.setAdapter(adapter2 = new MyGridViewAdapter(getActivity(), seTwoList));
        tv_name2.setText(ses.get(1).getName());
        gd_list3.setAdapter(adapter3 = new MyGridViewAdapter(getActivity(), ses.get(2).getList()));
        tv_name3.setText(ses.get(2).getName());
        gd_list4.setAdapter(adapter4 = new MyGridViewAdapter(getActivity(), ses.get(3).getList()));
        tv_name4.setText(ses.get(3).getName());
        //记录点击状态
        gd_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /**
                 * 需求：1、保留刷新前状态；2、保留其它项选中状态；
                 */
                //可点击状态下才显示
                if (!ses.get(0).getList().get(i).getDisabled()) {
                    myDialog.show();
                }
                if (!ses.get(0).getList().get(i).getSelected() && !ses.get(0).getList().get(i).getDisabled()) {
                    //刷新适配器，显示筛选结果
                    buffer1 += "," + ses.get(0).getList().get(i).getValue();
                    String values11 = buffer1.substring(1);
                    String values22 = "";
                    String values33 = "";
                    String values44 = "";
                    if (buffer2.length() > 0)
                        values22 = buffer2.substring(1);
                    if (buffer3.length() > 0)
                        values33 = buffer3.substring(1);
                    if (buffer4.length() > 0)
                        values44 = buffer4.substring(1);
                    getChooseData(values11, values22, values33, values44, 1);
                    //getServiceData(values11,values22,values33,values44);
                } else if (ses.get(0).getList().get(i).getSelected() && !ses.get(0).getList().get(i).getDisabled()) {
                    //刷新适配器，显示筛选结果
                    buffer1 = buffer1.replace("," + ses.get(0).getList().get(i).getValue(), "");
                    String values11 = "";
                    String values22 = "";
                    String values33 = "";
                    String values44 = "";
                    if (buffer1.lastIndexOf(",") != -1) {
                        values11 = buffer1.substring(1);
                    }
                    if (buffer2.length() > 0)
                        values22 = buffer2.substring(1);
                    if (buffer3.length() > 0)
                        values33 = buffer3.substring(1);
                    if (buffer4.length() > 0)
                        values44 = buffer4.substring(1);
                    getChooseData(values11, values22, values33, values44, 1);
                    // getServiceData(values11,values22,values33,values44);
                }

            }
        });
        gd_list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //可点击状态下才显示
                if (!ses.get(1).getList().get(i).getDisabled()) {
                    myDialog.show();
                }
                if (!ses.get(1).getList().get(i).getSelected() && !ses.get(1).getList().get(i).getDisabled()) {
                    //刷新适配器，显示筛选结果
                    buffer2 += "," + ses.get(1).getList().get(i).getValue();
                    String values11 = "";
                    String values22 = buffer2.substring(1);
                    String values33 = "";
                    String values44 = "";
                    if (buffer1.length() > 0)
                        values11 = buffer1.substring(1);
                    if (buffer3.length() > 0)
                        values33 = buffer3.substring(1);
                    if (buffer4.length() > 0)
                        values44 = buffer4.substring(1);
                    getChooseData(values11, values22, values33, values44, 1);
                    //getServiceData(values11,values22,values33,values44);
                } else if (ses.get(1).getList().get(i).getSelected() && !ses.get(1).getList().get(i).getDisabled()) {
                    //刷新适配器，显示筛选结果
                    buffer2 = buffer2.replace("," + ses.get(1).getList().get(i).getValue(), "");
                    String values11 = "";
                    String values22 = "";
                    String values33 = "";
                    String values44 = "";
                    if (buffer2.lastIndexOf(",") != -1) {
                        values22 = buffer2.substring(1);
                    }
                    if (buffer1.length() > 0)
                        values11 = buffer1.substring(1);
                    if (buffer3.length() > 0)
                        values33 = buffer3.substring(1);
                    if (buffer4.length() > 0)
                        values44 = buffer4.substring(1);
                    getChooseData(values11, values22, values33, values44, 1);
                    //getServiceData(values11,values22,values33,values44);
                }
            }
        });
        gd_list3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //可点击状态下才显示
                if (!ses.get(2).getList().get(i).getDisabled()) {
                    myDialog.show();
                }
                if (!ses.get(2).getList().get(i).getSelected() && !ses.get(2).getList().get(i).getDisabled()) {
                    //刷新适配器，显示筛选结果
                    buffer3 += "," + ses.get(2).getList().get(i).getValue();
                    String values11 = "";
                    String values22 = "";
                    String values33 = buffer3.substring(1);
                    String values44 = "";
                    if (buffer1.length() > 0)
                        values11 = buffer1.substring(1);
                    if (buffer2.length() > 0)
                        values22 = buffer2.substring(1);
                    if (buffer4.length() > 0)
                        values44 = buffer4.substring(1);
                    getChooseData(values11, values22, values33, values44, 1);
                    //getServiceData(values11,values22,values33,values44);
                } else if (ses.get(2).getList().get(i).getSelected() && !ses.get(2).getList().get(i).getDisabled()) {
                    //刷新适配器，显示筛选结果
                    buffer3 = buffer3.replace("," + ses.get(2).getList().get(i).getValue(), "");
                    String values11 = "";
                    String values22 = "";
                    String values33 = "";
                    String values44 = "";
                    if (buffer3.lastIndexOf(",") != -1) {
                        values33 = buffer3.substring(1);
                    }
                    if (buffer1.length() > 0)
                        values11 = buffer1.substring(1);
                    if (buffer2.length() > 0)
                        values22 = buffer2.substring(1);
                    if (buffer4.length() > 0)
                        values44 = buffer4.substring(1);
                    getChooseData(values11, values22, values33, values44, 1);
                    //getServiceData(values11,values22,values33,values44);
                }
            }
        });
        gd_list4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //可点击状态下才显示
                if (!ses.get(3).getList().get(i).getDisabled()) {
                    myDialog.show();
                }
                if (!ses.get(3).getList().get(i).getSelected() && !ses.get(3).getList().get(i).getDisabled()) {
                    //刷新适配器，显示筛选结果
                    buffer4 += "," + ses.get(3).getList().get(i).getValue();
                    String values11 = "";
                    String values22 = "";
                    String values33 = "";
                    String values44 = buffer4.substring(1);
                    if (buffer1.length() > 0)
                        values11 = buffer1.substring(1);
                    if (buffer2.length() > 0)
                        values22 = buffer2.substring(1);
                    if (buffer3.length() > 0)
                        values33 = buffer3.substring(1);
                    getChooseData(values11, values22, values33, values44, 1);
                    //getServiceData(values11,values22,values33,values44);
                } else if (ses.get(3).getList().get(i).getSelected() && !ses.get(3).getList().get(i).getDisabled()) {
                    //刷新适配器，显示筛选结果
                    buffer4 = buffer4.replace("," + ses.get(3).getList().get(i).getValue(), "");
                    String values11 = "";
                    String values22 = "";
                    String values33 = "";
                    String values44 = "";
                    if (buffer4.lastIndexOf(",") != -1) {
                        values44 = buffer4.substring(1);
                    }
                    if (buffer1.length() > 0)
                        values11 = buffer1.substring(1);
                    if (buffer2.length() > 0)
                        values22 = buffer2.substring(1);
                    if (buffer3.length() > 0)
                        values33 = buffer3.substring(1);
                    getChooseData(values11, values22, values33, values44, 1);
                    //getServiceData(values11,values22,values33,values44);
                }
            }
        });
        Button reset = (Button) popupWindowView.findViewById(R.id.reset);
        Button certain = (Button) popupWindowView.findViewById(R.id.certain);
        //判断是否显示展开
        if (ses.get(1).getList().size() > 6) {
            tv_open_close.setVisibility(View.VISIBLE);
        }
        tv_open_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen == 0) {
                    //点击展开
                    tv_open_close.setText("闭合");
                    gd_list2.setAdapter(adapter2 = new MyGridViewAdapter(getActivity(), ses.get(1).getList()));
                    isOpen = 1;
                } else if (isOpen == 1) {
                    //闭合
                    tv_open_close.setText("展开");
                    gd_list2.setAdapter(adapter2 = new MyGridViewAdapter(getActivity(), seTwoList));
                    isOpen = 0;
                }
            }
        });
        //重置
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //带空参数重新请求服务器
                myDialog.show();
                getChooseData("", "", "", "", 1);
                buffer1 = "";
                buffer2 = "";
                buffer3 = "";
                buffer4 = "";
            }
        });
        //确定
        certain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                //一次性筛选
                String values11 = "";
                String values22 = "";
                String values33 = "";
                String values44 = "";
                if (buffer1.lastIndexOf(",") != -1) {
                    values11 = buffer1.substring(1);
                }
                if (buffer2.lastIndexOf(",") != -1) {
                    values22 = buffer2.substring(1);
                }
                if (buffer3.lastIndexOf(",") != -1) {
                    values33 = buffer3.substring(1);
                }
                if (buffer4.lastIndexOf(",") != -1) {
                    values44 = buffer4.substring(1);
                }
                isLoadingMore = false;
                getServiceData(values11, values22, values33, values44, 1);
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
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    class popupDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //搜索——>更新数据
    public void refreshData() {
        Bundle bundle = getArguments();
        String name = bundle.getString("name");
        //List<HelperCustomer> listNew=new ArrayList<>();
       /* for (int i=0;i<list1.size();i++){
            if (list1.get(i).getFullname().equals(name)){
                listNew.add(list1.get(i));
            }
        }
        list1.clear();
        list1.addAll(listNew);
        myAdapter.notifyDataSetChanged();*/
    }
}
