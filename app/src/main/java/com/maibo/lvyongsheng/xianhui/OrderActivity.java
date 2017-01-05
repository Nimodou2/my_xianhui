package com.maibo.lvyongsheng.xianhui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.adapter.OrderAdapter;
import com.maibo.lvyongsheng.xianhui.entity.Employee;
import com.maibo.lvyongsheng.xianhui.entity.Order;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;

import java.util.List;

import butterknife.Bind;


/**
 * Created by LYS on 2016/10/9.
 */
public class OrderActivity extends BaseActivity implements View.OnClickListener{
    ListView lv_order;
    TextView cus_name,tv_files,back;
    LinearLayout tv_notdata;
    List<Order> productOrder,projectOrder,collOrder,customerOrder;
    int project_id;
    int tag;
    int item_id;
    Employee employee;
    int customer_id;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        lv_order=(ListView)findViewById(R.id.lv_order);
        cus_name=(TextView) findViewById(R.id.cus_name);
        tv_files=(TextView) findViewById(R.id.tv_files);
        tv_notdata=(LinearLayout) findViewById(R.id.tv_notdata);
        back= (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_files.setOnClickListener(this);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();

        productOrder=(List<Order>) bundle.get("productOrder");
        projectOrder=(List<Order>) bundle.get("projectOrder");
        collOrder= (List<Order>)bundle.get("collOrder");
        customerOrder=(List<Order>) bundle.get("customerOrder");
        customer_id=intent.getIntExtra("customerID",-1);
        employee=(Employee) bundle.get("Employee");
        tag=intent.getIntExtra("tag",-1);
        project_id=intent.getIntExtra("project_id",-1);
        item_id=intent.getIntExtra("item_id",-1);
        if (tag==0){
            if(productOrder.size()>0){
                cus_name.setText(productOrder.get(0).getItem_name());

                lv_order.setAdapter(new OrderAdapter(this,productOrder,0,viewHeight));
            }else{
                cus_name.setText(intent.getStringExtra("productName"));
                tv_notdata.setVisibility(View.VISIBLE);
            }

        }else if(tag==1){
            if(projectOrder.size()>0){
                cus_name.setText(projectOrder.get(0).getProject_name());
                lv_order.setAdapter(new OrderAdapter(this,projectOrder,1,viewHeight));
            }else{
                cus_name.setText(intent.getStringExtra("projectName"));
                tv_notdata.setVisibility(View.VISIBLE);
            }
        }else if (tag==2){
            if(collOrder.size()>0){
                cus_name.setText(intent.getStringExtra("collName"));
                lv_order.setAdapter(new OrderAdapter(this,collOrder,1,viewHeight));
            }else{
                cus_name.setText(intent.getStringExtra("collName"));
                tv_notdata.setVisibility(View.VISIBLE);
            }
        }else if(tag==3){
            if(customerOrder.size()>0){
                cus_name.setText(bundle.getString("customer_name"));
                lv_order.setAdapter(new OrderAdapter(this,customerOrder,1,viewHeight));
            }else{
                cus_name.setText(bundle.getString("customer_name"));
                tv_notdata.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        // productOrder.get(0).getItem_id();
        if (tag==0){
            Intent intent=new Intent(OrderActivity.this, ProductMessageActivity.class);
            intent.putExtra("Item_id",item_id);
            startActivity(intent);


        }else if (tag==1){
            Intent intent=new Intent(OrderActivity.this, ProjectMessageActivity.class);
            intent.putExtra("project_id",project_id);
            if (project_id!=-1){
                startActivity(intent);
            }else{
                App.showToast(getApplicationContext(),"无数据");
            }

        }else if (tag==2){
                Intent intent = new Intent(getApplicationContext(), AllMessageActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("Employee",employee);
                intent.putExtras(bundle);
                intent.putExtra("tag",1);
            if (!employee.getJob().equals("")&&employee.getJob()!=null){
                startActivity(intent);
            }else{
                App.showToast(getApplicationContext(),"无数据");
            }
        }else if(tag==3){
                Intent intent=new Intent(getApplicationContext(), PeopleMessageActivity.class);
                intent.putExtra("customer_id",customer_id);
                startActivity(intent);

        }


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
