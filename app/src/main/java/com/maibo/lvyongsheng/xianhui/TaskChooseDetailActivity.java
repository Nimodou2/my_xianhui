package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.adapter.TaskChooseTypeAdapter;
import com.maibo.lvyongsheng.xianhui.entity.Task;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.myinterface.OnChooseTypeListener;

import java.util.List;

import butterknife.Bind;
import cn.leancloud.chatkit.view.LCIMDividerItemDecoration;

/**
 * Created by LYS on 2016/11/23.
 */

public class TaskChooseDetailActivity extends BaseActivity implements View.OnClickListener{
    TextView tv_hand,back,tv_task_name;
    RecyclerView rv_detail_list;
    List<Task> list_task;
    String tag;
    RecyclerView.LayoutManager manager;
    SharedPreferences sp;
    String myName;
    TaskChooseTypeAdapter adapter;
    String resultData="";//回传过去的用于上传的数据
    String textData="";//回传过去用于界面填充的数据
    @Bind(R.id.ll_head)
    LinearLayout ll_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView(){
        setContentView(R.layout.activity_choose_detail);
        adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        tv_hand= (TextView) findViewById(R.id.tv_hand);
        rv_detail_list= (RecyclerView) findViewById(R.id.rv_detail_list);
        back= (TextView) findViewById(R.id.back);
        tv_task_name= (TextView) findViewById(R.id.tv_task_name);

        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        myName=sp.getString("displayname",null);

        back.setOnClickListener(this);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        list_task= (List<Task>) bundle.get("task");
        String save= bundle.getString("save");
        resultData=save;
        String text=bundle.getString("text");
        textData=text;
        tag=bundle.getString("tag");

        manager=new LinearLayoutManager(this);
        rv_detail_list.setLayoutManager(manager);
        rv_detail_list.addItemDecoration(new LCIMDividerItemDecoration(this));


    }

    private void initData(){
        if (!TextUtils.isEmpty(tag)){
            if (tag.equals("range")){
                //准备范围数据，开启适配器
                for (int i=0;i<list_task.size();i++){
                    if (list_task.get(i).getValue().equals(resultData)){
                        list_task.get(i).setIsChecked(1);
                    }
                }
                rv_detail_list.setAdapter(adapter=new TaskChooseTypeAdapter(TaskChooseDetailActivity.this,list_task,tag,viewHeight));
                tv_hand.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = tv_hand.getLayoutParams();
                params.height=viewHeight*15/255;
                tv_hand.setLayoutParams(params);
                tv_task_name.setText("范围");
                myNotificeDataChange(list_task,1);
            }else if(tag.equals("type")){
                //准备类型数据
                for (int i=0;i<list_task.size();i++){
                    if (list_task.get(i).getValue().equals(resultData)){
                        list_task.get(i).setIsChecked(1);
                    }
                }
                rv_detail_list.setAdapter(adapter=new TaskChooseTypeAdapter(TaskChooseDetailActivity.this,list_task,tag,viewHeight));
                tv_task_name.setText("类型");
                myNotificeDataChange(list_task,1);
            }else if (tag.equals("joiner")){
                //获取联系人数据
                String[] joiner_save=resultData.split(",");
                if (joiner_save.length>0){
                    for (int i=0;i<list_task.size();i++){
                        for (int j=0;j<joiner_save.length;j++){
                            if (list_task.get(i).getValue().equals(joiner_save[j])){
                                list_task.get(i).setIsChecked(1);
                            }
                        }

                    }
                }

                rv_detail_list.setAdapter(adapter=new TaskChooseTypeAdapter(TaskChooseDetailActivity.this,list_task,tag,viewHeight));
                tv_task_name.setText("参与者");
                myNotificeDataChange(list_task,2);

            }
        }
    }

    /**
     * 单选
     * @param datas
     */
    private void myNotificeDataChange(final List<Task> datas,final int what) {
        adapter.setOnChooseTypeListener(new OnChooseTypeListener() {
            @Override
            public void onChooseType(int position, int type,String tag) {
                for (int i=0;i<datas.size();i++){
                    if (what==1){
                        if (type==1){
                            if (i==position){
                                if ( datas.get(position).getIsChecked()==1){
                                    datas.get(position).setIsChecked(0);
                                    resultData="";
                                    textData="";
                                }else{
                                    datas.get(position).setIsChecked(1);
                                    //记录下被选中的那一个
                                    resultData=datas.get(position).getValue();
                                    textData=datas.get(position).getText();
                                }
                            }else{
                                datas.get(i).setIsChecked(0);
                            }
                        }
                    }else if (what==2){
                        if (i==position){
                            if ( datas.get(position).getIsChecked()==1){
                                datas.get(position).setIsChecked(0);
                                resultData=resultData.replace(","+datas.get(position).getValue(),"");
                                textData=textData.replace(","+datas.get(position).getText(),"");
                            }else{
                                datas.get(position).setIsChecked(1);
                                resultData+=","+datas.get(position).getValue();
                                textData+=","+datas.get(position).getText();
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        if (tag.equals("range")){
            intent.putExtra("range",resultData);
            intent.putExtra("range_text",textData);
            setResult(3,intent);
        }else if (tag.equals("type")){
            intent.putExtra("type",resultData);
            intent.putExtra("type_text",textData);
            setResult(4,intent);
        }else if (tag.equals("joiner")){
            if (resultData.lastIndexOf(",")!=-1){
                if (resultData.substring(0,1).equals(",")){
                    resultData=resultData.substring(1);
                }
            }

            if (textData.lastIndexOf(",")!=-1){
                if (textData.substring(0,1).equals(",")){
                    textData=textData.substring(1);
                }
            }
            intent.putExtra("joiner",resultData);
            intent.putExtra("joiner_text",textData);
            setResult(5,intent);

        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent();
            if (tag.equals("range")){
                intent.putExtra("range",resultData);
                intent.putExtra("range_text",textData);
                setResult(3,intent);
            }else if (tag.equals("type")){
                intent.putExtra("type",resultData);
                intent.putExtra("type_text",textData);
                setResult(4,intent);
            }else if (tag.equals("joiner")){
                if (resultData.lastIndexOf(",")!=-1){
                    if (resultData.substring(0,1).equals(",")){
                        resultData=resultData.substring(1);
                    }
                }

                if (textData.lastIndexOf(",")!=-1){
                    if (textData.substring(0,1).equals(",")){
                        textData=textData.substring(1);
                    }
                }
                intent.putExtra("joiner",resultData);
                intent.putExtra("joiner_text",textData);
                setResult(5,intent);

            }
            finish();
            return true;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
