package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.SelectEntity;

import java.util.List;


/**
 * Created by LYS on 2016/10/19.
 */
public class MyGridViewAdapter extends BaseAdapter {
    Context context;
    List<SelectEntity> data;
    public MyGridViewAdapter(Context context, List<SelectEntity> data){
        this.context=context;
        this.data=data;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v=View.inflate(context, R.layout.style_recycler_center,null);
        TextView tv= (TextView) v.findViewById(R.id.tv_little);
        ImageView iv_tag= (ImageView) v.findViewById(R.id.iv_tag);
        LinearLayout ll_bg= (LinearLayout) v.findViewById(R.id.ll_bg);
        tv.setText(data.get(i).getText());
        if (data.get(i).getDisabled()){
            ll_bg.setBackgroundResource(R.drawable.shap_all_gray_bg);
        }else{
            ll_bg.setBackgroundResource(R.drawable.shap_all_fenhong_bg);
        }
        if (data.get(i).getSelected()){
            iv_tag.setVisibility(View.VISIBLE);
        }else{
            iv_tag.setVisibility(View.INVISIBLE);
        }
        return v;
    }
}
