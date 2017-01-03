package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.TaskInfomation;

import java.util.List;

/**
 * Created by LYS on 2016/11/24.
 */

public class TaskListAdapter extends BaseAdapter {

    List<TaskInfomation> data;
    Context context;

    public TaskListAdapter(Context context,List<TaskInfomation> data){
        this.context=context;
        this.data=data;
    }
    @Override
    public int getCount() {
        return data.size();
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
            view=View.inflate(context, R.layout.style_task_progress,null);
            holder.tv_progress_cotent= (TextView) view.findViewById(R.id.tv_progress_cotent);
            holder.tv_progress_value= (TextView) view.findViewById(R.id.tv_progress_value);
            holder.pb_progressbar= (ProgressBar) view.findViewById(R.id.pb_progressbar);
            view.setTag(holder);
        }else{
            holder= (ViewHolder) view.getTag();
        }
        TaskInfomation tkInfor=data.get(i);
        String values="范围:"+tkInfor.getRange_name()+"  类型:"+tkInfor.getType_name()+"  截止:"+tkInfor.getEnd_date().substring(5);
        holder.tv_progress_cotent.setText(values);
        int progress=Integer.parseInt(tkInfor.getPercentage());
        holder.tv_progress_value.setText(progress/100+"%");
        holder.pb_progressbar.setProgress(50);
        return view;
    }

    class ViewHolder{
        TextView tv_progress_cotent,tv_progress_value;
        ProgressBar pb_progressbar;
    }
}
