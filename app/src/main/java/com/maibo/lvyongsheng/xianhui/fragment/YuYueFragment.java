package com.maibo.lvyongsheng.xianhui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.Custemer;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by LYS on 2016/9/12.
 */
public class YuYueFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SharedPreferences sp;
    String apiURL;
    String token;
    List<Custemer> list1;
    ListView lv_yuyue;
    MyAdapter myAda;
    SwipeRefreshLayout swipRefresh;

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    list1 = (List<Custemer>) msg.obj;
                    //操作数据
                    operationData();
                    break;
                case 2:
                    initData();
                    myAda.notifyDataSetChanged();
                    swipRefresh.setRefreshing(false);
                    break;


            }
        }};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.yuyue_fragment, container, false);
        lv_yuyue=(ListView )view.findViewById(R.id.lv_yuyue);
        swipRefresh=(SwipeRefreshLayout) view.findViewById(R.id.swipRefresh);
        swipRefresh.setOnRefreshListener(this);
        sp = getActivity().getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
        initData();
        myAda=new MyAdapter();
        return view;
    }

    public void operationData(){
        lv_yuyue.setAdapter(myAda);
    }
    //适配器
    class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return list1.size();
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
            View v = View.inflate(getActivity(), R.layout.plan_fragment_style, null);
            TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
            TextView tv_state = (TextView) v.findViewById(R.id.tv_state);
            Custemer cus=list1.get(i);
            tv_name.setText(cus.getFullname());
            tv_state.setText(cus.getStatus());
            return v;
        }
    }

    public void initData(){
        //从服务器端获取数据
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getscheduletable")
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        List<Custemer> list=new ArrayList<>();
                        //获取顾客信息
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        JsonObject data = object.get("data").getAsJsonObject();
                        JsonArray array = data.get("rows").getAsJsonArray();

                        for (JsonElement jsonElement : array) {
                            JsonObject jObject = jsonElement.getAsJsonObject();
                            String names = jObject.get("fullname").getAsString();
                            String status = jObject.get("status").getAsString();
                            String adate=jObject.get("adate").getAsString();
                            String start_time=jObject.get("start_time").getAsString();
                            String end_time=jObject.get("end_time").getAsString();
                            list.add(new Custemer(names,status,adate,start_time,end_time));


                        }
                        Message msg1 = Message.obtain();
                        msg1.what = 1;
                        msg1.obj = list;
                        handler.sendMessage(msg1);

                    }
                });
    }

    @Override
    public void onRefresh() {
       /* Message msg2 = Message.obtain();
        msg2.what=2;
        handler.sendMessage(msg2);*/
        handler.sendEmptyMessageAtTime(2,2000);

    }
}
