package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.adapter.DayTabListAdapter;
import com.maibo.lvyongsheng.xianhui.entity.BTabList;
import com.maibo.lvyongsheng.xianhui.entity.Card;
import com.maibo.lvyongsheng.xianhui.entity.LTabList;
import com.maibo.lvyongsheng.xianhui.entity.Radar;
import com.maibo.lvyongsheng.xianhui.entity.TabMax;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.utils.ACache;
import com.maibo.lvyongsheng.xianhui.utils.NetWorkUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.Call;

public class DayTabActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG="DayTabActivity";
    private RadarChart mChart;
    private Typeface mTfLight;
    private int color_id;
    SharedPreferences sp;
    TextView backs;
    String apiURL;
    String token;
    List<List<BTabList>> bTab;
    //传递给柱状图的数据
    List<List<Radar>> zhuList1;
    ListView lv_detail;
    int[] monthAvg;
    int[] monthTal;
    int notice_id;
    List<BTabList> bt;
    //传递给饼状图的数据
    List<List<List<List<Card>>>> pie_data;
    String chartData;
    TextView btn_product, btn_caozuo, btn_keliu, btn_yuangong, btn_money, tv_club_name, tv_time, tv_setting;

    String org_id;
    String org_name;
    List<TabMax> list1;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.in_no_datas)
    LinearLayout in_no_datas;
    @Bind(R.id.in_loading_error)
    LinearLayout in_loading_error;
    @Bind(R.id.ll_all_data)
    LinearLayout ll_all_data;
    ACache aCache;//缓存数据

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    // mData=(List<List<Radar>>)msg.obj;
                    //首先判断点击的条目
                    final Intent intent = getIntent();
                    //待用数据notice_id
                    //int position = intent.getIntExtra("position",-1);
                    //在雷达图中展示数据
                    //List<Radar> radars= mData.get(position);
                    Map<String, List<Radar>> radars = (Map<String, List<Radar>>) msg.obj;
                    setData(radars);
                    break;
                case 1:
                    bt = (List<BTabList>) msg.obj;
                    //在ListView中展示数据
                    DayTabListAdapter adapter = new DayTabListAdapter(DayTabActivity.this, bt, viewHeight);
                    lv_detail.setAdapter(adapter);
                    lv_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //分别跳转到下部列表的详细介绍页
                            //判断当前值是否为空，不为空才跳转
                            if (!bt.get(i).getAmount().equals("0")) {
                                Intent intent3 = new Intent(DayTabActivity.this, ListDetailActivity.class);
                                intent3.putExtra("data", (Serializable) bt.get(i));
                                intent3.putExtra("position", i);
                                startActivity(intent3);
                            }
                        }
                    });

                    break;
                case 2:
                    //柱状图
                    zhuList1 = (List<List<Radar>>) msg.obj;
                    break;
                case 3:
                    //饼状图
                    pie_data = (List<List<List<List<Card>>>>) msg.obj;
                    break;
                case 4:
                    monthAvg = (int[]) msg.obj;
                    break;
                case 5:
                    monthTal = (int[]) msg.obj;
                    break;
                case 6:
                    list1 = (List<TabMax>) msg.obj;
                    Intent intent6 = new Intent(DayTabActivity.this, MaxSettingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("maxList", (Serializable) list1);
                    bundle.putString("org_name", org_name);
                    bundle.putString("org_id", org_id);
                    intent6.putExtras(bundle);
//                startActivity(intent);
                    startActivityForResult(intent6, 15);
                    break;
                case 7:
                    in_loading_error.setVisibility(View.VISIBLE);
                    ll_all_data.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_table_new2);
        //adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);

        initView();
        aCache = ACache.get(this);
        String mResponse = aCache.getAsString(notice_id + "");
        if (TextUtils.isEmpty(mResponse)) {
            showLongDialog();
            getServiceData();
        } else {
            JsonObject object = new JsonParser().parse(mResponse).getAsJsonObject();
            analisysJsonDatas(object);
        }


    }

    public void initView() {
        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);

        Intent intent3 = getIntent();
        notice_id = intent3.getIntExtra("notice_id", -1);
        org_name = intent3.getStringExtra("org_name");
        org_id = intent3.getStringExtra("org_id");
        String create_time = intent3.getStringExtra("create_time");
        color_id=intent3.getIntExtra("color_id",-1);

        lv_detail = (ListView) findViewById(R.id.lv_detail);
        btn_product = (TextView) findViewById(R.id.btn_product);
        btn_caozuo = (TextView) findViewById(R.id.btn_caozuo);
        btn_keliu = (TextView) findViewById(R.id.btn_keliu);
        btn_yuangong = (TextView) findViewById(R.id.btn_yuangong);
        btn_money = (TextView) findViewById(R.id.btn_money);
        backs = (TextView) findViewById(R.id.back);
        tv_club_name = (TextView) findViewById(R.id.tv_club_name);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_setting = (TextView) findViewById(R.id.tv_setting);

        if (!TextUtils.isEmpty(org_name))
            tv_club_name.setText(org_name);
        if (!TextUtils.isEmpty(create_time)) {
            tv_time.setText(create_time.substring(5, 10));
        }

        backs.setOnClickListener(this);
        btn_product.setOnClickListener(this);
        btn_caozuo.setOnClickListener(this);
        btn_keliu.setOnClickListener(this);
        btn_yuangong.setOnClickListener(this);
        btn_money.setOnClickListener(this);
        tv_setting.setOnClickListener(this);

        mChart = (RadarChart) findViewById(R.id.chart1);
        setRadarChartColor();
        initRanderChartStyle();


    }

    private void setRadarChartColor() {
        switch (color_id){
            case 0://星期日
                mChart.setBackgroundResource(R.color.zhushou_bg_7);
                ll_head.setBackgroundResource(R.color.zhushou_bg2_7);
                break;
            case 1://星期一
                mChart.setBackgroundResource(R.color.zhushou_bg_1);
                ll_head.setBackgroundResource(R.color.zhushou_bg2_1);
                break;
            case 2://星期二
                mChart.setBackgroundResource(R.color.zhushou_bg_2);
                ll_head.setBackgroundResource(R.color.zhushou_bg2_2);
                break;
            case 3://星期三
                mChart.setBackgroundResource(R.color.zhushou_bg_3);
                ll_head.setBackgroundResource(R.color.zhushou_bg2_3);
                break;
            case 4://星期四
                mChart.setBackgroundResource(R.color.zhushou_bg_4);
                ll_head.setBackgroundResource(R.color.zhushou_bg2_4);
                break;
            case 5://星期五
                mChart.setBackgroundResource(R.color.zhushou_bg_5);
                ll_head.setBackgroundResource(R.color.zhushou_bg2_5);
                break;
            case 6://星期六
                mChart.setBackgroundResource(R.color.zhushou_bg_6);
                ll_head.setBackgroundResource(R.color.zhushou_bg2_6);
                break;
            default:break;
        }

    }


    /**
     * 初始化雷达图样式
     */
    private void initRanderChartStyle() {
        Description description = new Description();
        description.setText("");
        mChart.setDescription(description);

        mChart.setWebLineWidth(0f);
        mChart.setWebColor(Color.WHITE);
        mChart.setWebLineWidthInner(0f);
        mChart.setWebColorInner(Color.WHITE);
        //这句话会决定看不看得到线
        mChart.setWebAlpha(0);
        mChart.setRotationEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(16f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            private String[] mActivities = new String[]{"现金", "实操", "产品", "客流", "工时"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });

        xAxis.setTextColor(Color.rgb(255, 255, 255));


        YAxis yAxis = mChart.getYAxis();
        yAxis.setTypeface(mTfLight);
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(8f);
        yAxis.setDrawLabels(false);
        //设置图例相关参数
        Legend l = mChart.getLegend();
        l.setEnabled(false);
    }


    public void setColorInthis(RadarDataSet set){
        if(set!=null){
            switch (color_id){
                case 0://星期日
                    set.setColor(Color.rgb(130, 49, 129));
                    set.setFillColor(Color.rgb(130, 49, 129));
                    break;
                case 1://星期一
                    set.setColor(Color.rgb(195, 108, 102));
                    set.setFillColor(Color.rgb(195, 108, 102));
                    break;
                case 2://星期二204,133,92,1
                    set.setColor(Color.rgb(204, 133, 92));
                    set.setFillColor(Color.rgb(204, 133, 92));
                    break;
                case 3://星期三204,159,49,1
                    set.setColor(Color.rgb(204, 159, 49));
                    set.setFillColor(Color.rgb(204, 159, 49));
                    break;
                case 4://星期四33,185,129,1
                    set.setColor(Color.rgb(33, 185, 129));
                    set.setFillColor(Color.rgb(33, 185, 129));
                    break;
                case 5://星期五28,170,170,1
                    set.setColor(Color.rgb(28, 170, 170));
                    set.setFillColor(Color.rgb(28, 170, 170));
                    break;
                case 6://星期六23,106,157,1
                    set.setColor(Color.rgb(23, 106, 157));
                    set.setFillColor(Color.rgb(23, 106, 157));
                    break;
                default:break;
            }
        }
    }
    /**
     * 初始化雷达图样式
     */
    public void setData(Map<String, List<Radar>> ra) {

        ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();
        ArrayList<RadarEntry> entries2 = new ArrayList<RadarEntry>();
        ArrayList<RadarEntry> entries3 = new ArrayList<RadarEntry>();
      /*  List<Radar> ra1 = ra.get("7日均值");

        TextView[] btn_text = {btn_money, btn_caozuo, btn_product, btn_keliu, btn_yuangong};
        for (int i = 0; i < ra1.size(); i++) {
            if (ra1.get(i).getScore() * 2 > 10) {
                entries1.add(new RadarEntry(10));
            } else {
                entries1.add(new RadarEntry(ra1.get(i).getScore() * 2));
            }

        }*/

        List<Radar> ra2 = ra.get("今日");
        for (int i = 0; i < ra2.size(); i++) {
            if (ra2.get(i).getScore() * 2 > 10) {
                entries2.add(new RadarEntry(10));
            } else {
                entries2.add(new RadarEntry(ra2.get(i).getScore() * 2));
            }
        }
        for (int i = 0; i < 5; i++) {
            entries3.add(new RadarEntry(10));
        }

       /* RadarDataSet set1 = new RadarDataSet(entries1, "7日均值");
        set1.setColor(Color.rgb(218, 182, 133));
        set1.setFillColor(Color.rgb(218, 182, 133));
        set1.setDrawFilled(true);
        set1.setFillAlpha(100);
        set1.setLineWidth(0f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);*/

        RadarDataSet set2 = new RadarDataSet(entries2, "今日");
        setColorInthis(set2);
        set2.setDrawFilled(true);
        set2.setFillAlpha(255);
        set2.setLineWidth(1f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);

        RadarDataSet set3 = new RadarDataSet(entries3, "");
        set3.setColor(Color.argb(80,255,255,255));
        set3.setFillColor(Color.argb(80,255,255,255));
        set3.setDrawFilled(true);
        set3.setFillAlpha(80);
        set3.setLineWidth(0f);
        set3.setDrawHighlightCircleEnabled(true);
        set3.setDrawHighlightIndicators(false);


        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set2);
        //sets.add(set1);
        sets.add(set3);

        RadarData data = new RadarData(sets);
        data.setValueTypeface(mTfLight);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);
        mChart.invalidate();
    }


    /**
     * 获取服务器数据
     */
    public void getServiceData() {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getdailyreportdata")
                .addParams("token", token)
                .addParams("notice_id", notice_id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Message msg = Message.obtain();
                        msg.what = 7;
                        handler.sendMessage(msg);
                        dismissLongDialog();
                        dismissShortDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        String msg_status = object.get("status").getAsString();
                        String message = object.get("message").getAsString();
                        if (msg_status.equals("ok")) {
                            analisysJsonDatas(object);
                            aCache.put(notice_id + "", response, 10);
                        } else {
                            App.showToast(getApplicationContext(), message);
                        }
                        dismissLongDialog();
                        dismissShortDialog();
                    }
                });
    }

    /**
     * 解析Json
     *
     * @param object
     */
    private void analisysJsonDatas(JsonObject object) {
        JsonObject data = object.get("data").getAsJsonObject();
        JsonArray weekly = data.get("weekly_daily").getAsJsonArray();
        //各项每月平均数据
        int[] monthlyAvg = new int[5];
        JsonObject monthly_avg = data.get("monthly_avg").getAsJsonObject();
        int cash1 = 0;
        int project1 = 0;
        int product1 = 0;
        int customer1 = 0;
        int employee1 = 0;
        if (!monthly_avg.get("cash").isJsonNull()) {
            cash1 = monthly_avg.get("cash").getAsInt();
            monthlyAvg[0] = cash1;
        }
        if (!monthly_avg.get("project").isJsonNull()) {
            project1 = monthly_avg.get("project").getAsInt();
            monthlyAvg[1] = project1;
        }
        if (!monthly_avg.get("product").isJsonNull()) {
            product1 = monthly_avg.get("product").getAsInt();
            monthlyAvg[2] = product1;
        }
        if (!monthly_avg.get("customer").isJsonNull()) {
            customer1 = monthly_avg.get("customer").getAsInt();
            monthlyAvg[3] = customer1;
        }
        if (!monthly_avg.get("employee").isJsonNull()) {
            employee1 = monthly_avg.get("employee").getAsInt();
            monthlyAvg[4] = employee1;
        }
        //各项每月总数据
        int[] monthlyTotal = new int[5];
        JsonObject monthly_total = data.get("monthly_total").getAsJsonObject();
        int cash2 = monthly_total.get("cash").getAsInt();
        monthlyTotal[0] = cash2;
        int project2 = monthly_total.get("project").getAsInt();
        monthlyTotal[1] = project2;
        int product2 = monthly_total.get("product").getAsInt();
        monthlyTotal[2] = product2;
        int customer2 = monthly_total.get("customer").getAsInt();
        monthlyTotal[3] = customer2;
        int employee2 = monthly_total.get("employee").getAsInt();
        monthlyTotal[4] = employee2;
        //雷达图数据,只需解析当天即可
        JsonObject radarData = weekly.get(6).getAsJsonObject();
        Map<String, List<Radar>> radarMap = new HashMap<String, List<Radar>>();
        JsonArray radar_list = radarData.get("radar_list").getAsJsonArray();
        for (JsonElement je : radar_list) {
            List<Radar> listR = new ArrayList<Radar>();
            JsonObject jo = je.getAsJsonObject();
            String key = "";
            if (!jo.get("key").isJsonNull())
                key = jo.get("key").getAsString();
            JsonArray value = jo.get("value").getAsJsonArray();
            for (JsonElement je2 : value) {
                JsonObject jb = je2.getAsJsonObject();
                String name = "";
                int amount = 0;
                int score = 0;
                if (!jb.get("name").isJsonNull())
                    name = jb.get("name").getAsString();
                if (!jb.get("amount").isJsonNull())
                    amount = jb.get("amount").getAsInt();
                if (!jb.get("score").isJsonNull())
                    score = jb.get("score").getAsInt();
                listR.add(new Radar(name, amount, score));
            }
            radarMap.put(key, listR);
        }
        //传递数据:雷达图数据
        Message msg = Message.obtain();
        msg.what = 0;
        msg.obj = radarMap;
        handler.sendMessage(msg);

        //解析下方列表数据
        List<BTabList> btabList = new ArrayList<BTabList>();
        JsonObject json_home = weekly.get(6).getAsJsonObject();
        JsonArray home_list = json_home.get("home_list").getAsJsonArray();
        for (JsonElement je : home_list) {
            JsonObject jo = je.getAsJsonObject();
            String name = "";
            String amount = "";
            if (!jo.get("name").isJsonNull())
                name = jo.get("name").getAsString();
            if (!jo.get("amount").isJsonNull())
                amount = jo.get("amount").getAsString();
            JsonArray list = jo.get("list").getAsJsonArray();
            List<LTabList> lb = new ArrayList<LTabList>();
            for (JsonElement je1 : list) {
                JsonObject js = je1.getAsJsonObject();
                String fullname = "";
                String amount1 = "";
                if (!js.get("fullname").isJsonNull())
                    fullname = js.get("fullname").getAsString();
                if (!js.get("amount").isJsonNull())
                    amount1 = js.get("amount").getAsString();
                lb.add(new LTabList(fullname, amount1));
            }
            btabList.add(new BTabList(name, lb, amount));
        }
        //传递数据:下方列表数据
        Message msg1 = Message.obtain();
        msg1.what = 1;
        msg1.obj = btabList;
        handler.sendMessage(msg1);

        //解析柱状图数据
        //date，name，amount
        List<List<Radar>> zhu_list = new ArrayList<List<Radar>>();
        for (JsonElement je : weekly) {
            JsonObject jo = je.getAsJsonObject();
            String date = "";
            if (!jo.get("date").isJsonNull())
                date = jo.get("date").getAsString();
            JsonArray ja = jo.get("radar_list").getAsJsonArray();
            JsonObject second = ja.get(1).getAsJsonObject();
            JsonArray value = second.get("value").getAsJsonArray();
            List<Radar> radars = new ArrayList<Radar>();
            for (JsonElement je1 : value) {
                JsonObject jo1 = je1.getAsJsonObject();
                String name = "";
                int amount = 0;
                int scro = 0;
                if (!jo1.get("name").isJsonNull())
                    name = jo1.get("name").getAsString();
                if (!jo1.get("amount").isJsonNull())
                    amount = jo1.get("amount").getAsInt();
                if (!jo1.get("score").isJsonNull())
                    scro = jo1.get("score").getAsInt();
                radars.add(new Radar(name, amount, scro, date));
            }
            zhu_list.add(radars);
        }
        //传递数据：柱状图
        Message msg2 = Message.obtain();
        msg2.what = 2;
        msg2.obj = zhu_list;
        handler.sendMessage(msg2);

        //解析饼状图数据
        List<List<List<List<Card>>>> c4 = new ArrayList<List<List<List<Card>>>>();
        for (JsonElement je : weekly) {
            JsonObject jo = je.getAsJsonObject();
            JsonArray js_pie = jo.get("chart_list").getAsJsonArray();
            List<List<List<Card>>> c3 = new ArrayList<List<List<Card>>>();
            for (JsonElement je1 : js_pie) {
                JsonObject jo1 = je1.getAsJsonObject();
                JsonArray value = jo1.get("value").getAsJsonArray();
                List<List<Card>> c2 = new ArrayList<List<Card>>();
                for (JsonElement je2 : value) {
                    JsonObject jo2 = je2.getAsJsonObject();
                    JsonArray value1 = jo2.get("value").getAsJsonArray();
                    List<Card> c1 = new ArrayList<Card>();
                    for (JsonElement je3 : value1) {
                        String name = "";
                        int amount = 0;
                        JsonObject jo3 = je3.getAsJsonObject();
                        if (!jo3.get("name").isJsonNull()) {
                            name = jo3.get("name").getAsString();
                        }
                        if (!jo3.get("amount").isJsonNull()) {
                            amount = jo3.get("amount").getAsInt();
                        }
                        c1.add(new Card(name, amount));
                    }
                    c2.add(c1);
                }
                c3.add(c2);
            }
            c4.add(c3);
        }
        //传递数据:饼状图数据
        Message msg3 = Message.obtain();
        msg3.what = 3;
        msg3.obj = c4;
        handler.sendMessage(msg3);
        //传递数据:monthly_avg
        Message msg4 = Message.obtain();
        msg4.what = 4;
        msg4.obj = monthlyAvg;
        handler.sendMessage(msg4);
        //传递数据:monthly_total
        Message msg5 = Message.obtain();
        msg5.what = 5;
        msg5.obj = monthlyTotal;
        handler.sendMessage(msg5);
    }

    /**
     * @deprecated 返回内部列表值
     */
    public List<LTabList> getListItem(String str, JsonObject jsonObject) {
        List<LTabList> listT = new ArrayList<>();
        JsonArray project_list = jsonObject.get(str).getAsJsonArray();
        for (JsonElement je1 : project_list) {
            JsonObject jo1 = je1.getAsJsonObject();
            String amount = jo1.get("amount").getAsString();
            String fullname = jo1.get("fullname").getAsString();
            listT.add(new LTabList(amount, fullname));
        }
        return listT;
    }

    //分别跳转到现金、实操、产品、客流、员工
    public void startActivitys(int position) {
        Intent intent = new Intent(this, ItemDetailActivity.class);
        Bundle bundle = new Bundle();
        //柱状图数据
        bundle.putSerializable("zhuzhuangtu", (Serializable) zhuList1);
        //饼状图数据
        bundle.putSerializable("pie", (Serializable) pie_data);
        //七日平均值
        intent.putExtra("monthAvg", monthAvg);
        //七日当前总量
        intent.putExtra("monthTal", monthTal);
        //点击位置
        intent.putExtra("position", position);
        //会所名称
        intent.putExtra("org_name", org_name);
        //会所ID
        intent.putExtra("org_id", org_id);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_money:
                //点击第一个位置时
                startActivitys(0);
                break;
            case R.id.btn_caozuo:
                startActivitys(1);
                break;
            case R.id.btn_product:
                startActivitys(2);
                break;
            case R.id.btn_keliu:
                startActivitys(3);
                break;
            case R.id.btn_yuangong:
                startActivitys(4);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.tv_setting:
                //判断网络是否可用
                boolean isNetWork = NetWorkUtils.isNetworkConnected(this);
                if (isNetWork)
                    //跳转到最大值设置界面
                    getServiceMaxData();
                else showToast("网络不可用");

                break;
        }

    }

    //获取最大值相关参数
    //获取日报表峰值设置
    public void getServiceMaxData() {
        Log.e(TAG,"数据     "+org_id);
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getdailyreportsetting")
                .addParams("token", token)
                .addParams("org_id",org_id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG,"设置的返回值"+response);
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        JsonObject data = jsonObject.get("data").getAsJsonObject();
                        List<TabMax> list = new ArrayList<>();

                        JsonObject cash_amount = data.get("cash_amount").getAsJsonObject();
                        list.add(getAllData(cash_amount));

                        JsonObject project_amount = data.get("project_amount").getAsJsonObject();
                        list.add(getAllData(project_amount));

                        JsonObject product_amount = data.get("product_amount").getAsJsonObject();
                        list.add(getAllData(product_amount));

                        JsonObject room_turnover = data.get("room_turnover").getAsJsonObject();
                        list.add(getAllData(room_turnover));

                        JsonObject employee_hours = data.get("employee_hours").getAsJsonObject();
                        list.add(getAllData(employee_hours));

                        Message msg = Message.obtain();
                        msg.what = 6;
                        msg.obj = list;
                        handler.sendMessage(msg);

                    }
                });
    }

    public TabMax getAllData(JsonObject what) {
        String name = what.get("name").getAsString();
        int min = what.get("min").getAsInt();
        int max = what.get("max").getAsInt();
        float step = what.get("step").getAsFloat();
        String unit = what.get("unit").getAsString();
        JsonObject def = what.get("default").getAsJsonObject();
        float[] defaults = new float[3];
        defaults[0] = def.get("A").getAsFloat();
        defaults[1] = def.get("B").getAsFloat();
        defaults[2] = def.get("C").getAsFloat();
        float value = what.get("value").getAsFloat();

        return new TabMax(name, min, max, step, unit, defaults, value);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 16) {
            initRanderChartStyle();
            getServiceData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }

    /**
     * 网络问题，重新加载
     *
     * @param view
     */
    public void loadingMore(View view) {
        showShortDialog();
        in_loading_error.setVisibility(View.GONE);
        ll_all_data.setVisibility(View.VISIBLE);
        getServiceData();
    }
}
