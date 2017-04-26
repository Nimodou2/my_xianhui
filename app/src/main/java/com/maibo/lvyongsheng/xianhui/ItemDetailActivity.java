package com.maibo.lvyongsheng.xianhui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.adapter.ImageAdapter;
import com.maibo.lvyongsheng.xianhui.entity.Card;
import com.maibo.lvyongsheng.xianhui.entity.Radar;
import com.maibo.lvyongsheng.xianhui.entity.TabMax;
import com.maibo.lvyongsheng.xianhui.fragment.YeJiZhuChengFragment1;
import com.maibo.lvyongsheng.xianhui.fragment.YeJiZhuChengFragment2;
import com.maibo.lvyongsheng.xianhui.fragment.YeJiZhuChengFragment3;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.utils.NetWorkUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2016/9/2.
 */
public class ItemDetailActivity extends BaseFragmentActivity implements View.OnClickListener {
//    ProgressDialog dialog;
    ViewPager viewPager;
    MyFragmentPageAdapter fragmentPageAdapter;
    List<Fragment> data;
    LinearLayout dot_layout;
    int[] monthAvg;
    int[] monthTal;
    //饼状图的数据
    TextView tv_number, lv_avg, lv_total, tv_time, tv_item_name, tv_setting, back;
    Gallery gallery;
    //同一种类型一周的数据
    int[] weekDays;
    //保存日期
    String[] cutDateTime;
    SharedPreferences sp;
    String token;
    String apiURL;
    List<TabMax> list1;

    List<List<Radar>> zhuList1;
    List<List<List<List<Card>>>> pie_data;
    String name;
    float[] values;
    int position;
    ImageAdapter imageAdapter;

    String org_id;
    String org_name;

