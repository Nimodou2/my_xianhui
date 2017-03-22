package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.Order;

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

    public OrderNewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setDatas(List<Order> list, Map<Integer, Integer> map) {
        this.list = list;
        this.map = map;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.style_new_order_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Order order = list.get(position);
        if (map.get(position) == 0) {
            convertView.setBackgroundColor(Color.rgb(231, 224, 213));
        } else {
            convertView.setBackgroundColor(Color.rgb(235, 214, 181));
        }
        holder.tv_project_name.setText(order.getProject_name());
        holder.tv_tec_name.setText(order.getEngineer_name());
        int orderStatus = order.getRaw_status();
        if (orderStatus == 0) {
            holder.order_status.setImageResource(R.mipmap.appointment_img);
        } else if (orderStatus == 1) {
            holder.order_status.setImageResource(R.mipmap.doing_img);

        } else {
            holder.order_status.setImageResource(R.mipmap.finish_img);

        }
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.order_status)
        ImageView order_status;
        @Bind(R.id.tv_project_name)
        TextView tv_project_name;
        @Bind(R.id.tv_tec_name)
        TextView tv_tec_name;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
