package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.maibo.lvyongsheng.xianhui.entity.Custemer;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.implement.MyProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2016/10/10.
 */
public class CustomerManagerActivity extends BaseActivity implements View.OnClickListener {
    SharedPreferences sp;
    String token, apiURL;
    int customer_id;
    List<Custemer> list1;
    MyAdapter adapter;
    ListView lv_guwen;
    int[] buffer;
    TextView back, tv_update_time;
    MyProgressDialog myDialog;

    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.tv_spacing1)
    TextView tv_spacing1;
    @Bind(R.id.tv_spacing2)
    TextView tv_spacing2;
    @Bind(R.id.ll_change_date)
    LinearLayout ll_change_date;
    @Bind(R.id.ll_all_data)
    LinearLayout ll_all_data;

    @Bind(R.id.in_no_datas)
    LinearLayout in_no_datas;
    @Bind(R.id.in_loading_error)
    LinearLayout in_loading_error;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 0:
                    ll_all_data.setVisibility(View.GONE);
                    in_loading_error.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    ll_all_data.setVisibility(View.VISIBLE);
                    in_loading_error.setVisibility(View.GONE);
                    setCustomerManagerAdapter(msg);
                    break;
            }
        }
    };

    /**
     * 设置顾问选项
     * @param msg
     */
    private void setCustomerManagerAdapter(Message msg) {
        list1 = (List<Custemer>) msg.obj;
        //初始化buffer
        buffer = new int[list1.size()];
        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i).getSelected())
                buffer[i] = 1;
            else buffer[i] = 0;
        }
        adapter = new MyAdapter();
        lv_guwen.setAdapter(adapter);
        tv_update_time.setText(list1.get(0).getUpdate_time());
        lv_guwen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ImageView tv_checked = (ImageView) view.findViewById(R.id.iv_checked);
                if (buffer[i] == 0) {
                    tv_checked.setVisibility(View.VISIBLE);
                    for (int j = 0; j < buffer.length; j++) {
                        if (buffer[j] == 1) {
                            View v = adapterView.getChildAt(j);
                            ImageView checked = (ImageView) v.findViewById(R.id.iv_checked);
                            checked.setVisibility(View.INVISIBLE);
                            buffer[j] = 0;
                        }

                    }
                    buffer[i] = 1;
                } else {
                    if (buffer[i] != 1)
                        tv_checked.setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_manager);
        adapterLitterBar(ll_head);
        initHeightAndWidth();
        CloseAllActivity.getScreenManager().pushActivity(this);
        myDialog = new MyProgressDialog(this);
        myDialog.show();

        lv_guwen = (ListView) findViewById(R.id.lv_guwen);
        back = (TextView) findViewById(R.id.back);
        tv_update_time = (TextView) findViewById(R.id.tv_update_time);
        back.setOnClickListener(this);

        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token = sp.getString("token", null);
        apiURL = sp.getString("apiURL", null);
        Intent intent = getIntent();
        customer_id = intent.getIntExtra("customer_id", -1);
        String name = intent.getStringExtra("customer_name");
        if (customer_id!=-1)
            getAdviserList(customer_id);
        else {
            showToast(R.string.data_error);
            myDialog.dismiss();
        }
    }

    /**
     * 初始化布局宽高
     */
    private void initHeightAndWidth() {
        View views[]=new View[3];
        views[0]=tv_spacing1;
        views[1]=ll_change_date;
        views[2]=tv_spacing2;
        int height[]=new int[3];
        height[0]=viewHeight*15/255;
        height[1]=viewHeight*20/255;
        height[2]=viewHeight*15/255;
        int width[]=null;
        setViewHeightAndWidth(views,height,width);
    }

    public void getAdviserList(int customer_id) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getcustomeradviserlist")
                .addParams("token", token)
                .addParams("customer_id", customer_id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Message msg=Message.obtain();
                        msg.what=0;
                        handler.sendMessage(msg);
                        myDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //Log.e("顾问",response);
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String msg_status = jsonObject.get("status").getAsString();
                        String message = jsonObject.get("message").getAsString();
                        if (msg_status.equals("ok")) {
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            String update_time = "";
                            if (!data.get("update_time").isJsonNull())
                                update_time = data.get("update_time").getAsString();
                            JsonArray rows = data.get("rows").getAsJsonArray();
                            List<Custemer> list = new ArrayList<Custemer>();
                            for (JsonElement jsonElement : rows) {
                                JsonObject jo = jsonElement.getAsJsonObject();
                                int user_id = -1;
                                String display_name = "";
                                String avator_url = "";
                                String guid = "";
                                if (!jo.get("user_id").isJsonNull())
                                    user_id = jo.get("user_id").getAsInt();
                                if (!jo.get("display_name").isJsonNull())
                                    display_name = jo.get("display_name").getAsString();
                                if (!jo.get("avator_url").isJsonNull())
                                    avator_url = jo.get("avator_url").getAsString();
                                if (!jo.get("guid").isJsonNull())
                                    guid = jo.get("guid").getAsString();
                                boolean selected = jo.get("selected").getAsBoolean();
                                list.add(new Custemer(user_id, display_name, avator_url, guid, selected, update_time));
                            }
                            Message msg = Message.obtain();
                            msg.what=1;
                            msg.obj = list;
                            handler.sendMessage(msg);
                        } else {
                            App.showToast(getApplication(), message);
                        }
                        myDialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View view) {

        finish();
    }


    class MyAdapter extends BaseAdapter {

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
            View v = View.inflate(getApplicationContext(), R.layout.style_choose_adviser, null);
            TextView tv_adviser_name = (TextView) v.findViewById(R.id.tv_adviser_name);
            LinearLayout ll_choose_adviser= (LinearLayout) v.findViewById(R.id.ll_choose_adviser);
            setSingleViewHeightAndWidth(ll_choose_adviser,viewHeight*20/255,0);
            tv_adviser_name.setText(list1.get(i).getFullname());
            final ImageView iv_checked = (ImageView) v.findViewById(R.id.iv_checked);
            if (list1.get(i).getSelected()) {
                iv_checked.setVisibility(View.VISIBLE);
            }
            return v;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        //保存更改的顾问
        int clickWhat = 0;
        if (buffer!=null){
            for (int i = 0; i < buffer.length; i++) {
                if (buffer[i] == 1) {
                    clickWhat = i;
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("adviserName", list1.get(i).getFullname());
                    editor.commit();
                }
            }

            setCustomerAdviser(clickWhat);
        }

    }

    public void setCustomerAdviser(int clickWhat) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/setcustomeradviser")
                .addParams("token", token)
                .addParams("customer_id", customer_id + "")
                .addParams("adviser", list1.get(clickWhat).getCustomer_id() + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("保存:", response);
                    }
                });
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
        myDialog.show();
        if (customer_id!=-1)
            getAdviserList(customer_id);
        else {
            showToast(R.string.data_error);
            myDialog.dismiss();
        }
    }
}
