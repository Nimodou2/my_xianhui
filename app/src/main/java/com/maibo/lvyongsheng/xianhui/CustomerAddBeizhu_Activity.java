package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.maibo.lvyongsheng.xianhui.adapter.CustomerAddBeizhu_XRecyclerviewAdapter;
import com.maibo.lvyongsheng.xianhui.entity.BeizhuListBean;
import com.maibo.lvyongsheng.xianhui.helperutils.MyItemTouchHelper;
import com.maibo.lvyongsheng.xianhui.helperutils.MyItemTouchHelperCallBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerAddBeizhu_Activity extends AppCompatActivity {
    private static  final String TAG="AddBeizhu_Activity";
    private static final int requestcode=0;
    private RequestQueue requestQueue;
    private TextView text_back;
    private TextView text_title;
    private RecyclerView xrecyclerview;
    private CustomerAddBeizhu_XRecyclerviewAdapter adapter;
    private TextView text_change;
    private boolean doneFirst=true;
    private boolean isFirstClick=true;
    private SharedPreferences sp;
    private String token;
    private String apiURL;
    private int customer_id;
    private CustomerAddBeizhu_XRecyclerviewAdapter.GetChange getChange;
    private boolean isChange=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_add_beizhu_);
        initview();

    }

    private void initview() {
        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token = sp.getString("token", null);
        apiURL = sp.getString("apiURL", null);
        requestQueue=((App)getApplication()).getRequestQueue();
        customer_id=getIntent().getIntExtra("customer_id",-1);
        Log.e(TAG,"回传结果"+token +"    "+customer_id);
        text_back = (TextView) findViewById(R.id.activity_customer_add_beizhu_text_back);
        text_title = (TextView) findViewById(R.id.activity_customer_add_beizhu_text_title);
        text_change = (TextView) findViewById(R.id.activity_customer_add_beizhu_text_change);
        xrecyclerview= (RecyclerView) findViewById(R.id.activity_customer_add_beizhu_xrecyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        xrecyclerview.setLayoutManager(linearLayoutManager);
       /* xrecyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xrecyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xrecyclerview.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);*/
        //MyRefreshHeadView myRefreshHeadView = new MyRefreshHeadView(this);
        //myRefreshHeadView.setArrowImageView(R.mipmap.indicator_arrow);
        //xrecyclerview.setRefreshHeader(myRefreshHeadView);
       // xrecyclerview.setItemAnimator(new DefaultItemAnimator());

        MyItemTouchHelper itemTouchHelper = new MyItemTouchHelper(new MyItemTouchHelperCallBack.OnItemTouchCallbackListener() {
            @Override
            public void onSwiped(int adapterPosition) {
              //这里是滑动删除操作
            }
            //移动条目立马更新数据，如果条目静止，并把标识改为条目以变化
            @Override
            public boolean onMove(int srcPosition, int targetPosition) {
                //这里是拖拽换序
                if (adapter.getList_all() != null) {
                    // 更换数据源中的数据Item的位置
                    Collections.swap(adapter.getList_all(), srcPosition, targetPosition);
                    // 更新UI中的Item的位置，主要是给用户看到交互效果
                    adapter.notifyItemMoved(srcPosition, targetPosition);
                    //拿到数据就行网络更新
                    isChange=true;
                    return true;
                }
                return false;
            }
            //判断当前的手势状态 如果是该条目静止 可以加载网络了，并设置加载标识为false
            @Override
            public void onSelectchange(int actionState) {
                if (isChange&&actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                    changeListXX();
                    isChange=false;
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(xrecyclerview);

        //是否可以拖拽
        itemTouchHelper.setDragEnable(true);
        //是否可以滑动删除
        itemTouchHelper.setSwipeEnable(false);



        View emptyView = LayoutInflater.from(this).inflate(R.layout.incloud_no_datas, null);
        //xrecyclerview.setEmptyView(emptyView);
        Log.e(TAG,"这里是回传结果");
        StringRequest stringRequest=new StringRequest(Request.Method.POST, apiURL + "/rest/employee/getcustomerextlist", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if(s!=null){
                    BeizhuListBean beizhuListBean= new Gson().fromJson(s,BeizhuListBean.class);
                    if(beizhuListBean.getData()!=null){
                        adapter=new CustomerAddBeizhu_XRecyclerviewAdapter(CustomerAddBeizhu_Activity.this,beizhuListBean.getData());
                        adapter.setCustomer_id(customer_id);
                        xrecyclerview.setAdapter(adapter);
                        Log.e(TAG,"这个是数据大小"+s);
                    }else {
                        List<BeizhuListBean.DataBean> konglist=new ArrayList<>();
                        adapter=new CustomerAddBeizhu_XRecyclerviewAdapter(CustomerAddBeizhu_Activity.this,konglist);
                        adapter.setCustomer_id(customer_id);
                        xrecyclerview.setAdapter(adapter);
                        Log.e(TAG,"这个是数据大小"+s);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                if(customer_id!=-1){
                    map.put("token",token);
                    map.put("customer_id",customer_id+"");
                }
                return map;
            }
        };
        requestQueue.add(stringRequest);

        text_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFirstClick) {
                    adapter.setHaveFoot(doneFirst, getChange = new CustomerAddBeizhu_XRecyclerviewAdapter.GetChange() {
                       //这个是判断是否点击了编辑 ，然后添加一个空的视图，以添加新的备注
                        @Override
                        public void getChangeResult(boolean result) {
                            doneFirst = !result;
                            Log.e("回调结果","  回调 "+doneFirst);
                            isFirstClick=false;
                            if(result){
                                text_back.setText("撤销");
                                text_title.setText("编辑");
                                text_change.setText("保存");
                            }else {
                                text_back.setText("返回");
                                text_title.setText("顾客信息");
                                text_change.setText("编辑");
                            }
                        }
                    });

                }else {
                    adapter.setHaveFoot(doneFirst,getChange);
                }

            }
        });

        text_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //initXrListener();

        }
   /* private void initXrListener() {
        xrecyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
                xrecyclerview.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                //下载更多
                xrecyclerview.loadMoreComplete();
            }
        });
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        adapter= (CustomerAddBeizhu_XRecyclerviewAdapter) xrecyclerview.getAdapter();
        if(adapter!=null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, apiURL + "/rest/employee/getcustomerextlist", new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    if (s!= null) {
                            BeizhuListBean beizhuListBean = new Gson().fromJson(s, BeizhuListBean.class);
                            adapter.setList_all(beizhuListBean.getData());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    if (customer_id != -1) {
                        map.put("token", token);
                        map.put("customer_id", customer_id + "");
                    }
                    return map;
                }
            };
            requestQueue.add(stringRequest);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //不可见的时候把adapter 重置
        if(adapter!=null){
            adapter =new CustomerAddBeizhu_XRecyclerviewAdapter(this,adapter.getList_all());
            xrecyclerview.setAdapter(adapter);
        }
    }
    //切换位置之后重新请求网络数据，并在返回值后刷新adapter
    public void changeListXX(){
        StringRequest request=new StringRequest(Request.Method.POST, apiURL + "/rest/employee/changecustomerextorder", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response!=null){
                    Log.e(TAG,response);
                    adapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("token",token);
                List<BeizhuListBean.DataBean> list=adapter.getList_all();
                String file_id="";
                for(int i=0;i<list.size();i++){
                    file_id=file_id+list.get(i).getField_id()+",";
                }
                file_id=file_id.substring(0,file_id.lastIndexOf(","));
                Log.e(TAG,file_id);
                map.put("field_list",file_id);
                return map;
            }
        };
        requestQueue.add(request);
    }
}
