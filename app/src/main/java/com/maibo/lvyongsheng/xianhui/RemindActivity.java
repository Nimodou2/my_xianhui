package com.maibo.lvyongsheng.xianhui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
 * Created by LYS on 2016/9/27.
 */
public class RemindActivity extends BaseActivity implements RefreshListView.OnRefreshListener {

    SharedPreferences sp;
    String apiURL;
    String token;
    RefreshListView lv_remind;
    List<Notice> data;
    List<Notice> data2;
    TextView back;
    int currentPageNum;
    int totalPage;
    MyAdapter adapter;
    Boolean isLoadingMore = false;
    int whatItem = -1;
    Notice whatNotice;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;

    @Bind(R.id.in_no_datas)
    LinearLayout in_no_datas;
    @Bind(R.id.in_loading_error)
    LinearLayout in_loading_error;

    android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    in_loading_error.setVisibility(View.VISIBLE);
                    lv_remind.setVisibility(View.GONE);
                    break;
                case 1:
                    List<Notice> datas = (List<Notice>) msg.obj;
                    currentPageNum = msg.arg1;
                    totalPage = msg.arg2;
                    if (isLoadingMore && datas != null) {
                        Collections.reverse(datas);
                        data.addAll(0, datas);
                        adapter.notifyDataSetChanged();
                        lv_remind.setSelection(data.size() - data2.size());
                    } else {
                        Collections.reverse(datas);
                        data.clear();
                        data = datas;
                        if (data.size() == 0) {
                            in_no_datas.setVisibility(View.VISIBLE);
                            lv_remind.setVisibility(View.GONE);
                            return;
                        }
                        lv_remind.setAdapter(adapter = new MyAdapter());
                    }
                    data2.addAll(0, datas);
                    lv_remind.completeRefresh();

