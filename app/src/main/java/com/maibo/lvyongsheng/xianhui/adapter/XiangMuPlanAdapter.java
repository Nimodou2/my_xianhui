package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;

import java.util.List;


/**
 * Created by LYS on 2016/9/20.
 */
public class XiangMuPlanAdapter extends BaseAdapter {
    List<String> data1;
    List<String> data2;
    int p=0;
    Context context;
    int viewHeight;

    public XiangMuPlanAdapter(Context context,List<String> data1,List<String> data2,int viewHeight){
        this.data1=data1;
        this.data2=data2;
        this.context=context;
        this.viewHeight=viewHeight;

    }
    @Override
    public int getCount() {
        int num=0;
        if(data1.size()!=0){
            num++;
        }
        if(data2.size()!=0){
            num++;
        }
        return data1.size()+data2.size()+num;
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

        //共有四种情况
        if(data1.size()==0&&data2.size()!=0){
            if (i == 0) {
                TextView textView = new TextView(context);
                setMySelfTextView(textView);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setText("计划产品");
                textView.setTextSize(14);
                textView.setBackgroundColor(context.getResources().getColor(R.color.weixin_lianxiren_gray));
                textView.setPadding(30,0,0,0);
                return textView;
            }else{
                String product = data2.get(i-1);
                TextView textView = new TextView(context);
                setItemTextView(textView);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setText(product);
                textView.setTextSize(16);
                textView.setPadding(40,0,0,0);
                textView.setTextColor(context.getResources().getColor(R.color.lcim_common_gray));
                textView.setBackgroundColor(Color.WHITE);
                return textView;
            }
        }else if (data1.size()==0&&data2.size()==0){
            return null;
        }else if(data1.size()!=0&&data2.size()==0){
            if (i == 0) {
                p=1;
                TextView textView = new TextView(context);
                setMySelfTextView(textView);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setText("计划项目");
                textView.setTextSize(14);
                textView.setBackgroundColor(context.getResources().getColor(R.color.weixin_lianxiren_gray));
                textView.setPadding(30,0,0,0);
                return textView;
            }else{
                String project=data1.get(i-1);
                TextView textView = new TextView(context);
                setItemTextView(textView);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setText(project);
                textView.setPadding(40,0,0,0);
                textView.setBackgroundColor(Color.WHITE);
                textView.setTextColor(context.getResources().getColor(R.color.lcim_common_gray));
                textView.setTextSize(16);
                return textView;
            }
        }else{
            if(i==0){
                TextView textView = new TextView(context);
                setMySelfTextView(textView);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setText("计划项目");
                textView.setTextSize(14);
                textView.setBackgroundColor(context.getResources().getColor(R.color.weixin_lianxiren_gray));
                textView.setPadding(30,0,0,0);
                return textView;
            }else if(i<1+data1.size()){
                String project=data1.get(i-1);
                TextView textView = new TextView(context);
                setItemTextView(textView);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setText(project);
                textView.setTextSize(16);
                textView.setPadding(40,0,0,0);
                textView.setBackgroundColor(Color.WHITE);
                textView.setTextColor(context.getResources().getColor(R.color.lcim_common_gray));
                return textView;
            }else if(i==1+data1.size()){
                TextView textView = new TextView(context);
                setMySelfTextView(textView);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setText("计划产品");
                textView.setPadding(30,0,0,0);
                textView.setBackgroundColor(context.getResources().getColor(R.color.weixin_lianxiren_gray));
                textView.setTextSize(14);
                return textView;
            }else{
                String product = data2.get(i-(2+data1.size()));
                TextView textView = new TextView(context);
                setItemTextView(textView);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setText(product);
                textView.setTextSize(16);
                textView.setPadding(40,0,0,0);
                textView.setTextColor(context.getResources().getColor(R.color.lcim_common_gray));
                textView.setBackgroundColor(Color.WHITE);
                return textView;
            }
        }
    }

    /**
     * 动态设置自定义Textview的高度
     *
     * @param tv
     */
    private void setMySelfTextView(TextView tv) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = viewHeight * 15 / 255;
        tv.setLayoutParams(params);
    }

    /**
     * 动态设置自定义Textview的高度
     *
     * @param tv
     */
    private void setItemTextView(TextView tv) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = viewHeight * 20 / 255;
        tv.setLayoutParams(params);
    }
}
