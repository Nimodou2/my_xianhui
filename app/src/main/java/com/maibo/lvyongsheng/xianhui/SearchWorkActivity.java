package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.adapter.ColleagueAdapter;
import com.maibo.lvyongsheng.xianhui.adapter.CustomerAdapter;
import com.maibo.lvyongsheng.xianhui.adapter.ProductAdapter;
import com.maibo.lvyongsheng.xianhui.adapter.ProjectAdapter;
import com.maibo.lvyongsheng.xianhui.entity.Employee;
import com.maibo.lvyongsheng.xianhui.entity.HelperCustomer;
import com.maibo.lvyongsheng.xianhui.entity.Product;
import com.maibo.lvyongsheng.xianhui.entity.Project;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.implement.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2016/11/30.
 */

public class SearchWorkActivity extends BaseActivity {
    SearchView search_people;
    ListView lv_seached_people;
    TextView back;
    SharedPreferences sp;
    String token,apiURL;
    int tag;
    int screenHeight;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    List<HelperCustomer> list=(List<HelperCustomer>) msg.obj;
                    lv_seached_people.setAdapter(new CustomerAdapter(getApplicationContext(),list,screenHeight));
                    myClickListener(list);
                    break;
                case 1:
                    List<Employee> list1= (List<Employee>) msg.obj;
                    lv_seached_people.setAdapter(new ColleagueAdapter(getApplicationContext(),list1,screenHeight));
                    myClickListener(list1);
                    break;
                case 2:
                    List<Project> list2= (List<Project>) msg.obj;
                    lv_seached_people.setAdapter(new ProjectAdapter(getApplicationContext(),list2,screenHeight));
                    myClickListener(list2);
                    break;
                case 3:
                    List<Product> list3= (List<Product>) msg.obj;
                    lv_seached_people.setAdapter(new ProductAdapter(getApplicationContext(),list3,screenHeight));
                    myClickListener(list3);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_work);

        ViewGroup.LayoutParams params=ll_head.getLayoutParams();
        params.height=((Util.getScreenHeight(this)-getStatusBarHeight())/35)*3;
        ll_head.setLayoutParams(params);
        CloseAllActivity.getScreenManager().pushActivity(this);
        screenHeight= Util.getScreenHeight(this)-getStatusBarHeight();
        initView();
    }

    private void initView() {
        sp=getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token=sp.getString("token",null);
        apiURL=sp.getString("apiURL",null);
        Intent intent=getIntent();
        tag=intent.getIntExtra("tag",-10);

        search_people= (SearchView) findViewById(R.id.search_people);
        updateSearchViewStyle();
        back= (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        lv_seached_people= (ListView) findViewById(R.id.lv_seached_people);
        searchData();
    }

    /**
     * 修改SearchView样式
     */
    private void updateSearchViewStyle() {
        int id = search_people.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView hintText= (TextView) search_people.findViewById(id);
        hintText.setTextSize(14);
        hintText.setTextColor(Color.BLACK);
        Class<?> c=search_people.getClass();
        try {
            Field f=c.getDeclaredField("mSearchPlate");//通过反射，获得类对象的一个属性对象
            f.setAccessible(true);//设置此私有属性是可访问的
//                View v=(View) f.get(search_people);//获得属性的值
//                v.setBackgroundResource(R.drawable.searchview_shap_all_white_bg);//设置此view的背景
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void myClickListener(final List<?> data){
        lv_seached_people.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=null;
                if (tag==0){
                    intent=new Intent(SearchWorkActivity.this, PeopleMessageActivity.class);
                    List<HelperCustomer> customers=(List<HelperCustomer>)data;
                    intent.putExtra("customer_id",customers.get(i).getCustomer_id());
                }else if (tag==1){
                    intent=new Intent(SearchWorkActivity.this, AllMessageActivity.class);
                    List<Employee> colleagues=(List<Employee>)data;
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("Employee",colleagues.get(i));
                    intent.putExtras(bundle);
                    intent.putExtra("tag",1);
                }else if (tag==2){
                    intent=new Intent(SearchWorkActivity.this, ProjectMessageActivity.class);
                    List<Project> project=(List<Project>)data;
                    intent.putExtra("project_id",project.get(i).getProject_id());
                    intent.putExtra("projectName",project.get(i).getProject_name());
                }else if (tag==3){
                    intent=new Intent(SearchWorkActivity.this, ProductMessageActivity.class);
                    List<Product> products=(List<Product>)data;
                    intent.putExtra("Item_id",products.get(i).getItem_id());
                    intent.putExtra("productName",products.get(i).getFullname());
                }
                startActivity(intent);

            }
        });
    }

    /**
     * 监听searchView变化
     */
    private void searchData() {
        search_people.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {//当点击搜索按钮时执行
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {//当搜索框内容变化时执行
                //进行数据查询
                if (!TextUtils.isEmpty(newText)){
                    if (tag==0){
                        searchCustomer(newText);
                    }else if (tag==1){
                        searchColleague(newText);
                    }else if (tag==2){
                        searchProject(newText);
                    }else if (tag==3){
                        searchProduct(newText);
                    }
                }
                return false;
            }
        });
    }

    /**
     * 顾客查询
     */
    public void searchCustomer(String keyword){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/gethelpercustomerlist")
                .addParams("token",token)
                .addParams("pageSize",10000+"")
                .addParams("pageNumber",1+"")
                .addParams("keyword",keyword)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String msg_status=jsonObject.get("status").getAsString();
                        String message=jsonObject.get("message").getAsString();
                        if (msg_status.equals("ok")) {
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            int pageNumber=-1;
                            int totalPage=-1;
                            if (!data.get("pageNumber").isJsonNull())
                                pageNumber=data.get("pageNumber").getAsInt();
                            if (!data.get("totalPage").isJsonNull())
                                totalPage=data.get("totalPage").getAsInt();
                            JsonArray rows = data.get("rows").getAsJsonArray();
                            if (rows != null) {
                                List<HelperCustomer> list = new ArrayList<HelperCustomer>();
                                for (JsonElement jsonElement : rows) {
                                    JsonObject job = jsonElement.getAsJsonObject();
                                    String org_name ="";
                                    int org_id=-1;
                                    String vip_star="";
                                    int customer_id =-1;
                                    String fullname="";
                                    String avator_url ="";
                                    String last_consume_time="";
                                    String guid ="";
                                    String days ="";
                                    int project_total=0;
                                    String schedule_date="";
                                    String schedule_time="";
                                    int status =-1;
                                    if (!job.get("org_name").isJsonNull())
                                        org_name = job.get("org_name").getAsString();
                                    if (!job.get("org_id").isJsonNull())
                                        org_id = job.get("org_id").getAsInt();
                                    if (!job.get("vip_star").isJsonNull())
                                        vip_star=job.get("vip_star").getAsString();
                                    if (!job.get("customer_id").isJsonNull())
                                        customer_id = job.get("customer_id").getAsInt();
                                    if (!job.get("fullname").isJsonNull())
                                        fullname = job.get("fullname").getAsString();
                                    if (!job.get("avator_url").isJsonNull())
                                        avator_url = job.get("avator_url").getAsString();
                                    if (!job.get("last_consume_time").isJsonNull())
                                        last_consume_time=job.get("last_consume_time").getAsString();
                                    if (!job.get("guid").isJsonNull())
                                        guid = job.get("guid").getAsString();
                                    if (!job.get("days").isJsonNull())
                                        days = job.get("days").getAsString();
                                    if (!job.get("project_total").isJsonNull())
                                        project_total=job.get("project_total").getAsInt();
                                    if (!job.get("schedule_date").isJsonNull())
                                        schedule_date=job.get("schedule_date").getAsString();
                                    if (!job.get("schedule_time").isJsonNull())
                                        schedule_time=job.get("schedule_time").getAsString();
                                    if (!job.get("status").isJsonNull())
                                        status = job.get("status").getAsInt();
                                    list.add(new HelperCustomer(org_name, vip_star, customer_id, fullname, avator_url, days, project_total, status));
                                }
                                Message msg = Message.obtain();
                                msg.what = 0;
                                msg.obj = list;
                                msg.arg1=pageNumber;
                                msg.arg2=totalPage;
                                handler.sendMessage(msg);
                            }
                        }else{

                        }
                    }
                });
    }

    /**
     * 同事查询
     */
    public void searchColleague(String keyword){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/gethelperworkerlist")
                .addParams("token",token)
                .addParams("keyword",keyword)
                .addParams("pageSize",10000+"")
                .addParams("pageNumber",1+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        //Log.e("同事:",response);
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String msg_status=jsonObject.get("status").getAsString();
                        String message=jsonObject.get("message").getAsString();
                        if (msg_status.equals("ok")){
                            JsonObject data=jsonObject.get("data").getAsJsonObject();
                            int pageNumber=-1;
                            int totalPage=-1;
                            if (!data.get("pageNumber").isJsonNull())
                                pageNumber=data.get("pageNumber").getAsInt();
                            if (!data.get("totalPage").isJsonNull())
                                totalPage=data.get("totalPage").getAsInt();
                            JsonArray rows = data.get("rows").getAsJsonArray();
                            if (rows!=null){
                                List<Employee> list=new ArrayList<>();
                                for (JsonElement jsonElement:rows){
                                    JsonObject jo=jsonElement.getAsJsonObject();
                                    String job="";
                                    boolean user_level=false;
                                    String mobile="";
                                    String user_code="";
                                    String entry_date="";
                                    String org_name="";
                                    int org_id=-1;
                                    int user_id=-1;
                                    String display_name="";
                                    String avator_url="";
                                    String guid="";
                                    int status=-1;
                                    Double project_hours=-1.00;
                                    int project_qty=-1;
                                    String schedule_time="";
                                    if (!jo.get("job").isJsonNull())
                                        job=jo.get("job").getAsString();
                                    if (!jo.get("user_level").isJsonNull())
                                        user_level=jo.get("user_level").getAsBoolean();
                                    if (!jo.get("mobile").isJsonNull())
                                        mobile=jo.get("mobile").getAsString();
                                    if (!jo.get("user_code").isJsonNull())
                                        user_code=jo.get("user_code").getAsString();
                                    if (!jo.get("entry_date").isJsonNull())
                                        entry_date=jo.get("entry_date").getAsString();
                                    if (!jo.get("org_name").isJsonNull())
                                        org_name=jo.get("org_name").getAsString();
                                    if (!jo.get("org_id").isJsonNull())
                                        org_id=jo.get("org_id").getAsInt();
                                    if (!jo.get("user_id").isJsonNull())
                                        user_id=jo.get("user_id").getAsInt();
                                    if (!jo.get("display_name").isJsonNull())
                                        display_name=jo.get("display_name").getAsString();
                                    if (!jo.get("avator_url").isJsonNull())
                                        avator_url=jo.get("avator_url").getAsString();
                                    if (!jo.get("guid").isJsonNull())
                                        guid=jo.get("guid").getAsString();
                                    if (!jo.get("status").isJsonNull())
                                        status=jo.get("status").getAsInt();
                                    if (!jo.get("project_hours").isJsonNull())
                                        project_hours=jo.get("project_hours").getAsDouble();
                                    if (!jo.get("project_qty").isJsonNull())
                                        project_qty=jo.get("project_qty").getAsInt();
                                    if (!jo.get("schedule_time").isJsonNull())
                                        schedule_time=jo.get("schedule_time").getAsString();

                                    list.add(new Employee(user_id,display_name,avator_url,job,user_level,mobile,user_code,
                                            entry_date,org_name,status,project_hours,project_qty,schedule_time));
                                }
                                Message msg=Message.obtain();
                                msg.what=1;
                                msg.obj=list;
                                msg.arg1=pageNumber;
                                msg.arg2=totalPage;
                                handler.sendMessage(msg);
                            }
                        }else{

                        }
                    }
                });
    }

    /**
     * 项目查询
     */
    public void searchProject(String keyword){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/gethelperprojectlist")
                .addParams("token",token)
                .addParams("pageSize",10000+"")
                .addParams("pageNumber",1+"")
                .addParams("keyword",keyword)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();

                        String msg_status=jsonObject.get("status").getAsString();
                        String message=jsonObject.get("message").getAsString();
                        if (msg_status.equals("ok")){
                            JsonObject data=jsonObject.get("data").getAsJsonObject();
                            JsonArray rows = data.get("rows").getAsJsonArray();
                            int pageNumber=-1;
                            int totalPage=-1;
                            if (!data.get("pageNumber").isJsonNull())
                                pageNumber=data.get("pageNumber").getAsInt();
                            if (!data.get("totalPage").isJsonNull())
                                totalPage=data.get("totalPage").getAsInt();
                            if (rows!=null){
                                List<Project> list=new ArrayList<>();
                                for (JsonElement jsonElement:rows){
                                    JsonObject jo=jsonElement.getAsJsonObject();
                                    String avator_url="";
                                    String schedule_num="0";
                                    String paid_num="0";
                                    String org_name="全公司";
                                    int project_id = 0;
                                    String project_name="";
                                    if (!jo.get("schedule_num").isJsonNull()){
                                        schedule_num=jo.get("schedule_num").getAsString();
                                    }
                                    if(!jo.get("paid_num").isJsonNull()){
                                        paid_num=jo.get("paid_num").getAsString();
                                    }
                                    if(!jo.get("org_name").isJsonNull()){
                                        org_name=jo.get("org_name").getAsString();
                                    }
                                    if (!jo.get("avator_url").isJsonNull()){
                                        avator_url=jo.get("avator_url").getAsString();
                                    }
                                    if(!jo.get("project_id").isJsonNull()){
                                        project_id = jo.get("project_id").getAsInt();
                                    }
                                    if(!jo.get("project_name").isJsonNull()){
                                        project_name=jo.get("project_name").getAsString();
                                    }

                                    list.add(new Project(avator_url,project_id,schedule_num,paid_num,project_name,org_name));
                                }
                                Message msg=Message.obtain();
                                msg.what=2;
                                msg.obj=list;
                                msg.arg1=pageNumber;
                                msg.arg2=totalPage;
                                handler.sendMessage(msg);
                               }
                        }else{

                        }
                    }
                });
    }

    /**
     * 产品查询
     */
    public void searchProduct(String keyword){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/gethelperproductlist")
                .addParams("token",token)
                .addParams("pageSize",10000+"")
                .addParams("pageNumber",1+"")
                .addParams("keyword",keyword)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String msg_status=jsonObject.get("status").getAsString();
                        String message=jsonObject.get("message").getAsString();
                        if (msg_status.equals("ok")){
                            JsonObject data=jsonObject.get("data").getAsJsonObject();
                            int pageNumber=-1;
                            int totalPage=-1;
                            if (!data.get("pageNumber").isJsonNull())
                                pageNumber=data.get("pageNumber").getAsInt();
                            if (!data.get("totalPage").isJsonNull())
                                totalPage=data.get("totalPage").getAsInt();
                            JsonArray rows = data.get("rows").getAsJsonArray();
                            List<Product> list=new ArrayList<>();
                            int item_id=0;
                            String fullname="";
                            String buy_qty="0";
                            String buy_num="0";
                            String stock_qty="0";
                            String avator_url="";
                            String org_name="全公司";
                            for (JsonElement jsonElement:rows){
                                JsonObject jo=jsonElement.getAsJsonObject();
                                if(!jo.get("item_id").isJsonNull()){
                                    item_id=jo.get("item_id").getAsInt();
                                }
                                if(!jo.get("item_name").isJsonNull()){
                                    fullname=jo.get("item_name").getAsString();
                                }
                                if(!jo.get("buy_qty").isJsonNull()){
                                    buy_qty=jo.get("buy_qty").getAsString();
                                }
                                if(!jo.get("buy_num").isJsonNull()){
                                    buy_num=jo.get("buy_num").getAsString();
                                }
                                if(!jo.get("stock_qty").isJsonNull()){
                                    stock_qty=jo.get("stock_qty").getAsString();
                                }
                                if(!jo.get("avator_url").isJsonNull()){
                                    avator_url=jo.get("avator_url").getAsString();
                                }
                                if (!jo.get("org_name").isJsonNull()){
                                    org_name=jo.get("org_name").getAsString();
                                }
                                list.add(new Product(avator_url,item_id,fullname,buy_qty,buy_num,stock_qty,org_name));
                            }
                            Message msg=Message.obtain();
                            msg.what=3;
                            msg.obj=list;
                            msg.arg1=pageNumber;
                            msg.arg2=totalPage;
                            handler.sendMessage(msg);
                        }else{
                        }
                    }
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
