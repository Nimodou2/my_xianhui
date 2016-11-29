package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.entity.Project;

import java.util.List;

/**
 * Created by LYS on 2016/11/27.
 */

public class TaskPeopleDetailActivity extends Activity {
    TextView back,people_name;
    List<Project> datas;
    ListView lv_people_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_people_detail);
        initView();
    }
    private void initView(){
        back= (TextView) findViewById(R.id.back);
        people_name= (TextView) findViewById(R.id.people_name);
        lv_people_list= (ListView) findViewById(R.id.lv_people_list);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        datas= (List<Project>) bundle.get("details");
        people_name.setText(bundle.getString("name"));
        lv_people_list.setAdapter(new MyAdapter());
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
                view.setTag(holder);
            }else{
                holder= (ViewHolder) view.getTag();
            }
            Project pro=datas.get(i);
            holder.name.setText(pro.getFullname());
            holder.numbers.setText(pro.getAmount());
            return view;
        }
    }
    class ViewHolder{
        TextView name,numbers;
    }
}
