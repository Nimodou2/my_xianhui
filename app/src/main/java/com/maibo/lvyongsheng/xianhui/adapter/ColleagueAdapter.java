package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.Employee;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LYS on 2016/11/30.
 */


public class ColleagueAdapter extends BaseAdapter {

    private Context context;
    private List<Employee> list1;
    private int screenHeight;
    public ColleagueAdapter(Context context, List<Employee> list1,int screenHeight){
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
            view=View.inflate(context, R.layout.style_colleague_list,null);
            holder.iv_avator=(CircleImageView) view.findViewById(R.id.iv_avator);
            holder.tv_name=(TextView) view.findViewById(R.id.tv_name);
            holder.tv_position=(TextView) view.findViewById(R.id.tv_position);
            holder.tv_times=(TextView) view.findViewById(R.id.tv_times);
            holder.tv_items=(TextView) view.findViewById(R.id.tv_items);
            holder.tv_status=(TextView) view.findViewById(R.id.tv_status);
            holder.ll_all= (LinearLayout) view.findViewById(R.id.ll_alls);
            view.setTag(holder);
        }else{
            holder=(ViewHolder) view.getTag();
        }
        //setHeightAndWidth(holder);

        Employee employee=list1.get(i);
        Picasso.with(context).load(employee.getAvator_url()).into(holder.iv_avator);
        //setHead(employee,holder.iv_avator);
        holder.tv_name.setText(employee.getDisplay_name());
        holder.tv_position.setText(employee.getOrg_name());
        holder.tv_times.setText(employee.getProject_qty()+"");
        holder.tv_items.setText(employee.getProject_hours()+"");
            /*1服务中,2等待中有预约，3等待中未预约*/
        if(employee.getStatus()==1){
            holder.tv_status.setText("服务中");
        }else if (employee.getStatus()==2){
            holder.tv_status.setText("等待中");
        }else if (employee.getStatus()==3){
            holder.tv_status.setText("等待中");
        }
        return view;
    }
    class ViewHolder{
        CircleImageView iv_avator;
        TextView tv_name,tv_position,tv_times,tv_items,tv_status;
        LinearLayout ll_all;
    }
    /**
     * 动态设置宽和高
     * @param holder
     */
    private void setHeightAndWidth(ColleagueAdapter.ViewHolder holder) {
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


