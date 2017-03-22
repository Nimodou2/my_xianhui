package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.Order;
import com.maibo.lvyongsheng.xianhui.myinterface.OrderStateListener;

import java.util.List;


/**
 * Created by LYS on 2016/10/9.
 */
public class OrderAdapter extends BaseAdapter {

    Context context;
    List<Order> list;
    int log;
    int viewHeight;
    OrderStateListener listener=null;

    class ViewHolder{
        TextView tv_cus_name,tv_status,tv_bianhao,tv_start_time,tv_pro_name,tv_bed_num,tv_jishi_name;
        TextView tv_start_cancel,tv_finish;
        LinearLayout ll_all,ll_foot;
    }
    public OrderAdapter(){}

    public OrderAdapter(Context context, List<Order> list,int log,int viewHeight){
        this.context=context;
        this.list=list;
        this.log=log;
        this.viewHeight=viewHeight;
    }

    public void setDatas(Context context, List<Order> list,int log,int viewHeight){
        this.context=context;
        this.list=list;
        this.log=log;
        this.viewHeight=viewHeight;
    }

    /**
     * 变更数据时使用
     * @param list
     */
    public void setDatas(List<Order> list){
        this.list=list;
        notifyDataSetChanged();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
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
            holder.ll_all= (LinearLayout) view.findViewById(R.id.ll_all);
            holder.tv_start_cancel= (TextView) view.findViewById(R.id.tv_start_cancel);
            holder.tv_finish= (TextView) view.findViewById(R.id.tv_finish);
            holder.ll_foot= (LinearLayout) view.findViewById(R.id.ll_foot);
            view.setTag(holder);
        }else{
            holder=(ViewHolder) view.getTag();
        }

        ViewGroup.LayoutParams params=holder.ll_all.getLayoutParams();
        params.height= (int) (viewHeight/3.2);
        holder.ll_all.setLayoutParams(params);
        if(log==0){
            Order order=list.get(i);
            holder.tv_cus_name.setText("顾客:"+order.getCustomer_name());
            holder.tv_status.setText(order.getStatus());
            holder.tv_bianhao.setText("编号:"+order.getFlowno());
            holder.tv_start_time.setText("");
            holder.tv_pro_name.setText("品名:"+order.getItem_name());
            holder.tv_bed_num.setText("数量:"+order.getQty());
            holder.tv_jishi_name.setText("技师:");
            holder.ll_foot.setVisibility(View.GONE);
            return view;
        }else if(log==1){
          final Order order=list.get(i);
            holder.tv_cus_name.setText("顾客:"+order.getCustomer_name());
//            holder.tv_status.setText(order.getStatus());
            holder.tv_bianhao.setText("编号:"+order.getProject_code());
            holder.tv_start_time.setText("开始时间:"+order.getStart_time());
            holder.tv_pro_name.setText("品名:"+order.getProject_name());
            holder.tv_bed_num.setText("床位:"+order.getBed_name());
            holder.tv_jishi_name.setText("技师:"+order.getEngineer_name());
            if (order.getRaw_status()==0){
                holder.tv_status.setText("未开始");
                holder.ll_foot.setVisibility(View.VISIBLE);
                holder.tv_start_cancel.setText("开始");
                holder.tv_start_cancel.setEnabled(true);
                holder.tv_start_cancel.setAlpha(1.0f);
                holder.tv_finish.setEnabled(false);
                holder.tv_finish.setAlpha(0.5f);
            }else if (order.getRaw_status()==1){
                holder.tv_status.setText("进行中");
                holder.ll_foot.setVisibility(View.VISIBLE);
                holder.tv_start_cancel.setText("取消");
                holder.tv_start_cancel.setEnabled(true);
                holder.tv_start_cancel.setAlpha(1.0f);
                holder.tv_finish.setEnabled(true);
                holder.tv_finish.setAlpha(1.0f);
            }else if (order.getRaw_status()==2){
                holder.tv_status.setText("已结束");
                holder.ll_foot.setVisibility(View.GONE);
            }else if (order.getRaw_status()==3){
                holder.tv_status.setText("已结单");
                holder.ll_foot.setVisibility(View.GONE);
            }else{
                holder.ll_foot.setVisibility(View.GONE);
            }
            holder.tv_start_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (order.getRaw_status()==0){
                        listener.startOrder(i,order.getSchedule_id());
                    }else if (order.getRaw_status()==1){
                        listener.cancelOrder(i,order.getSchedule_id());
                    }
                }
            });
            holder.tv_finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (order.getRaw_status()==1){
                        listener.finishOrder(i,order.getSchedule_id());
                    }
                }
            });
            return view;
        }
        return null;
    }

    public void setOnMyButtonClickListener(OrderStateListener listener){
        this.listener=listener;
    }
}
