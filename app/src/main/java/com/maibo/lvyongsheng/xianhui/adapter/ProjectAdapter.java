package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.Project;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LYS on 2016/11/30.
 */


public class ProjectAdapter extends BaseAdapter {
    Context context;
    List<Project> list1;
    int screenHeight;
    public ProjectAdapter(Context context, List<Project> list1,int screenHeight){
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
            view=View.inflate(context, R.layout.style_project_list,null);
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
        Project project=list1.get(i);
        if(project.getAvator_url().equals("")){
            //设置圆形头像
            holder.tv_head.setVisibility(View.VISIBLE);
            holder.iv_avator.setVisibility(View.INVISIBLE);
            holder.tv_head.setBackgroundResource(R.drawable.yello_circle);

            if (project.getProject_name().length()>1){
                String text=project.getProject_name().substring(0,2);
                holder.tv_head.setText(text);
            }else{
                holder.tv_head.setText(project.getProject_name());
            }
        }else{
            //设置原本头像
            holder.tv_head.setVisibility(View.INVISIBLE);
            holder.iv_avator.setVisibility(View.VISIBLE);
            Picasso.with(context).load(project.getAvator_url()).into(holder.iv_avator);
            //setHead(project.getAvator_url(),holder.iv_avator);
        }
        holder.tv_name.setText(project.getProject_name());
        holder.tv_position.setText(project.getOrg_name());
        holder.tv_times.setText(project.getPaid_num()+"");
        holder.tv_items.setText(project.getPaid_num()+"");
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
    private void setHeightAndWidth(ProjectAdapter.ViewHolder holder) {
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

