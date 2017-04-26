package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.maibo.lvyongsheng.xianhui.adapter.BeizhuConversationAdapter;
import com.maibo.lvyongsheng.xianhui.entity.BeizhuDetailBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeizhuConversationActivity extends AppCompatActivity {
    private static final String TAG=" BeizhuConversation";
    private RecyclerView listview;
    private EditText edittext;
    private ImageView send;
    private BeizhuConversationAdapter adapter;
    private BeizhuDetailBean beizhudetailbean;
    private SharedPreferences sp;
    private LinearLayout linearlayout_data;
    private Intent intent;
    private TextView text_back;
    private String apiURL;
    private String token;
    private int field_id;
    private RequestQueue requestQueue;
    private String uid;
    private String thisuid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beizhu_conversation);
        initview();
    }

    private void initview() {
        requestQueue=((App)getApplication()).getRequestQueue();
        sp=getSharedPreferences("baseDate", MODE_PRIVATE);
        apiURL=sp.getString("apiURL", null);
        token=sp.getString("token", null);
        uid=sp.getString("guid","99999");
        thisuid=uid.substring(uid.lastIndexOf("_")+1);
        listview = (RecyclerView) findViewById(R.id.activity_beizhu_conversation_list);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listview.setLayoutManager(linearLayoutManager);
        edittext = (EditText) findViewById(R.id.activity_beizhu_conversation_edit);
        send = (ImageView) findViewById(R.id.activity_beizhu_conversation_image);
        linearlayout_data = (LinearLayout) findViewById(R.id.activity_beizhu_conversation_nodata);
        text_back= (TextView) findViewById(R.id.activity_beizhu_conversation_text_back);
        intent=getIntent();
        beizhudetailbean= (BeizhuDetailBean) intent.getSerializableExtra("bean");
        field_id=intent.getIntExtra("field_id",-1);
        if(beizhudetailbean!=null){
            if(beizhudetailbean.getData()!=null&&beizhudetailbean.getData().size()>0){
                Log.e(TAG,"这个是uid   "+uid);
                adapter=new BeizhuConversationAdapter(this,beizhudetailbean,Integer.parseInt(thisuid));
                listview.setAdapter(adapter);
                listview.smoothScrollToPosition(adapter.getItemCount()-1);
            }else {
                linearlayout_data.setVisibility(View.VISIBLE);
                adapter=new BeizhuConversationAdapter(this,null,Integer.parseInt(thisuid));
                listview.setAdapter(adapter);
            }
        }
        text_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String content=edittext.getText().toString().trim();
                    if(content!=null&&content.length()>0){
                        setNewBeizhuDetail(content);
                        View view = getWindow().peekDecorView();
                        if (view != null) {
                            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        edittext.setText("");
                        edittext.clearFocus();
                    }else {
                        Toast.makeText(BeizhuConversationActivity.this,"输入内容不能为空哦",Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }
    public void setNewBeizhuDetail(final String content){
        if(apiURL!=null&&token!=null&&field_id!=-1) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, apiURL + "/rest/employee/setcustomerextvalue", new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    if (s != null) {
                        Log.e(TAG,"这个是消息的回传"+s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if(jsonObject.getString("status").equals("ok")){
                                Log.e(TAG,"添加成功");
                                //添加成功就重新加载，并把新的数据加入
                                downNewBeizhuDetail();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                    {
                        map.put("token", token);
                        map.put("field_value", content);
                        map.put("field_id", field_id+"");
                    }
                    return map;
                }
            };
            requestQueue.add(stringRequest);
        }
    }
    public void downNewBeizhuDetail(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiURL + "/rest/employee/getcustomerextdetail", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s != null) {
                   BeizhuDetailBean thisbeizhudetailbean=new Gson().fromJson(s,BeizhuDetailBean.class);
                    if(adapter!=null&&adapter.getData()!=null){
                        List<BeizhuDetailBean.DataBean> thisdatabean=new ArrayList<>();
                        for(int i=adapter.getBeizhuDatalistSize();i<thisbeizhudetailbean.getData().size();i++){
                            thisdatabean.add(thisbeizhudetailbean.getData().get(i));
                        }
                        adapter.setBeizhuDetailData(thisdatabean);
                      //  listview.setSelection(adapter.getCount()-1);
                        listview.smoothScrollToPosition(adapter.getItemCount()-1);
                    }else {
                        adapter.setData(thisbeizhudetailbean);
                       // listview.setSelection(adapter.getCount()-1);
                        listview.smoothScrollToPosition(adapter.getItemCount()-1);
                        linearlayout_data.setVisibility(View.GONE);
                    }
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
                {
                    map.put("token",token);
                    map.put("field_id", field_id+"");
                }
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adapter!=null){
            adapter=null;
            Log.e(TAG,"资源释放了");
        }
    }
}
