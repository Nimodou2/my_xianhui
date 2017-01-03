package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.entity.YuyueTab;
import com.maibo.lvyongsheng.xianhui.implement.AllDividerDecoration;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2016/10/27.
 */
public class ReservationInformationActivity extends BaseActivity {

    SharedPreferences sp;
    String token, apiURL;
    int customer_id;
    List<YuyueTab> list1;
    RecyclerView rv_yuyue;
    TextView back;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            list1 = (List<YuyueTab>) msg.obj;
            //处理数据:lastDatas为处理后的数据
            List<LastData> lastDatas = dealDatas();
            //传递给适配器
            LinearLayoutManager llm = new LinearLayoutManager(ReservationInformationActivity.this);
            rv_yuyue.setLayoutManager(llm);
            rv_yuyue.addItemDecoration(new AllDividerDecoration(ReservationInformationActivity.this));
            rv_yuyue.setAdapter(new MyRecyclerViewAdapter(lastDatas));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuyue_message);
        adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        rv_yuyue = (RecyclerView) findViewById(R.id.rv_yuyue);
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token = sp.getString("token", null);
        apiURL = sp.getString("apiURL", null);
        Intent intent = getIntent();
        customer_id = intent.getIntExtra("customer_id", -1);
        getYuyueMsg();
    }

    /**
     * 获取顾客预约信息
     */
    public void getYuyueMsg() {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getcustomerschedulelist")
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
                        List<YuyueTab> list = new ArrayList<YuyueTab>();
                        if (!jsonObject.get("data").isJsonNull()) {
                            JsonArray data = jsonObject.get("data").getAsJsonArray();
                            for (JsonElement je : data) {
                                JsonObject jo = je.getAsJsonObject();
                                String schedule_id = "";
                                String adate = "";
                                String start_time = "";
                                String end_time = "";
                                String item_id = "";
                                String fullname = "";
                                String engineer_id = "";
                                String engineer_name = "";
                                String status = "";
                                if (!jo.get("schedule_id").isJsonNull())
                                    schedule_id = jo.get("schedule_id").getAsString();
                                if (!jo.get("adate").isJsonNull())
                                    adate = jo.get("adate").getAsString();
                                if (!jo.get("start_time").isJsonNull())
                                    start_time = jo.get("start_time").getAsString();
                                if (!jo.get("end_time").isJsonNull())
                                    end_time = jo.get("end_time").getAsString();
                                if (!jo.get("item_id").isJsonNull())
                                    item_id = jo.get("item_id").getAsString();
                                if (!jo.get("fullname").isJsonNull())
                                    fullname = jo.get("fullname").getAsString();
                                if (!jo.get("engineer_id").isJsonNull())
                                    engineer_id = jo.get("engineer_id").getAsString();
                                if (!jo.get("engineer_name").isJsonNull())
                                    engineer_name = jo.get("engineer_name").getAsString();
                                if (!jo.get("status").isJsonNull())
                                    status = jo.get("status").getAsString();
                                list.add(new YuyueTab(schedule_id, adate, start_time, end_time, item_id, fullname, engineer_id, engineer_name, status));
                            }
                        }
                        Message msg = Message.obtain();
                        msg.obj = list;
                        handler.sendMessage(msg);
                    }
                });
    }

    //处理数据
    public List<LastData> dealDatas() {
        //将数据倒序排列


        //将数据按时间排列
        List<LastData> lastList = new ArrayList<>();
        List<String> dataTime = new ArrayList<>();
        List<String> queChong = new ArrayList<>();
       /* //倒序数据
        List<YuyueTab> fanDatas=new ArrayList<>();
        for (int i=0;i<list1.size();i++){
            fanDatas.add(list1.get(list1.size()-1-i));
        }*/
        //将时间单独拿出来为比较做准备
        for (int i = 0; i < list1.size(); i++) {
            dataTime.add(list1.get(i).getAdate());
        }
        for (int i = 0; i < list1.size(); i++) {
            if (!queChong.contains(list1.get(i).getAdate())) {
                queChong.add(list1.get(i).getAdate());
                lastList.add(new LastData(dataTime.get(i), null, 0));
            }
            lastList.add(new LastData("0", list1.get(i), 1));
        }
        return lastList;
    }

    //适配器
    class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<LastData> lists;
        LayoutInflater inflater;

        MyRecyclerViewAdapter(List<LastData> lists) {
            this.lists = lists;
            inflater = LayoutInflater.from(ReservationInformationActivity.this);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new TimeViewHolder(inflater.inflate(R.layout.style_yuyue_time, parent, false));
            } else {
                return new ContentViewHolder(inflater.inflate(R.layout.style_yuyue_content, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof TimeViewHolder) {
                if (position == 0) {
                    ((TimeViewHolder) holder).tv_datatimes.setText(lists.get(position).getTime());
                    ((TimeViewHolder) holder).tv_message.setVisibility(View.VISIBLE);
                    ((TimeViewHolder) holder).tv_jihua.setVisibility(View.VISIBLE);
                    ((TimeViewHolder) holder).tv_shiji.setVisibility(View.VISIBLE);
                } else {
                    ((TimeViewHolder) holder).tv_datatimes.setText(lists.get(position).getTime());
                    ((TimeViewHolder) holder).tv_message.setVisibility(View.INVISIBLE);
                    ((TimeViewHolder) holder).tv_jihua.setVisibility(View.INVISIBLE);
                    ((TimeViewHolder) holder).tv_shiji.setVisibility(View.INVISIBLE);
                }
            } else if (holder instanceof ContentViewHolder) {
                ((ContentViewHolder) holder).tv_yuyue_name.setText(lists.get(position).getYuYueList().getFullname());
                ((ContentViewHolder) holder).tv_start_end_time.setText(lists.get(position).getYuYueList().getStart_time() + "-" + lists.get(position).getYuYueList().getEnd_time());
            }
        }

        @Override
        public int getItemCount() {
            return lists == null ? 0 : lists.size();
        }

        @Override
        public int getItemViewType(int position) {

            return lists.get(position).getType();
        }

        class TimeViewHolder extends RecyclerView.ViewHolder {
            TextView tv_datatimes, tv_message, tv_shiji, tv_jihua;
            LinearLayout ll_reservation_time;

            TimeViewHolder(View view) {
                super(view);
                tv_datatimes = (TextView) view.findViewById(R.id.tv_datatimes);
                tv_message = (TextView) view.findViewById(R.id.tv_message);
                tv_shiji = (TextView) view.findViewById(R.id.tv_shiji);
                tv_jihua = (TextView) view.findViewById(R.id.tv_jihua);
                ll_reservation_time= (LinearLayout) view.findViewById(R.id.ll_reservation_time);
                ViewGroup.LayoutParams params=ll_reservation_time.getLayoutParams();
                params.height=viewHeight*15/255;
                ll_reservation_time.setLayoutParams(params);
            }
        }

        class ContentViewHolder extends RecyclerView.ViewHolder {
            TextView tv_yuyue_name, tv_start_end_time;
            LinearLayout ll_reservation_content;

            ContentViewHolder(View view) {
                super(view);
                tv_yuyue_name = (TextView) view.findViewById(R.id.tv_yuyue_name);
                tv_start_end_time = (TextView) view.findViewById(R.id.tv_start_end_time);
                ll_reservation_content= (LinearLayout) view.findViewById(R.id.ll_reservation_content);
                ViewGroup.LayoutParams params=ll_reservation_content.getLayoutParams();
                params.height=viewHeight*15/255;
                ll_reservation_content.setLayoutParams(params);

            }
        }
    }

    //最终数据形式
    class LastData {
        String time;
        YuyueTab yuYueList;
        int type;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public YuyueTab getYuYueList() {
            return yuYueList;
        }

        public void setYuYueList(YuyueTab yuYueList) {
            this.yuYueList = yuYueList;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public LastData(String time, YuyueTab yuYueList, int type) {
            this.time = time;
            this.yuYueList = yuYueList;
            this.type = type;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