    public int test = 0;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.in_no_datas)
    LinearLayout in_no_datas;
    @Bind(R.id.in_loading_error)
    LinearLayout in_loading_error;
    @Bind(R.id.ll_all_data)
    LinearLayout ll_all_data;

    public int getTest() {
        return test;
    }

    public void setTest(int test) {
        this.test = test;
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    in_loading_error.setVisibility(View.GONE);
                    ll_all_data.setVisibility(View.VISIBLE);

                    list1 = (List<TabMax>) msg.obj;
                    values = new float[5];
                    for (int i = 0; i < 5; i++) {
                        values[i] = list1.get(i).getValue();
                    }
                    //Gallery适配器
                    imageAdapter = new ImageAdapter(ItemDetailActivity.this, weekDays, position, values);
                    // gallery添加ImageAdapter图片资源
                    gallery.setAdapter(imageAdapter);
                    gallery.setOnItemClickListener(listener); // gallery设置点击图片资源的事件
                    gallery.setOnItemSelectedListener(selectedListener);//gallery设置选中图片资源的事件
                    gallery.setSelection(6);
                    break;
                case 1:
                    //网络异常
                    in_loading_error.setVisibility(View.VISIBLE);
                    ll_all_data.setVisibility(View.GONE);
                    break;
                case 2:
                    list1 = (List<TabMax>) msg.obj;
                    Intent intent = new Intent(ItemDetailActivity.this, MaxSettingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("maxList", (Serializable) list1);
                    bundle.putString("org_name", org_name);
                    bundle.putString("org_id", org_id);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 15);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xian_jin);
        adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        //showLongDialog();

        sp = getSharedPreferences("baseDate", MODE_PRIVATE);
        token = sp.getString("token", null);
        apiURL = sp.getString("apiURL", null);

        viewPager = (ViewPager) findViewById(R.id.vp_xianjing);
        dot_layout = (LinearLayout) findViewById(R.id.dot_layout);
        tv_number = (TextView) findViewById(R.id.tv_number);
        lv_avg = (TextView) findViewById(R.id.lv_avg);
        lv_total = (TextView) findViewById(R.id.lv_total);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_item_name = (TextView) findViewById(R.id.tv_item_name);
        tv_setting = (TextView) findViewById(R.id.tv_setting);
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setSpacing(screenWidth / 12);
        tv_setting.setOnClickListener(this);
        //默认为最后一个(作用：为了在选择Gallery时，饼状图也跟随变化)
        setTest(6);

        //接收数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        monthAvg = intent.getIntArrayExtra("monthAvg");
        monthTal = intent.getIntArrayExtra("monthTal");
        org_name = intent.getStringExtra("org_name");
        org_id = intent.getStringExtra("org_id");

        //柱状图数据
        zhuList1 = (List<List<Radar>>) bundle.get("zhuzhuangtu");
        //饼状图数据
        pie_data = (List<List<List<List<Card>>>>) bundle.get("pie");
        position = intent.getIntExtra("position", -1);

        //准备柱状图数据
        String[] dateTime = new String[7];
        weekDays = new int[7];
        for (int i = 0; i < 7; i++) {
            if (position == 0) {
                name = zhuList1.get(0).get(0).getName();
                dateTime[i] = zhuList1.get(i).get(0).getDate();
                weekDays[i] = zhuList1.get(i).get(0).getAmount();
                setData(0);
            } else if (position == 1) {
                name = zhuList1.get(0).get(1).getName();
                dateTime[i] = zhuList1.get(i).get(1).getDate();
                weekDays[i] = zhuList1.get(i).get(1).getAmount();
                setData(1);
            } else if (position == 2) {
                name = zhuList1.get(0).get(2).getName();
                dateTime[i] = zhuList1.get(i).get(2).getDate();
                weekDays[i] = zhuList1.get(i).get(2).getAmount();
                setData(2);
            } else if (position == 3) {
                name = zhuList1.get(0).get(3).getName();
                dateTime[i] = zhuList1.get(i).get(3).getDate();
                weekDays[i] = zhuList1.get(i).get(3).getAmount();
                setData(3);
            } else if (position == 4) {
                name = zhuList1.get(0).get(4).getName();
                dateTime[i] = zhuList1.get(i).get(4).getDate();
                weekDays[i] = zhuList1.get(i).get(4).getAmount();
                setData(4);
            }
        }
        tv_item_name.setText(name);
        //对获取到的日期进行截取只保留月和天
        cutDateTime = new String[7];
        for (int i = 0; i < 7; i++) {
            cutDateTime[i] = dateTime[i].substring(5);
        }

        //第一次进入默认选择填充第七个
        int number = weekDays[6];
        tv_number.setText(number + "");


       /* gallery.setOnItemClickListener(listener); // gallery设置点击图片资源的事件
        gallery.setOnItemSelectedListener(selectedListener);//gallery设置选中图片资源的事件
        gallery.setSelection(6);*/

        data = new ArrayList<>();

        if (name.equals("工时")) {
            data.add(new YeJiZhuChengFragment1());
            data.add(new YeJiZhuChengFragment2());
        } else {
            data.add(new YeJiZhuChengFragment1());
            data.add(new YeJiZhuChengFragment2());
            data.add(new YeJiZhuChengFragment3());
        }

        initDots();
        updateIntroAndDot();
        initListener();
        fragmentPageAdapter = new MyFragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPageAdapter);
        //获取日报表峰值
        getServiceMaxData(0);

    }


    public void setData(int i) {
        //填充当前平均和当前总量
        lv_avg.setText("当前平均:" + monthAvg[i]);
        lv_total.setText("当前总量:" + monthTal[i]);
    }

    /**
     * 初始化dot
     */
    private void initDots() {
        for (int i = 0; i < data.size(); i++) {
            View view = new View(this);
            LayoutParams params = new LayoutParams(30, 30);
            params.topMargin = 5;
            if (i != 0) {
                params.leftMargin = 20;
            }
            view.setLayoutParams(params);
            view.setBackgroundResource(R.drawable.selector_dot);
            dot_layout.addView(view);
        }
    }

    //ViewPager监听器
    private void initListener() {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateIntroAndDot();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 更新文本
     */
    private void updateIntroAndDot() {
        int currentPage = viewPager.getCurrentItem() % data.size();
        for (int i = 0; i < dot_layout.getChildCount(); i++) {
            dot_layout.getChildAt(i).setEnabled(i == currentPage);
        }
    }

    //点击进入设置最大值
    @Override
    public void onClick(View view) {
        //判断网络连接状态
        boolean isNetWork = NetWorkUtils.isNetworkConnected(this);
        if (isNetWork) {
            if (list1==null){
                showToast("网络异常");
            }else{
                Intent intent = new Intent(ItemDetailActivity.this, MaxSettingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("maxList", (Serializable) list1);
                bundle.putString("org_name", org_name);
                bundle.putString("org_id", org_id);
                intent.putExtras(bundle);
                startActivityForResult(intent, 15);
            }

        } else {
            showToast("网络连接异常");
        }


    }

    //获取日报表峰值设置
    public void getServiceMaxData(final int tag) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getdailyreportsetting")
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Message msg = Message.obtain();
                        msg.what = 1;
                        handler.sendMessage(msg);
                        dismissLongDialog();
                        dismissShortDialog();

                    }

                    @Override
                    public void onResponse(String response, int id) {
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
                        if (tag==0){
                            Message msg = Message.obtain();
                            msg.what = 0;
                            msg.obj = list;
                            handler.sendMessage(msg);
                        }
                        dismissLongDialog();
                        dismissShortDialog();

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


    //ViewPager适配器
    class MyFragmentPageAdapter extends FragmentStatePagerAdapter {

        public MyFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return data.get(position);
        }

        @Override
        public int getCount() {
            return data.size();
        }
    }

    //Gallery适配器view的点击事件
    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int number = weekDays[position];
            tv_number.setText(number + "");
            tv_time.setText(cutDateTime[position]);
            setTest(position);
            data.clear();
            if (name.equals("工时")) {
                data.add(new YeJiZhuChengFragment1());
                data.add(new YeJiZhuChengFragment2());
            } else {
                data.add(new YeJiZhuChengFragment1());
                data.add(new YeJiZhuChengFragment2());
                data.add(new YeJiZhuChengFragment3());
            }
            viewPager.setAdapter(fragmentPageAdapter);
        }
    };
    AdapterView.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            int number = weekDays[i];
            tv_number.setText(number + "");
            tv_time.setText(cutDateTime[i]);
            setTest(i);
            data.clear();
            if (name.equals("工时")) {
                data.add(new YeJiZhuChengFragment1());
                data.add(new YeJiZhuChengFragment2());
            } else {
                data.add(new YeJiZhuChengFragment1());
                data.add(new YeJiZhuChengFragment2());
                data.add(new YeJiZhuChengFragment3());
            }
            viewPager.setAdapter(fragmentPageAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 16) {
            getServiceMaxData(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }

    /**
     * 网络问题，重新加载
     * @param view
     */
    public void loadingMore(View view){
        showShortDialog();
        getServiceMaxData(0);
    }
}
