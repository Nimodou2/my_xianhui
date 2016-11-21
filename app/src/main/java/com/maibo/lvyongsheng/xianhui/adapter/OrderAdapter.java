package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.Order;

import java.util.List;


/**
 * Created by LYS on 2016/10/9.
 */
public class OrderAdapter extends BaseAdapter {

    Context context;
    List<Order> list;
    int log;

    class ViewHolder{
        TextView tv_cus_name,tv_status,tv_bianhao,tv_start_time,tv_pro_name,tv_bed_num,tv_jishi_name;
    }

    public OrderAdapter(Context context, List<Order> list,int log){
        this.context=context;
        this.list=list;
        this.log=log;
    }
    @Override
    public int getCount() {
        return list.size();
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
        if(view==null){
            holder=new ViewHolder();
            view=View.inflate(context, R.layout.style_order,null);
            holder.tv_cus_name=(TextView) view.findViewById(R.id.tv_cus_name);
            holder.tv_status=(TextView) view.findViewById(R.id.tv_status);
            holder.tv_bianhao=(TextView) view.findViewById(R.id.tv_bianhao);
            holder.tv_start_time=(TextView) view.findViewById(R.id.tv_start_time);
            holder.tv_pro_name=(TextView) view.findViewById(R.id.tv_pro_name);
            holder.tv_bed_num=(TextView) view.findViewById(R.id.tv_bed_num);
            holder.tv_jishi_name=(TextView) view.findViewById(R.id.tv_jishi_name);
            view.setTag(holder);
        }else{
            holder=(ViewHolder) view.getTag();
        }
        if(log==0){
            Order order=list.get(i);
            holder.tv_cus_name.setText("顾客:"+order.getCustomer_name());
            holder.tv_status.setText(order.getStatus());
            holder.tv_bianhao.setText("编号:"+order.getFlowno());
            holder.tv_start_time.setText("");
            holder.tv_pro_name.setText("品名:"+order.getItem_name());
            holder.tv_bed_num.setText("数量:"+order.getQty());
            holder.tv_jishi_name.setText("技师:");
            return view;
        }else if(log==1){
          Order order=list.get(i);
            holder.tv_cus_name.setText("顾客:"+order.getCustomer_name());
            holder.tv_status.setText(order.getStatus());
            holder.tv_bianhao.setText("编号:"+order.getProject_code());
            holder.tv_start_time.setText("开始时间:"+order.getStart_time());
            holder.tv_pro_name.setText("品名:"+order.getProject_name());
            holder.tv_bed_num.setText("床位:"+order.getBed_name());
            holder.tv_jishi_name.setText("技师:"+order.getEngineer_name());
            return view;
        }
        return null;
    }
}
