package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.entity.Notice;
import com.maibo.lvyongsheng.xianhui.view.RefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;

/**
 * Created by LYS on 2016/9/27.
 */
public class RemindActivity extends Activity implements RefreshListView.OnRefreshListener{

    SharedPreferences sp;
    String apiURL;
    String token;
    RefreshListView lv_remind;
    List<Notice> data;
    List<Notice> data2;
    ProgressDialog dialog;
    TextView back;
    int currentPageNum;
    int totalPage;
    MyAdapter adapter;
    Boolean isLoadingMore=false;
    android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    List<Notice> datas=(List<Notice>)msg.obj;
                    currentPageNum=msg.arg1;
                    totalPage=msg.arg2;
                    if (isLoadingMore&&datas!=null){
                        Collections.reverse(datas);
                        data.addAll(0,datas);
                        adapter.notifyDataSetChanged();
                        lv_remind.setSelection(data.size()-data2.size());
                    }else{
                        Collections.reverse(datas);
                        data.clear();
                        data=datas;
                        lv_remind.setAdapter(adapter=new MyAdapter());
                    }
                    data2.addAll(0,datas);
                    dialog.dismiss();
                    lv_remind.completeRefresh();
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
        dialog=new ProgressDialog(this);
        dialog.setMessage("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
        dialog.show();
        data=new ArrayList<>();
        data2=new ArrayList<>();
        sp = getSharedPreferences("baseDate", MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
        lv_remind = (RefreshListView) findViewById(R.id.lv_remind);
        lv_remind.setOnRefreshListener(this);
        back= (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getServiceData(1);
    }

    @Override
    public void onPullRefresh() {
        //下拉加载更多
        isLoadingMore=true;
        if (currentPageNum!=totalPage){
            getServiceData(currentPageNum+1);
        }else{
            App.showToast(getApplicationContext(),"已加载全部!");
            lv_remind.completeRefresh();
        }

    }

    @Override
    public void onLoadingMore() {
        //上拉无效
        //data.clear();
//        isLoadingMore=false;
//        getServiceData(1);
        lv_remind.completeRefresh();

    }

    //请求数据
    public void getServiceData(int pageNum) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getnoticelist")
                .addParams("token", token)
                .addParams("pageNumber",pageNum+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //Log.e("remind",response);
                        //解析数据
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        String data_status = object.get("status").getAsString();
                        int total = 0;
                        int pageSize = 0;
                        int pageNumber = 0;
                        int totalPage = 0;
                        if (data_status.equals("ok")) {
                            List<Notice> data1 = new ArrayList<Notice>();
                            JsonObject data = object.get("data").getAsJsonObject();
                            if (!data.get("total").isJsonNull())
                                total = data.get("total").getAsInt();
                            if (!data.get("pageSize").isJsonNull())
                                pageSize=data.get("pageSize").getAsInt();
                            if (!data.get("pageNumber").isJsonNull())
                                pageNumber=data.get("pageNumber").getAsInt();
                            if (!data.get("totalPage").isJsonNull())
                                totalPage=data.get("totalPage").getAsInt();
                        JsonArray rows = data.get("rows").getAsJsonArray();
                        //判断rows是否为空，是：Toast（没有更多数据了）否：显示加载的数据
                        if (rows!=null){
                            for (JsonElement jsonElement : rows) {
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                int notice_id=0;
                                String notice_type="";
                                String subject ="";
                                String body ="";
                                String create_time ="";
                                int status =0;
                                String extra_id ="";
                                String org_name="";
                                if (!jsonObject.get("notice_id").isJsonNull())
                                    notice_id = jsonObject.get("notice_id").getAsInt();
                                if (!jsonObject.get("notice_type").isJsonNull())
                                    notice_type = jsonObject.get("notice_type").getAsString();
                                if (!jsonObject.get("subject").isJsonNull())
                                    subject = jsonObject.get("subject").getAsString();
                                if (!jsonObject.get("body").isJsonNull())
                                    body = jsonObject.get("body").getAsString();
                                if (!jsonObject.get("create_time").isJsonNull())
                                    create_time = jsonObject.get("create_time").getAsString();
                                if (!jsonObject.get("status").isJsonNull())
                                    status = jsonObject.get("status").getAsInt();
                                if (!jsonObject.get("extra_id").isJsonNull())
                                    extra_id = jsonObject.get("extra_id").getAsString();
                                if (!jsonObject.get("org_name").isJsonNull())
                                    org_name=jsonObject.get("org_name").getAsString();
                                data1.add(new Notice(notice_id, notice_type, subject, body, create_time, status, extra_id,org_name));
                            }
                            Message msg = Message.obtain();
                            msg.what = 1;
                            msg.obj = data1;
                            msg.arg1=pageNumber;
                            msg.arg2=totalPage;
                            handler.sendMessage(msg);
                        }else {
                            App.showToast(getApplicationContext(),"已加载全部!");
                        }


                    }
                }
                });
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return data.size();
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
            if(view==null){
                holder=new ViewHolder();
                view = View.inflate(RemindActivity.this,R.layout.zhu_shou_list_style,null);
                holder.create_time=(TextView) view.findViewById(R.id.create_time);
                holder.subject=(TextView) view.findViewById(R.id.subject);
                holder.extra_id=(TextView) view.findViewById(R.id.extra_id);
                holder.body=(TextView) view.findViewById(R.id.body);
                holder.tv_red_tag=(TextView) view.findViewById(R.id.tv_red_tag);
                view.setTag(holder);
            }else{
                holder=(ViewHolder) view.getTag();
            }

            Notice not=data.get(i);
            holder.create_time.setText(not.getCreat_time().substring(0,10));
            holder.subject.setText(not.getSubject()+"("+not.getOrg_name()+")");
           // holder.extra_id.setText(not.getExtra_id());
            holder.extra_id.setText(not.getCreat_time().substring(0,10));
            holder.body.setText(not.getBody());
            holder.tv_red_tag.setVisibility(View.INVISIBLE);
           /* if(not.getStatus()==0){
                holder.tv_red_tag.setVisibility(View.INVISIBLE);
            }else if(not.getStatus()==1){
                holder.tv_red_tag.setVisibility(View.INVISIBLE);
            }*/

            return view;
        }
    }

    class ViewHolder{
        TextView  create_time,subject,extra_id,body,tv_red_tag;
    }
}
