package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.entity.Notice;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.view.RefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2016/9/11.
 */
public class HelperActivity extends BaseActivity implements RefreshListView.OnRefreshListener {

    private RefreshListView lv_zhushou;
    SharedPreferences sp;
    String apiURL;
    String token;
    List<Notice> data2;
    List<Notice> data3;
    TextView back;
    MyAdapter adapter;
    Boolean isLoadingMore = false;
    int currentPageNum;
    int totalPage;
    //记录点击的条目
    int whatItem = -1;
    Notice whatNotice;

    @Bind(R.id.ll_head)
    LinearLayout ll_head;

    @Bind(R.id.in_no_datas)
    LinearLayout in_no_datas;
    @Bind(R.id.in_loading_error)
    LinearLayout in_loading_error;


    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    in_loading_error.setVisibility(View.VISIBLE);
                    lv_zhushou.setVisibility(View.GONE);
                    break;
                case 1:
                    List<Notice> data = (List<Notice>) msg.obj;
                    currentPageNum = msg.arg1;
                    totalPage = msg.arg2;
                    if (isLoadingMore && data != null) {
                        //先将data数据倒序之后再加入
                        Collections.reverse(data);
                        data2.addAll(0, data);
                        adapter.notifyDataSetChanged();
                        lv_zhushou.setSelection(data2.size() - data3.size());

                    } else {
                        Collections.reverse(data);
                        data2.clear();
                        data2 = data;
                        if (data2.size()==0){
                            in_no_datas.setVisibility(View.VISIBLE);
                            lv_zhushou.setVisibility(View.GONE);
                            return;
                        }
                        lv_zhushou.setAdapter(adapter = new MyAdapter(data2));
                    }
                    data3.addAll(0, data);
                    lv_zhushou.completeRefresh();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData(1);
        lv_zhushou.setOnRefreshListener(this);
        lv_zhushou.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //将notice_id和position传递到下一个Activity
                //加上header和footer会是适配器的条目数增加两项
                whatItem = i;
                whatNotice = data2.get(i - 1);
                if (i > 0 && i < data2.size() + 1) {
                    if (data2.get(i - 1).getNotice_type().equals("daily_report")) {
                        //跳到日报表
                        Intent intent = new Intent(HelperActivity.this, DayTabActivity.class);
                        intent.putExtra("notice_id", data2.get(i - 1).getNotice_id());
                        intent.putExtra("org_id", data2.get(i - 1).getOrg_id());
                        intent.putExtra("org_name", data2.get(i - 1).getOrg_name());
                        intent.putExtra("create_time", data2.get(i - 1).getCreat_time());
                        startActivity(intent);
                    } else if (data2.get(i - 1).getNotice_type().equals("project_plan")) {
                        //跳到项目计划
                        startActivity(new Intent(getApplicationContext(), WorkActivity.class));
                    }
                }
            }
        });

    }

    private void initView() {
        setContentView(R.layout.activity_zhu_shou);
        CloseAllActivity.getScreenManager().pushActivity(this);
        adapterLitterBar(ll_head);

        //记录之前的Activity
        CloseAllActivity.getScreenManager().pushActivity(new MainActivity());
        lv_zhushou = (RefreshListView) findViewById(R.id.lv_zhushou);
        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
        data2 = new ArrayList<>();
        data3 = new ArrayList<>();
        showLongDialog();
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新数据，去除已读
        if (whatItem != -1) {
            data2.clear();
            Notice notice = new Notice(whatNotice.getNotice_id(), whatNotice.getNotice_type(), whatNotice.getSubject(), whatNotice.getBody(),
                    whatNotice.getCreat_time(), 1, whatNotice.getExtra_id(), whatNotice.getOrg_name(), whatNotice.getOrg_id());
            for (int i = 0; i < data3.size(); i++) {
                if (i == whatItem - 1) {
                    data2.add(notice);
                } else data2.add(data3.get(i));
            }
            data3.clear();
            data3.addAll(data2);
            adapter.notifyDataSetChanged();
        }


    }

    private void initData(int pageNum) {

        //获取助手列表
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/gethelperlist")
                .addParams("token", token)
                .addParams("pageNumber", pageNum + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Message msg = Message.obtain();
                        msg.what = 0;
                        handler.sendMessage(msg);
                        dismissLongDialog();
                        dismissShortDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // Log.e("助手：", response);
                        List<Notice> data1 = new ArrayList<Notice>();
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        String msg_status = object.get("status").getAsString();
                        String message=object.get("message").getAsString();
                        if (msg_status.equals("ok")) {
                            //解析Json字符串
                            analysisJsonDatas(data1, object);
                        }else{
                            showToast(message);
                        }
                        //
                        dismissLongDialog();
                        dismissShortDialog();
                    }
                });
    }

    /**
     * 解析Json
     * @param data1
     * @param object
     */
    private void analysisJsonDatas(List<Notice> data1, JsonObject object) {
        JsonObject data = object.get("data").getAsJsonObject();
        int pageSize = 0;
        int pageNumber = 0;
        int totalPage = 0;
        if (!data.get("pageSize").isJsonNull())
            pageSize = data.get("pageSize").getAsInt();
        if (!data.get("pageNumber").isJsonNull())
            pageNumber = data.get("pageNumber").getAsInt();
        if (!data.get("totalPage").isJsonNull())
            totalPage = data.get("totalPage").getAsInt();
        JsonArray rows = data.get("rows").getAsJsonArray();
        if (rows != null) {
            for (JsonElement jsonElement : rows) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                int notice_id = -1;
                String notice_type = "";
                String subject = "";
                String body = "";
                String create_time = "";
                int status = -1;
                String extra_id = "";
                String org_name = "";
                String org_id = "";
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
                    org_name = jsonObject.get("org_name").getAsString();
                if (!jsonObject.get("org_id").isJsonNull())
                    org_id = jsonObject.get("org_id").getAsString();
                data1.add(new Notice(notice_id, notice_type, subject, body, create_time, status, extra_id, org_name, org_id));
            }
            Message msg = Message.obtain();
            msg.obj = data1;
            msg.what = 1;
            msg.arg1 = pageNumber;
            msg.arg2 = totalPage;
            handler.sendMessage(msg);

        }
    }

    @Override
    public void onPullRefresh() {
        //下拉加载更多
        isLoadingMore = true;
        if (currentPageNum != totalPage) {
            initData(currentPageNum + 1);
        } else {
            App.showToast(getApplicationContext(), "已加载全部!");
            lv_zhushou.completeRefresh();
        }
    }

    @Override
    public void onLoadingMore() {
        //上拉设定为无效
        lv_zhushou.completeRefresh();

    }

    class MyAdapter extends BaseAdapter {
        List<Notice> list;

        MyAdapter(List<Notice> list) {
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
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = View.inflate(HelperActivity.this, R.layout.zhu_shou_list_style, null);
                holder.create_time = (TextView) view.findViewById(R.id.create_time);
                holder.subject = (TextView) view.findViewById(R.id.subject);
                holder.extra_id = (TextView) view.findViewById(R.id.extra_id);
                holder.body = (TextView) view.findViewById(R.id.body);
                holder.tv_red_tag = (TextView) view.findViewById(R.id.tv_red_tag);
                holder.iv_picture = (ImageView) view.findViewById(R.id.iv_picture);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            ViewGroup.LayoutParams params = holder.iv_picture.getLayoutParams();
            params.height = screenHeight / 60;
            holder.iv_picture.setLayoutParams(params);

            Notice not = list.get(i);
            holder.create_time.setText(not.getCreat_time().substring(0, 10));
            holder.subject.setText(not.getSubject() + "(" + not.getOrg_name() + ")");
            holder.extra_id.setText(not.getExtra_id());
            holder.body.setText(not.getBody());
            if (not.getStatus() == 0 && not.getNotice_type().equals("daily_report")) {
                holder.tv_red_tag.setVisibility(View.VISIBLE);
            } else if (not.getStatus() == 1 || not.getNotice_type().equals("project_plan")) {
                holder.tv_red_tag.setVisibility(View.INVISIBLE);
            }

            return view;
        }
    }

    class ViewHolder {
        TextView create_time, subject, extra_id, body, tv_red_tag;
        ImageView iv_picture;
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
        in_loading_error.setVisibility(View.GONE);
        lv_zhushou.setVisibility(View.VISIBLE);
        initData(1);
    }
}

