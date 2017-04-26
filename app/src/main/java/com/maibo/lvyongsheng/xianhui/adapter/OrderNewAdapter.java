package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.Order;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by LYS on 2017/3/22.
 */

public class OrderNewAdapter extends BaseAdapter {
    Context mContext;
    List<Order> list;
    Map<Integer, Integer> map;

    public static final int TYPE_NORMAL=0;
    public static final int TYPE_FOOD=1;

    @Override
    public int getItemViewType(int position) {
        if(position==list.size()){
            return TYPE_FOOD;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
    public int getListSize(){
        return list.size();
    }
    public OrderNewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setDatas(List<Order> list, Map<Integer, Integer> map) {
        this.list = list;
        this.map = map;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        FoodView foodview;
        switch (getItemViewType(position)){
            case TYPE_NORMAL:
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.style_new_order_item2, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                initHolderDate(holder,position);
                break;
            case TYPE_FOOD:
                if(convertView==null){
                    convertView=View.inflate(mContext,R.layout.food_view_layout_white,null);
                    foodview=new FoodView();
                    foodview.auto= (AutoLinearLayout) convertView.findViewById(R.id.food_view_auto);
                    convertView.setTag(foodview);
                }else {
                    foodview= (FoodView) convertView.getTag();
                }
                break;
            default:break;
        }
        return convertView;
    }

    private void initHolderDate(ViewHolder holder,int position) {
        Order order = list.get(position);
        if (map.get(position) == 0) {
            //holder.auto_ll.setBackgroundColor(Color.rgb(231, 224, 213));
            holder.auto_ll.setBackgroundResource(R.drawable.order_item_autolinearlayout_normal);
        } else {
            //holder.auto_ll.setBackgroundColor(Color.rgb(235, 214, 181));
            holder.auto_ll.setBackgroundResource(R.drawable.order_item_autolinearlayout_selecte);
        }

        holder.tv_project_name.setText(order.getProject_name());
        holder.tv_tec_name.setText(order.getEngineer_name());
        int orderStatus = order.getRaw_status();
        if (orderStatus == 0) {
            holder.order_status.setImageResource(R.mipmap.order_logo_yyy);
        } else if (orderStatus == 1) {
            holder.order_status.setImageResource(R.mipmap.order_logo_ing);
        } else {
            holder.order_status.setImageResource(R.mipmap.order_logo_yjs);
        }
    }

    class ViewHolder {
        @Bind(R.id.order_status)
        ImageView order_status;
        @Bind(R.id.tv_project_name)
        TextView tv_project_name;
        @Bind(R.id.tv_tec_name)
        TextView tv_tec_name;
        @Bind(R.id.auto_ll)
        AutoRelativeLayout auto_ll;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    class  FoodView{
        private AutoLinearLayout auto;
    }
}
