package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.BTabList;

import java.util.List;


/**
 * Created by LYS on 2016/9/18.
 */
public class DayTabListAdapter extends BaseAdapter {

    private List<BTabList> bt;
    private Context context;
    int viewHeight;
    public DayTabListAdapter(Context context,List<BTabList> bt,int viewHeight){
        this.context=context;
        this.bt=bt;
        this.viewHeight=viewHeight;
    }
    @Override
    public int getCount() {
        return bt.size();
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
        ViewHolder holder ;
        if(view==null){
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.style_have_right_arrow_image,null);
            holder.name=(TextView) view.findViewById(R.id.name);
            holder.number=(TextView) view.findViewById(R.id.numbers);
            holder.iv_arrow= (ImageView) view.findViewById(R.id.iv_arrow);
            holder.ll_all= (LinearLayout) view.findViewById(R.id.ll_all);
            view.setTag(holder);

        }else{
            holder =(ViewHolder) view.getTag();
        }
        ViewGroup.LayoutParams params=holder.ll_all.getLayoutParams();
        params.height=viewHeight/10;
        holder.ll_all.setLayoutParams(params);

        BTabList bTab=bt.get(i);
        holder.name.setText(bTab.getName());
        holder.number.setText(bTab.getAmount()+"");
        if (bTab.getAmount().equals("0")){
            holder.iv_arrow.setVisibility(View.INVISIBLE);
        }else{
            holder.iv_arrow.setVisibility(View.VISIBLE);
        }
        return view;
    }
    class ViewHolder{
        TextView name , number;
        ImageView iv_arrow;
        LinearLayout ll_all;
    }
}
