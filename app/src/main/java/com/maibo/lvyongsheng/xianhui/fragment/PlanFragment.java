package com.maibo.lvyongsheng.xianhui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.XiangMuPlanActivity;
import com.maibo.lvyongsheng.xianhui.entity.Custemer;
import com.maibo.lvyongsheng.xianhui.implement.DrawRoundCorner;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by LYS on 2016/9/12.
 */
public class PlanFragment extends Fragment {

    private ListView lv_plan;
    SharedPreferences sp;
    String apiURL;
    String token;
    List<Custemer> data3, data4;
    //记录data3和data4中是否有值
    int litleDate=0;
    //判断该Activity是否已经创建
    int m=0;
    ProgressDialog dialog;

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    data3 = (List<Custemer>) msg.obj;
                    if (data3.size() != 0)
                        litleDate++;
                    break;
                case 2:
                    data4 = (List<Custemer>) msg.obj;
                    if (data4.size() != 0)
                        litleDate++;
                    startAdapter();
                    break;
            }
        }};

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.plan_fragment, container, false);
            dialog=new ProgressDialog(getActivity());
            dialog.setMessage("加载中...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(true);
            dialog.setIndeterminate(false);
            dialog.show();
            m=1;
            lv_plan = (ListView) view.findViewById(R.id.lv_plan);
            sp = getActivity().getSharedPreferences("baseDate", Context.MODE_PRIVATE);
            apiURL = sp.getString("apiURL", null);
            token = sp.getString("token", null);
            initData();
            setOnItemClick();
            return view;
        }

    @Override
    public void onResume() {
        super.onResume();
        if(m==0){
            initData();
            setOnItemClick();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        m=0;
        litleDate=0;
    }

    //选项点击事件
        public void setOnItemClick(){
            lv_plan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //  data3是待计划
                    if (data3.size()>0&&data4.size()>0){
                        if (i>0&&i<data3.size()+1){
                            Custemer cus=data3.get(i-1);
                            int cusId=cus.getCustomer_id();
                            Intent intent=new Intent(getActivity(), XiangMuPlanActivity.class);
                            intent.putExtra("customer_id",cusId);
                            startActivity(intent);
                        }else if (i>data3.size()+1){
                            Custemer cus=data4.get(i-(data3.size()+2));
                            int cusId=cus.getCustomer_id();
                            Intent intent=new Intent(getActivity(), XiangMuPlanActivity.class);
                            intent.putExtra("customer_id",cusId);
                            startActivity(intent);
                        }
                    }else if(data3.size()>0&&data4.size()==0){
                        if (i>0){
                            Custemer cus=data3.get(i-1);
                            int cusId=cus.getCustomer_id();
                            Intent intent=new Intent(getActivity(), XiangMuPlanActivity.class);
                            intent.putExtra("customer_id",cusId);
                            startActivity(intent);
                        }
                    }else if (data3.size()==0&&data4.size()>0){
                        if (i>0){
                            Custemer cus=data4.get(i-1);
                            int cusId=cus.getCustomer_id();
                            Intent intent=new Intent(getActivity(), XiangMuPlanActivity.class);
                            intent.putExtra("customer_id",cusId);
                            startActivity(intent);
                        }
                    }
                }
            });
        }

        public void startAdapter() {
            lv_plan.setAdapter(new MyAdapter());
        }

        //适配器
        class MyAdapter extends BaseAdapter {
            int p=0;

            @Override
            public int getCount() {
                return data3.size() + data4.size()+litleDate;
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
               ViewHolder holder;
                view=null;
                if(view==null){
                    holder=new ViewHolder();
                    view=View.inflate(getActivity(), R.layout.plan_fragment_style, null);
                    holder.iv_head_plan=(ImageView) view.findViewById(R.id.iv_head_plan);
                    holder.tv_name=(TextView) view.findViewById(R.id.tv_name);
                    holder.tv_state=(TextView) view.findViewById(R.id.tv_state);
                    holder.ll_add_view=(LinearLayout) view.findViewById(R.id.ll_add_view);
                    view.setTag(holder);
                }else{
                    holder=(ViewHolder) view.getTag();
                }

                if (data3.size() != 0 && i<1+data3.size()) {
                    if (i == 0) {
                        p=1;
                        TextView textView = new TextView(getActivity());
                        textView.setText("待计划");
                        textView.setBackgroundColor(getResources().getColor(R.color.weixin_lianxiren_gray));
                        textView.setPadding(30,10,10,10);
                        textView.setTextSize(16);
                        return textView;
                    }else{
                        Custemer cus = data3.get(i-1);
                        holder.tv_name.setText(cus.getFullname());
                        holder.tv_state.setText("待计划");
                        setHeadPicture(cus.getAvator_url(),holder.iv_head_plan);
                        return view;
                    }
                }
                if(data3.size()==0){
                    if (i == 0) {
                        TextView textView = new TextView(getActivity());
                        textView.setText("已计划");
                        textView.setBackgroundColor(getResources().getColor(R.color.weixin_lianxiren_gray));
                        textView.setPadding(30,10,10,10);
                        textView.setTextSize(16);
                        return textView;
                    }else{
                        Custemer cus = data4.get(i-1);
                        holder.tv_name.setText(cus.getFullname());
                        holder.tv_state.setText("已计划");
                        setHeadPicture(cus.getAvator_url(),holder.iv_head_plan);
                        return view;
                    }
                }else{
                    if (data4.size() != 0) {
                        if (i == 1+data3.size()) {
                            TextView textView = new TextView(getActivity());
                            textView.setText("已计划");
                            textView.setTextSize(16);
                            textView.setBackgroundColor(getResources().getColor(R.color.weixin_lianxiren_gray));
                            textView.setPadding(30,10,10,10);
                            return textView;
                        }else{
                            Custemer cus = data4.get(i-(2+data3.size()));
                            holder.tv_name.setText(cus.getFullname());
                            holder.tv_state.setText("已计划");
                            setHeadPicture(cus.getAvator_url(),holder.iv_head_plan);
                            return view;
                        }
                }
                }

                return null;
            }
        }

    //下载头像
    public void setHeadPicture(String avator_url,final ImageView iv_avator){
        //下载头像
        OkHttpUtils
                .get()
                .url(avator_url)
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }
                    @Override
                    public void onResponse(Bitmap response, int id) {
                        Bitmap bt= DrawRoundCorner.makeRoundCorner(response);
                        Drawable drawable =new BitmapDrawable(bt);
                        iv_avator.setImageDrawable(drawable);
                    }
                });
    }

        /**
         * 获取填充Listview的数据
         */
        public void initData() {
            //从服务器端获取数据
            OkHttpUtils
                    .post()
                    .url(apiURL + "/rest/employee/getplantable")
                    .addParams("token", token)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }
                        @Override
                        public void onResponse(String response, int id) {
                            //Log.e("PlanFragment:",response);
                            //获取顾客信息
                            List<Custemer> data1 = new ArrayList<Custemer>();
                            List<Custemer> data2 = new ArrayList<Custemer>();
                            JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                            JsonArray array = object.get("data").getAsJsonArray();
                            for (JsonElement jsonElement : array) {
                                JsonObject jObject = jsonElement.getAsJsonObject();
                                String avator_url=jObject.get("avator_url").getAsString();
                                String names = jObject.get("fullname").getAsString();
                                String planed = jObject.get("planed").getAsString();
                                int customer_id=jObject.get("customer_id").getAsInt();
                                if (planed.equals("0")) {
                                    data1.add(new Custemer(avator_url,names,customer_id, planed));//待计划
                                } else {
                                    data2.add(new Custemer(avator_url,names, customer_id,planed));//已计划
                                }

                            }
                            Message msg1 = Message.obtain();
                            msg1.what = 1;
                            msg1.obj = data1;
                            handler.sendMessage(msg1);
                            Message msg2 = Message.obtain();
                            msg2.what = 2;
                            msg2.obj = data2;
                            handler.sendMessage(msg2);
                            dialog.dismiss();
                        }
                    });
        }
    class ViewHolder{
        TextView tv_name ,tv_state;
        ImageView iv_head_plan;
        LinearLayout ll_add_view;
    }

}