                    lv_remind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //只跳转和订单相关的提醒
                            whatItem = i;
                            whatNotice = data.get(i - 1);
                            String extra_type = whatNotice.getExtra_type();
                            String customer_id1 = whatNotice.getCustomer_id();
                            if (extra_type.equals("schedule")) {
                                int customer_id = Integer.parseInt(customer_id1);
                                Intent intent = new Intent(RemindActivity.this, PeopleMessageActivity.class);
                                intent.putExtra("customer_id", customer_id);
                                startActivity(intent);
                                //通过执行获取通知明细的接口来达到取消未读状态的目的
                                getNoticeDetail(whatNotice.getNotice_id());

                            }

                        }
                    });
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
        adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        showLongDialog();
        data = new ArrayList<>();
        data2 = new ArrayList<>();
        sp = getSharedPreferences("baseDate", MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
        lv_remind = (RefreshListView) findViewById(R.id.lv_remind);
        lv_remind.setOnRefreshListener(this);
        back = (TextView) findViewById(R.id.back);
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
        isLoadingMore = true;
        if (currentPageNum != totalPage) {
            getServiceData(currentPageNum + 1);
        } else {
            App.showToast(getApplicationContext(), "已加载全部!");
            lv_remind.completeRefresh();
        }

    }

    @Override
    public void onLoadingMore() {
        //上拉无效

        lv_remind.completeRefresh();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (whatItem != -1) {
            data.clear();
            Notice notice = new Notice(whatNotice.getNotice_id(), whatNotice.getNotice_type(),
                    whatNotice.getSubject(), whatNotice.getBody(),
                    whatNotice.getCreat_time(), 1, whatNotice.getExtra_id(), whatNotice.getOrg_name(),
                    whatNotice.getOrg_id(), whatNotice.getExtra_type(), whatNotice.getCustomer_id());
            for (int i = 0; i < data2.size(); i++) {
                if (i == whatItem - 1) {
                    data.add(notice);
                } else data.add(data2.get(i));
            }
            data2.clear();
            data2.addAll(data);
            adapter.notifyDataSetChanged();
        }

    }

    /**
     * 获取通知列表
     *
     * @param pageNum
     */
    public void getServiceData(int pageNum) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getnoticelist")
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
//                        Log.e("remind",response);
                        //解析数据
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        String data_status = object.get("status").getAsString();
                        String message = object.get("message").getAsString();
                        int total = 0;
                        int pageSize = 0;
                        int pageNumber = 0;
                        int totalPage = 0;
                        if (data_status.equals("ok")) {
                            List<Notice> data1 = new ArrayList<Notice>();
                            JsonObject data = object.get("data").getAsJsonObject();
                            analysisJson(pageNumber, totalPage, data1, data);
                        } else {
                            showToast(message);
                        }
                        dismissLongDialog();
                        dismissShortDialog();
                    }

                });
    }

    /**
     * 解析Json
     *
     * @param pageNumber
     * @param totalPage
     * @param data1
     * @param data
     */
    private void analysisJson(int pageNumber, int totalPage, List<Notice> data1, JsonObject data) {
        int total;
        int pageSize;
        if (!data.get("total").isJsonNull())
            total = data.get("total").getAsInt();
        if (!data.get("pageSize").isJsonNull())
            pageSize = data.get("pageSize").getAsInt();
        if (!data.get("pageNumber").isJsonNull())
            pageNumber = data.get("pageNumber").getAsInt();
        if (!data.get("totalPage").isJsonNull())
            totalPage = data.get("totalPage").getAsInt();
        JsonArray rows = data.get("rows").getAsJsonArray();
        //判断rows是否为空，是：Toast（没有更多数据了）否：显示加载的数据
        if (rows != null) {
            for (JsonElement jsonElement : rows) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                int notice_id = 0;
                String notice_type = "";
                String subject = "";
                String body = "";
                String create_time = "";
                int status = 0;
                String extra_id = "";
                String org_name = "";
                String org_id = "";
                String extra_type = "";
                String customer_id = "";
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
                if (!jsonObject.get("extra_type").isJsonNull())
                    extra_type = jsonObject.get("extra_type").getAsString();

                if (jsonObject.has("customer_id")) {
                    if (!jsonObject.get("customer_id").isJsonNull())
                        customer_id = jsonObject.get("customer_id").getAsString();
                }


                data1.add(new Notice(notice_id, notice_type, subject, body, create_time,
                        status, extra_id, org_name, org_id, extra_type, customer_id));
            }
            Message msg = Message.obtain();
            msg.what = 1;
            msg.obj = data1;
            msg.arg1 = pageNumber;
            msg.arg2 = totalPage;
            handler.sendMessage(msg);
        } else {
            App.showToast(getApplicationContext(), "已加载全部!");
        }
    }

    /**
     * 获取通知明细
     *
     * @param notice_id
     */
    private void getNoticeDetail(int notice_id) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getnoticedetail")
                .addParams("token", token)
                .addParams("notice_id", notice_id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonobject = new JsonParser().parse(response).getAsJsonObject();
                        String status = "";
                        String message = "";
                        if (!jsonobject.get("status").isJsonNull())
                            status = jsonobject.get("status").getAsString();
                        if (!jsonobject.get("message").isJsonNull())
                            message = jsonobject.get("message").getAsString();
                        if (status.equals("errow")) App.showToast(getApplicationContext(), message);
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
            if (view == null) {
                holder = new ViewHolder();
                view = View.inflate(RemindActivity.this, R.layout.zhu_shou_list_style, null);
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

            Notice not = data.get(i);
            holder.create_time.setText(not.getCreat_time().substring(0, 10));
            holder.subject.setText(not.getSubject() + "(" + not.getOrg_name() + ")");
            // holder.extra_id.setText(not.getExtra_id());
            holder.extra_id.setText(not.getCreat_time().substring(0, 10));
            holder.body.setText(not.getBody());
            holder.tv_red_tag.setVisibility(View.INVISIBLE);
            if (not.getStatus() == 0 && not.getExtra_type().equals("schedule")) {
                holder.tv_red_tag.setVisibility(View.VISIBLE);
            } else if (not.getStatus() == 1) {
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
     *
     * @param view
     */
    public void loadingMore(View view) {
        showShortDialog();
        in_loading_error.setVisibility(View.GONE);
        lv_remind.setVisibility(View.VISIBLE);
        getServiceData(1);
    }
}
