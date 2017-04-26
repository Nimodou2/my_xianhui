package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LYS on 2016/11/30.
 */


public class ProductAdapter extends BaseAdapter {

    Context context;
    List<Product> list1;
    int screenHeight;
    public ProductAdapter(Context context, List<Product> list1,int screenHeight){
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
            view=View.inflate(context, R.layout.style_product_list,null);
            holder.iv_avator=(CircleImageView) view.findViewById(R.id.iv_avator);
            holder.tv_name=(TextView) view.findViewById(R.id.tv_name);
            holder.tv_position=(TextView) view.findViewById(R.id.tv_position);
            holder.tv_times=(TextView) view.findViewById(R.id.tv_times);
            holder.tv_items=(TextView) view.findViewById(R.id.tv_items);
            holder.tv_status=(TextView) view.findViewById(R.id.tv_status);
            holder.tv_head=(TextView) view.findViewById(R.id.tv_head);
            holder.ll_all= (LinearLayout) view.findViewById(R.id.ll_alls);
            view.setTag(holder);
        }else{
            holder=(ViewHolder) view.getTag();
        }

        //setHeightAndWidth(holder);
        Product product=list1.get(i);
        if(product.getAvator_url().equals("")){
            //设置圆形头像
            holder.tv_head.setVisibility(View.VISIBLE);
            holder.iv_avator.setVisibility(View.INVISIBLE);
            holder.tv_head.setBackgroundResource(R.drawable.yello_circle);

            if (product.getFullname().length()>1){
                String text=product.getFullname().substring(0,2);
                holder.tv_head.setText(text);
            }else{
                holder.tv_head.setText(product.getFullname());
            }
        }else{
            //设置原本头像
            holder.tv_head.setVisibility(View.INVISIBLE);
            holder.iv_avator.setVisibility(View.VISIBLE);
            Picasso.with(context).load(product.getAvator_url()).into(holder.iv_avator);
            //setHead(product.getAvator_url(),holder.iv_avator);
        }
        holder.tv_name.setText(product.getFullname());
        holder.tv_position.setText(product.getOrg_name());
        holder.tv_times.setText(product.getBuy_num()+"");
        holder.tv_items.setText(product.getStock_qty());
        holder.tv_status.setText("");
        return view;
    }
    class ViewHolder{
        CircleImageView iv_avator;
        TextView tv_name,tv_position,tv_times,tv_items,tv_status,tv_head;
        LinearLayout ll_all;
    }
    /**
     * 动态设置宽和高
     * @param holder
     */
    private void setHeightAndWidth(ProductAdapter.ViewHolder holder) {
        int view_height=screenHeight/14;
        ViewGroup.LayoutParams params=holder.ll_all.getLayoutParams();
        params.height=view_height;
        holder.ll_all.setLayoutParams(params);

        ViewGroup.LayoutParams params1=holder.iv_avator.getLayoutParams();
        params1.height=view_height;
        params1.width=view_height;
        holder.iv_avator.setLayoutParams(params1);
    }
}

