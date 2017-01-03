package com.maibo.lvyongsheng.xianhui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.entity.Project;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;

import java.util.List;

import butterknife.Bind;

/**
 * Created by LYS on 2016/11/27.
 */

public class TaskPeopleDetailActivity extends BaseActivity implements View.OnClickListener{
    TextView back,people_name,tv_see;
    List<Project> datas;
    ListView lv_people_list;
    int customer_id;
    int tag;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_people_detail);
        adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        initView();
    }
    private void initView(){
        back= (TextView) findViewById(R.id.back);
        people_name= (TextView) findViewById(R.id.people_name);
        lv_people_list= (ListView) findViewById(R.id.lv_people_list);
        tv_see= (TextView) findViewById(R.id.tv_see);

        back.setOnClickListener(this);
        tv_see.setOnClickListener(this);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        datas= (List<Project>) bundle.get("details");
        people_name.setText(bundle.getString("name"));
        String customer_id_str=bundle.getString("customer_id");
        customer_id=Integer.parseInt(customer_id_str);
        tag=bundle.getInt("tag");
        if (tag==2){
            tv_see.setEnabled(true);
            tv_see.setVisibility(View.VISIBLE);
        }
        lv_people_list.setAdapter(new MyAdapter());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.tv_see:
                //跳转到客户详情界面
                Intent intent=new Intent(this,PeopleMessageActivity.class);
                intent.putExtra("customer_id",customer_id);
                startActivity(intent);
                break;
        }
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return datas.size();
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
            if (view==null){
                holder=new ViewHolder();
                view=View.inflate(TaskPeopleDetailActivity.this,R.layout.style_list_detail,null);
                holder.name= (TextView) view.findViewById(R.id.name);
                holder.numbers= (TextView) view.findViewById(R.id.numbers);
                holder.ll_all= (LinearLayout) view.findViewById(R.id.ll_all);
                view.setTag(holder);
            }else{
                holder= (ViewHolder) view.getTag();
            }
            ViewGroup.LayoutParams params=holder.ll_all.getLayoutParams();
            params.height=viewHeight*20/255;
            holder.ll_all.setLayoutParams(params);
            Project pro=datas.get(i);
            holder.name.setText(pro.getFullname());
            holder.numbers.setText(pro.getAmount());
            return view;
        }
    }
    class ViewHolder{
        TextView name,numbers;
        LinearLayout ll_all;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
