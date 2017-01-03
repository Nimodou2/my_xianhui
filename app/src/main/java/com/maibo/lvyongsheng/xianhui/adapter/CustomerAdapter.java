package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.HelperCustomer;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LYS on 2016/11/30.
 */

public class CustomerAdapter extends BaseAdapter {
    Context context;
    List<HelperCustomer> list1;
    int screenHeight;
    public CustomerAdapter(Context context, List<HelperCustomer> list1,int screenHeight){
        this.context=context;
        this.list1=list1;
        this.screenHeight=screenHeight;
    }
    @Override
    public int getCount() {
        return list1.size();
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
            view=View.inflate(context, R.layout.style_customer_list,null);
            holder.iv_avator=(CircleImageView) view.findViewById(R.id.iv_avator);
            holder.tv_name=(TextView) view.findViewById(R.id.tv_name);
            holder.tv_position=(TextView) view.findViewById(R.id.tv_position);
            holder.tv_times=(TextView) view.findViewById(R.id.tv_times);
            holder.tv_items=(TextView) view.findViewById(R.id.tv_items);
            holder.tv_status=(TextView) view.findViewById(R.id.tv_status);
            holder.tv_card_name=(TextView) view.findViewById(R.id.tv_card_name);
            holder.ll_all= (LinearLayout) view.findViewById(R.id.ll_alls);
            view.setTag(holder);
        }else{
            holder=(ViewHolder) view.getTag();
        }

        setHeightAndWidth(holder);

        HelperCustomer customer=list1.get(i);
        Picasso.with(context).load(customer.getAvator_url()).into(holder.iv_avator);

        holder.tv_name.setText(customer.getFullname());
        holder.tv_card_name.setText(customer.getVip_star());
        holder.tv_position.setText(customer.getOrg_name());
        holder.tv_times.setText(customer.getDays());
        holder.tv_items.setText(customer.getProject_total()+"");
        if(customer.getStatus()==1){
            holder.tv_status.setText("服务中");
        }else if (customer.getStatus()==2){
            holder.tv_status.setText("未到店");
        }else if (customer.getStatus()==3){
            holder.tv_status.setText("已离店");
        }else if (customer.getStatus()==4){
            holder.tv_status.setText("已预约");
        }else if (customer.getStatus()==5){
            holder.tv_status.setText("未预约");
        }else if (customer.getStatus()==6){
            holder.tv_status.setText("未预约");
        }
        return view;
    }

    /**
     * 动态设置宽和高
     * @param holder
     */
    private void setHeightAndWidth(ViewHolder holder) {
        int view_height=screenHeight/14;
        ViewGroup.LayoutParams params=holder.ll_all.getLayoutParams();
        params.height=view_height;
        holder.ll_all.setLayoutParams(params);

        ViewGroup.LayoutParams params1=holder.iv_avator.getLayoutParams();
        params1.height=view_height;
        params1.width=view_height;
        holder.iv_avator.setLayoutParams(params1);
    }

    class ViewHolder{
        CircleImageView iv_avator;
        TextView tv_name,tv_position,tv_times,tv_items,tv_status,tv_card_name;
        LinearLayout ll_all;
    }
}
