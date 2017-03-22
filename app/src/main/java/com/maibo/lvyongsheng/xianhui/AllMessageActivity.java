package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.entity.Employee;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.utils.NetWorkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by LYS on 2016/10/7.
 */
public class AllMessageActivity extends BaseActivity {
    ImageView cus_head;
    TextView cus_name, cus_grade, cus_files_num, back, tv_order;
    ListView lv_people_msg;
    SharedPreferences sp;
    String token, apiURL;
    int user_id;
    Employee employee;
    int tag;

    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.ll_head_message)
    LinearLayout ll_head_message;
    @Bind(R.id.tv_spacing)
    TextView tv_spacing;

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
                    ll_all_data.setVisibility(View.GONE);
                    in_loading_error.setVisibility(View.VISIBLE);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_message);
        adapterLitterBar(ll_head);
        setSingleViewHeightAndWidth(tv_spacing, viewHeight * 15 / 255, 0);
        setSingleViewHeightAndWidth(ll_head_message, viewHeight * 40 / 255, 0);
        CloseAllActivity.getScreenManager().pushActivity(this);

        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token = sp.getString("token", null);
        apiURL = sp.getString("apiURL", null);

        cus_head = (ImageView) findViewById(R.id.cus_head);
        setSingleViewHeightAndWidth(cus_head, viewHeight * 30 / 255, viewHeight * 30 / 255);
        cus_name = (TextView) findViewById(R.id.cus_name);
        cus_grade = (TextView) findViewById(R.id.cus_grade);
        cus_files_num = (TextView) findViewById(R.id.cus_files_num);
        lv_people_msg = (ListView) findViewById(R.id.lv_people_msg);
        tv_order = (TextView) findViewById(R.id.tv_order);
        back = (TextView) findViewById(R.id.back);


        Intent intent = getIntent();
        tag = intent.getIntExtra("tag", -1);
        Bundle bundle = intent.getExtras();
        employee = (Employee) bundle.get("Employee");
        user_id = employee.getUser_id();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        if (NetWorkUtils.isNetworkConnected(this)) {
            setListener();
            if (tag == 1) {
                setColleageData(employee);
            }
//            getColleagueOrder(user_id);
        } else {
            ll_all_data.setVisibility(View.GONE);
            in_loading_error.setVisibility(View.VISIBLE);
            showToast(R.string.net_connect_error);
        }


    }


    /**
     * 设置点击监听器
     */
    private void setListener() {
        tv_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到订单页面
                Intent intent = new Intent(AllMessageActivity.this, OrderActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("collName", employee.getDisplay_name());
                intent.putExtras(bundle);
                intent.putExtra("tag", 2);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });
    }

    //设置员工详细信息
    public void setColleageData(Employee employee) {
        String avator_url = employee.getAvator_url();
        String display_name = employee.getDisplay_name();
        String job = employee.getJob();
        String user_code = employee.getUser_code();
        Picasso.with(this).load(avator_url).into(cus_head);
        cus_name.setText(display_name);
        cus_grade.setText("职务:" + job);
        cus_files_num.setText("工号:" + user_code);
        ////////////////////////////////////////////////////////////////////////////////////
        String[] name = {"所属店铺", "入职日期", "手机号码", "级别"};
        String[] msg = {employee.getOrg_name(), employee.getEntry_date(), employee.getMobile(), "暂无"};
        List<Map<String, String>> lists = new ArrayList<>();
        for (int i = 0; i < name.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("left", name[i]);
            map.put("right", msg[i]);
            lists.add(map);
        }
        lv_people_msg.setAdapter(new MyAdapter(lists));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }

    class MyAdapter extends BaseAdapter {
        List<Map<String, String>> list;

        MyAdapter(List<Map<String, String>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = View.inflate(getApplicationContext(), R.layout.style_list_detail, null);
            TextView name = (TextView) v.findViewById(R.id.name);
            TextView numbers = (TextView) v.findViewById(R.id.numbers);
            LinearLayout ll_all = (LinearLayout) v.findViewById(R.id.ll_all);
            ViewGroup.LayoutParams params = ll_all.getLayoutParams();
            params.height = viewHeight * 20 / 255;
            ll_all.setLayoutParams(params);
            Map<String, String> map = list.get(i);
            name.setText(map.get("left"));
            numbers.setText(map.get("right"));
            return v;
        }
    }

    /**
     * 网络问题，重新加载
     *
     * @param view
     */
    public void loadingMore(View view) {
        if (NetWorkUtils.isNetworkConnected(this)) {
            setListener();
            if (tag == 1) {
                setColleageData(employee);
            }
        } else {
            ll_all_data.setVisibility(View.GONE);
            in_loading_error.setVisibility(View.VISIBLE);
            showToast(R.string.net_connect_error);
        }

    }
}
