package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;

import java.util.List;


/**
 * Created by LYS on 2016/10/19.
 */
public class CustomerChooseAdapter extends RecyclerView.Adapter<CustomerChooseAdapter.MyViewHolder> {

    Context context;
    List<String> data;
    public CustomerChooseAdapter(Context context,List<String> data){
        this.context=context;
        this.data=data;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.style_recycler_center, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv_little.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position%6==0){
            return 0;
        }else{
            return 1;
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView tv_little;
        public MyViewHolder(View view)
        {
            super(view);
            tv_little = (TextView) view.findViewById(R.id.tv_little);
        }
    }
}
