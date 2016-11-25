package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
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
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class DayTabActivity extends Activity implements View.OnClickListener{

    private RadarChart mChart;
    private Typeface mTfLight;
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
    TextView btn_product,btn_caozuo,btn_keliu,btn_yuangong,btn_money,tv_club_name,tv_time;
    ProgressDialog dialog;

    String org_id;
    String org_name;


    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                   // mData=(List<List<Radar>>)msg.obj;
                    //首先判断点击的条目
                    final Intent intent=getIntent();
                    //待用数据notice_id
                    //int position = intent.getIntExtra("position",-1);
                    //在雷达图中展示数据
                    //List<Radar> radars= mData.get(position);
                    Map<String,List<Radar>> radars=(Map<String, List<Radar>>) msg.obj;
                    setData(radars);
                    break;
                case 1:
                    bt=(List<BTabList>) msg.obj;
                    //在ListView中展示数据
                    DayTabListAdapter adapter  = new DayTabListAdapter(DayTabActivity.this,bt);
                    lv_detail.setAdapter(adapter);
                    lv_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //分别跳转到下部列表的详细介绍页
                            //判断当前值是否为空，不为空才跳转
                            if (!bt.get(i).getAmount().equals("0")){
                            Intent intent3=new Intent(DayTabActivity.this, ListDetailActivity.class);
                            intent3.putExtra("data",(Serializable) bt.get(i));
                            intent3.putExtra("position",i);
                            startActivity(intent3);
                            }
                        }
                    });

                    break;
                case 2:
                   //柱状图
                   zhuList1=( List<List<Radar>>)msg.obj;
                    break;
                case 3:
                    //饼状图
                    pie_data=( List<List<List<List<Card>>>>)msg.obj;
                    break;
                case 4:
                    monthAvg=(int[])msg.obj;
                    break;
                case 5:
                    monthTal=(int[])msg.obj;
                    break;
            }
        }};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_table);
        dialog=new ProgressDialog(this);
        dialog.setMessage("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
        dialog.show();
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        initView();
        getServiceData();

    }

    public void  initView(){
        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);

        Intent intent3=getIntent();
        notice_id = intent3.getIntExtra("notice_id",-1);
        org_name=intent3.getStringExtra("org_name");
        org_id=intent3.getStringExtra("org_id");
        String create_time=intent3.getStringExtra("create_time");

        lv_detail = (ListView)findViewById(R.id.lv_detail);

        btn_product = (TextView)findViewById(R.id.btn_product);
        btn_caozuo = (TextView)findViewById(R.id.btn_caozuo);
        btn_keliu = (TextView)findViewById(R.id.btn_keliu);
        btn_yuangong = (TextView)findViewById(R.id.btn_yuangong);
        btn_money = (TextView)findViewById(R.id.btn_money);
        backs= (TextView) findViewById(R.id.back);
        tv_club_name= (TextView) findViewById(R.id.tv_club_name);
        tv_time= (TextView) findViewById(R.id.tv_time);
        backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

        if (!TextUtils.isEmpty(org_name))
            tv_club_name.setText(org_name);
        if (!TextUtils.isEmpty(create_time)){
            tv_time.setText(create_time.substring(5,10));
        }


        btn_product.setOnClickListener(this);
        btn_caozuo.setOnClickListener(this);
        btn_keliu.setOnClickListener(this);
        btn_yuangong.setOnClickListener(this);
        btn_money.setOnClickListener(this);

        mTfLight=Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        mChart = (RadarChart) findViewById(R.id.chart1);
        mChart.setBackgroundColor(Color.rgb(231,222,205));
        //设置对图表的描述
        mChart.setDescription("");

        mChart.setWebLineWidth(1f);//绘制线条宽度（向外辐射的线条）
        mChart.setWebColor(Color.LTGRAY);
        mChart.setWebLineWidthInner(1f);//内部线条宽度（外面的环状线条）
        mChart.setWebColorInner(Color.LTGRAY);
        mChart.setWebAlpha(0);//所有线条webline的透明度
        mChart.setRotationEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);// X坐标值字体样式
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new AxisValueFormatter() {
            private String[] mActivities = new String[]{"", "", "", "", ""};
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        xAxis.setTextColor(Color.WHITE);

        YAxis yAxis = mChart.getYAxis();
        yAxis.setTypeface(mTfLight);
        yAxis.setLabelCount(4, false);// Y坐标值标签个数
        yAxis.setTextSize(9f);
        yAxis.setDrawLabels(false);//是否显示y值在图表上

        //设置图例相关参数
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);//图例位置
        l.setTypeface(mTfLight);
        l.setXEntrySpace(7f);//图例X间距
        l.setYEntrySpace(5f);
        l.setXOffset(60f);
        l.setYOffset(10f);
        int[] color=new int[2];
        color[0]=Color.rgb(211,183,139);
        color[1]=Color.rgb(114,76,54);
        String[] des=new String[2];
        des[0]="七日均值";
        des[1]="今日";
        l.setCustom(color,des);
        l.setTextColor(Color.BLACK);
    }

    public void setData(Map<String,List<Radar>> ra) {

        ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();
        ArrayList<RadarEntry> entries2 = new ArrayList<RadarEntry>();
        ArrayList<RadarEntry> entries3 = new ArrayList<RadarEntry>();
        List<Radar> ra1=ra.get("7日均值");
        entries1.add(new RadarEntry(ra1.get(0).getScore()));
        btn_money.setText(ra1.get(0).getName());
        entries1.add(new RadarEntry(ra1.get(1).getScore()));
        btn_caozuo.setText(ra1.get(1).getName());
        entries1.add(new RadarEntry(ra1.get(2).getScore()));
        btn_product.setText(ra1.get(2).getName());
        entries1.add(new RadarEntry(ra1.get(3).getScore()));
        btn_keliu.setText(ra1.get(3).getName());
        entries1.add(new RadarEntry(ra1.get(4).getScore()));
        btn_yuangong.setText(ra1.get(4).getName());



        List<Radar> ra2=ra.get("今日");
        entries2.add(new RadarEntry(ra2.get(0).getScore()));
        btn_money.setText(ra2.get(0).getName());
        entries2.add(new RadarEntry(ra2.get(1).getScore()));
        btn_caozuo.setText(ra2.get(1).getName());
        entries2.add(new RadarEntry(ra2.get(2).getScore()));
        btn_product.setText(ra2.get(2).getName());
        entries2.add(new RadarEntry(ra2.get(3).getScore()));
        btn_keliu.setText(ra2.get(3).getName());
        entries2.add(new RadarEntry(ra2.get(4).getScore()));
        btn_yuangong.setText(ra2.get(4).getName());

        entries3.add(new RadarEntry(5));
        entries3.add(new RadarEntry(5));
        entries3.add(new RadarEntry(5));
        entries3.add(new RadarEntry(5));
        entries3.add(new RadarEntry(5));


        RadarDataSet set1 = new RadarDataSet(entries1, "7日均值");
        set1.setColor(Color.rgb(218,182,133));
        set1.setFillColor(Color.rgb(218,182,133));
        set1.setDrawFilled(true);
        set1.setFillAlpha(255);
        set1.setLineWidth(0f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        RadarDataSet set2 = new RadarDataSet(entries2, "今日");
        set2.setColor(Color.rgb(118,74,55));
        set2.setFillColor(Color.rgb(118,74,55));
        set2.setDrawFilled(true);
        set2.setFillAlpha(255);
        set2.setLineWidth(0f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);

        RadarDataSet set3 = new RadarDataSet(entries3, "");
        set3.setColor(Color.rgb(221,205,180));
        set3.setFillColor(Color.rgb(221,205,180));
        set3.setDrawFilled(true);
        set3.setFillAlpha(180);
        set3.setLineWidth(0f);
        set3.setDrawHighlightCircleEnabled(true);
        set3.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set2);
        sets.add(set1);
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
    public void getServiceData(){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/getdailyreportdata")
                .addParams("token",token)
                .addParams("notice_id", notice_id+"")//第二个参数待填
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
//                        Log.e("DayTabActivity:",response);
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        String msg_status=object.get("status").getAsString();
                        String message=object.get("message").getAsString();
                    if (msg_status.equals("ok")){
                        JsonObject data=object.get("data").getAsJsonObject();
                        JsonArray weekly=data.get("weekly_daily").getAsJsonArray();
                        //各项每月平均数据
                        int[] monthlyAvg=new int[5];
                        JsonObject monthly_avg=data.get("monthly_avg").getAsJsonObject();
                        int cash1=0;
                        int project1=0;
                        int product1=0;
                        int customer1=0;
                        int employee1=0;
                        if (!monthly_avg.get("cash").isJsonNull()){
                            cash1=monthly_avg.get("cash").getAsInt();
                            monthlyAvg[0]=cash1;
                        }
                        if (!monthly_avg.get("project").isJsonNull()){
                            project1=monthly_avg.get("project").getAsInt();
                            monthlyAvg[1]=project1;
                        }
                        if (!monthly_avg.get("product").isJsonNull()){
                            product1=monthly_avg.get("product").getAsInt();
                            monthlyAvg[2]=product1;
                        }
                        if (!monthly_avg.get("customer").isJsonNull()){
                            customer1=monthly_avg.get("customer").getAsInt();
                            monthlyAvg[3]=customer1;
                        }
                        if (!monthly_avg.get("employee").isJsonNull()){
                            employee1=monthly_avg.get("employee").getAsInt();
                            monthlyAvg[4]=employee1;
                        }
                        //各项每月总数据
                        int[] monthlyTotal=new int[5];
                        JsonObject monthly_total=data.get("monthly_total").getAsJsonObject();
                        int cash2=monthly_total.get("cash").getAsInt();
                        monthlyTotal[0]=cash2;
                        int project2=monthly_total.get("project").getAsInt();
                        monthlyTotal[1]=project2;
                        int product2=monthly_total.get("product").getAsInt();
                        monthlyTotal[2]=product2;
                        int customer2=monthly_total.get("customer").getAsInt();
                        monthlyTotal[3]=customer2;
                        int employee2=monthly_total.get("employee").getAsInt();
                        monthlyTotal[4]=employee2;
                        //雷达图数据,只需解析当天即可
                        JsonObject radarData=weekly.get(6).getAsJsonObject();
                        Map<String,List<Radar>> radarMap=new HashMap<String, List<Radar>>();
                        JsonArray radar_list=radarData.get("radar_list").getAsJsonArray();
                        for (JsonElement je:radar_list){
                            List<Radar> listR=new ArrayList<Radar>();
                            JsonObject jo=je.getAsJsonObject();
                            String key="";
                            if (!jo.get("key").isJsonNull())
                                key=jo.get("key").getAsString();
                            JsonArray value=jo.get("value").getAsJsonArray();
                            for (JsonElement je2:value){
                                JsonObject jb=je2.getAsJsonObject();
                                String name="";
                                int amount=0;
                                int score=0;
                                if (!jb.get("name").isJsonNull())
                                    name=jb.get("name").getAsString();
                                if (!jb.get("amount").isJsonNull())
                                    amount=jb.get("amount").getAsInt();
                                if (!jb.get("score").isJsonNull())
                                    score=jb.get("score").getAsInt();
                                listR.add(new Radar(name,amount,score));
                            }
                            radarMap.put(key,listR);
                        }
                        //传递数据:雷达图数据
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = radarMap;
                        handler.sendMessage(msg);

                        //解析下方列表数据
                        List<BTabList> btabList=new ArrayList<BTabList>();
                        JsonObject json_home=weekly.get(6).getAsJsonObject();
                        JsonArray home_list=json_home.get("home_list").getAsJsonArray();
                        for (JsonElement je:home_list){
                            JsonObject jo=je.getAsJsonObject();
                            String name="";
                            String amount="";
                            if (!jo.get("name").isJsonNull())
                                name=jo.get("name").getAsString();
                            if (!jo.get("amount").isJsonNull())
                                amount=jo.get("amount").getAsString();
                            JsonArray list=jo.get("list").getAsJsonArray();
                            List<LTabList> lb=new ArrayList<LTabList>();
                            for (JsonElement je1:list){
                                JsonObject js=je1.getAsJsonObject();
                                String fullname="";
                                String amount1="";
                                if (!js.get("fullname").isJsonNull())
                                    fullname=js.get("fullname").getAsString();
                                if (!js.get("amount").isJsonNull())
                                    amount1=js.get("amount").getAsString();
                                lb.add(new LTabList(fullname,amount1));
                            }
                            btabList.add(new BTabList(name,lb,amount));
                        }
                        //传递数据:下方列表数据
                        Message msg1 = Message.obtain();
                        msg1.what = 1;
                        msg1.obj = btabList;
                        handler.sendMessage(msg1);

                        //解析柱状图数据
                        //date，name，amount
                        List<List<Radar>> zhu_list=new ArrayList<List<Radar>>();
                        for(JsonElement je:weekly){
                            JsonObject jo=je.getAsJsonObject();
                            String date="";
                            if (!jo.get("date").isJsonNull())
                                date=jo.get("date").getAsString();
                            JsonArray ja=jo.get("radar_list").getAsJsonArray();
                            JsonObject second=ja.get(1).getAsJsonObject();
                            JsonArray value=second.get("value").getAsJsonArray();
                            List<Radar> radars=new ArrayList<Radar>();
                            for (JsonElement je1:value){
                                JsonObject jo1=je1.getAsJsonObject();
                                String name="";
                                int amount=0;
                                int scro=0;
                                if (!jo1.get("name").isJsonNull())
                                    name=jo1.get("name").getAsString();
                                if (!jo1.get("amount").isJsonNull())
                                    amount=jo1.get("amount").getAsInt();
                                if (!jo1.get("score").isJsonNull())
                                    scro=jo1.get("score").getAsInt();
                                radars.add(new Radar(name,amount,scro,date));
                            }
                            zhu_list.add(radars);
                        }
                        //传递数据：柱状图
                        Message msg2 = Message.obtain();
                        msg2.what = 2;
                        msg2.obj = zhu_list;
                        handler.sendMessage(msg2);

                        //解析饼状图数据
                        List<List<List<List<Card>>>> c4=new ArrayList<List<List<List<Card>>>>();
                        for (JsonElement je:weekly){
                            JsonObject jo=je.getAsJsonObject();
                            JsonArray js_pie=jo.get("chart_list").getAsJsonArray();
                            List<List<List<Card>>> c3=new ArrayList<List<List<Card>>>();
                            for (JsonElement je1:js_pie){
                                JsonObject jo1=je1.getAsJsonObject();
                                JsonArray value=jo1.get("value").getAsJsonArray();
                                List<List<Card>> c2=new ArrayList<List<Card>>();
                                for (JsonElement je2:value){
                                    JsonObject jo2=je2.getAsJsonObject();
                                    JsonArray value1=jo2.get("value").getAsJsonArray();
                                    List<Card> c1=new ArrayList<Card>();
                                    for (JsonElement je3:value1){
                                        String name="";
                                        int amount=0;
                                        JsonObject jo3=je3.getAsJsonObject();
                                        if (!jo3.get("name").isJsonNull()){
                                            name=jo3.get("name").getAsString();
                                        }
                                        if (!jo3.get("amount").isJsonNull()){
                                            amount=jo3.get("amount").getAsInt();
                                        }
                                        c1.add(new Card(name,amount));
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
                        dialog.dismiss();
                    }else{
                            App.showToast(getApplicationContext(),message);
                        }
                }
                });
    }

    /**
     * @deprecated 返回内部列表值
     */
    public List<LTabList> getListItem(String str,JsonObject jsonObject){
        List<LTabList> listT=new ArrayList<>();
        JsonArray project_list = jsonObject.get(str).getAsJsonArray();
        for(JsonElement je1:project_list){
            JsonObject jo1=je1.getAsJsonObject();
            String amount = jo1.get("amount").getAsString();
            String fullname = jo1.get("fullname").getAsString();
            listT.add(new LTabList(amount,fullname));
        }
        return listT;
    }
    //分别跳转到现金、实操、产品、客流、员工
    public void startActivitys(int position){
        Intent intent=new Intent(this, ItemDetailActivity.class);
        Bundle bundle=new Bundle();
        //柱状图数据
        bundle.putSerializable("zhuzhuangtu",(Serializable) zhuList1);
        //饼状图数据
        bundle.putSerializable("pie",(Serializable) pie_data);
        //七日平均值
        intent.putExtra("monthAvg",monthAvg);
        //七日当前总量
        intent.putExtra("monthTal",monthTal);
        //点击位置
        intent.putExtra("position",position);
        //会所名称
        intent.putExtra("org_name",org_name);
        //会所ID
        intent.putExtra("org_id",org_id);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
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
        }

    }
}
