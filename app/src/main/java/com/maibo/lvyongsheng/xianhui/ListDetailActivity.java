package com.maibo.lvyongsheng.xianhui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.entity.BTabList;
import com.maibo.lvyongsheng.xianhui.entity.LTabList;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;

import java.util.List;

import butterknife.Bind;


/**
 * Created by LYS on 2016/9/19.
 */
public class ListDetailActivity extends BaseActivity {
    List<LTabList> lists;
    TextView tv_item_name,back;
    ListView lv_detail_list;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);
        //adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        lv_detail_list=(ListView)findViewById(R.id.lv_detail_list);
        tv_item_name=(TextView)findViewById(R.id.tv_item_name);
        back= (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent=getIntent();
        BTabList data=(BTabList) intent.getSerializableExtra("data");
        int position=intent.getIntExtra("position",-1);
        tv_item_name.setText(data.getName());
        lists=data.getlTabLists();
        if(lists.size()!=0){
            lv_detail_list.setAdapter(new MyAdapter());
        }
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return lists.size();
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
            View v=View.inflate(ListDetailActivity.this,R.layout.style_list_detail,null);
            TextView name=(TextView) v.findViewById(R.id.name);
            TextView numbers=(TextView) v.findViewById(R.id.numbers);
            LinearLayout ll_all= (LinearLayout) v.findViewById(R.id.ll_all);
            ViewGroup.LayoutParams params=ll_all.getLayoutParams();
            params.height=viewHeight/10;
            ll_all.setLayoutParams(params);

            LTabList lTabList=lists.get(i);
            name.setText(lTabList.getFullname());
            numbers.setText(lTabList.getAmount());

            return v;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
