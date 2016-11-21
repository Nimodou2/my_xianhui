package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.entity.Employee;
import com.maibo.lvyongsheng.xianhui.entity.Order;
import com.maibo.lvyongsheng.xianhui.implement.DrawRoundCorner;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by LYS on 2016/10/7.
 */
public class AllMessageActivity extends Activity {
    ImageView cus_head;
    TextView cus_name,cus_grade,cus_files_num,back,tv_order;
    ListView lv_people_msg;
    SharedPreferences sp;
    String token,apiURL;
    int user_id;
    List<Order> orderList;
    ProgressDialog dialog;
    Employee employee;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            orderList=(List<Order>)msg.obj;
            dialog.dismiss();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_message);

        dialog=new ProgressDialog(this);
        dialog.setMessage("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
        dialog.show();
        sp=getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token=sp.getString("token",null);
        apiURL=sp.getString("apiURL",null);

        cus_head=(ImageView) findViewById(R.id.cus_head);
        cus_name=(TextView) findViewById(R.id.cus_name);
        cus_grade=(TextView) findViewById(R.id.cus_grade);
        cus_files_num=(TextView) findViewById(R.id.cus_files_num);
        lv_people_msg=(ListView) findViewById(R.id.lv_people_msg);
        tv_order= (TextView) findViewById(R.id.tv_order);
        back= (TextView) findViewById(R.id.back);

        Intent intent=getIntent();
        int tag=intent.getIntExtra("tag",-1);
        Bundle bundle=intent.getExtras();
        employee=(Employee) bundle.get("Employee");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到订单页面
                Intent intent = new Intent(AllMessageActivity.this, OrderActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("collOrder",(Serializable) orderList);
                //bundle.putSerializable("Employee",(Serializable) employee);
                intent.putExtra("collName",employee.getDisplay_name());
                intent.putExtras(bundle);
                intent.putExtra("tag",2);
                startActivity(intent);
            }
        });


        user_id=employee.getUser_id();
        if(tag==1){
            setColleageData(employee);
        }
        getColleagueOrder(user_id);
    }

    //设置员工详细信息
    public void setColleageData( Employee employee){
        String avator_url=employee.getAvator_url();
        String display_name=employee.getDisplay_name();
        String job=employee.getJob();
        String user_code=employee.getUser_code();
        //设置头像
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
                        Bitmap bm= DrawRoundCorner.makeRoundCorner(response,63);
                        Drawable drawable =new BitmapDrawable(bm);
                        cus_head.setImageDrawable(drawable);
                    }
                });
        cus_name.setText(display_name);
        cus_grade.setText("职务:"+job);
        cus_files_num.setText("工号:"+user_code);

        String[] name={"所属店铺","入职日期","手机号码","级别"};
        String[] msg={employee.getOrg_name(),employee.getEntry_date(),employee.getMobile(),"暂无"};
        List<Map<String,String>> lists=new ArrayList<>();
        for (int i=0;i<name.length;i++){
            Map<String,String> map=new HashMap<>();
            map.put("left",name[i]);
            map.put("right",msg[i]);
            lists.add(map);
        }
        SimpleAdapter simplead = new SimpleAdapter(this, lists,
                R.layout.style_list_detail, new String[] {"left","right"},
                new int[] {R.id.name,R.id.numbers});
        lv_people_msg.setDivider(new ColorDrawable(getResources().getColor(R.color.weixin_lianxiren_gray)));
        lv_people_msg.setDividerHeight(3);
        lv_people_msg.setAdapter(simplead);

    }
    //获取员工订单
    public void getColleagueOrder(int user_id){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/gethelperworkerschedulelist")
                .addParams("token",token)
                .addParams("user_id",user_id+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject=new JsonParser().parse(response).getAsJsonObject();
                        String statuss=jsonObject.get("status").getAsString();
                        String message=jsonObject.get("message").getAsString();
                        if (statuss.equals("ok")){
                            JsonObject data=jsonObject.get("data").getAsJsonObject();
                            JsonArray rows=data.get("rows").getAsJsonArray();
                            List<Order> list=new ArrayList<Order>();
                            for (JsonElement jsonElement:rows){
                                JsonObject jo=jsonElement.getAsJsonObject();
                                int schedule_id=jo.get("schedule_id").getAsInt();
                                int customer_id=jo.get("customer_id").getAsInt();
                                String status=jo.get("status").getAsString();
                                String start_time=jo.get("start_time").getAsString();
                                String end_time=jo.get("end_time").getAsString();
                                String engineer_id=jo.get("engineer_id").getAsString();
                                String bed_name=jo.get("bed_name").getAsString();
                                String project_code=jo.get("project_code").getAsString();
                                String project_name=jo.get("project_name").getAsString();
                                String customer_name=jo.get("customer_name").getAsString();
                                String engineer_name=jo.get("engineer_name").getAsString();

                                list.add(new Order(schedule_id, customer_id,status, start_time, end_time,  engineer_id,
                                        bed_name,  project_code,  project_name,  customer_name,  engineer_name));
                            }
                            Message msg= Message.obtain();
                            msg.what=2;
                            msg.obj=list;
                            handler.sendMessage(msg);

                        }
                    }
                });
    }
}
