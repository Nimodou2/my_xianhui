package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.Task;
import com.maibo.lvyongsheng.xianhui.myinterface.OnChooseTypeListener;

import java.util.List;

/**
 * Created by LYS on 2016/11/23.
 */

public class TaskChooseTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Task> list_task;
    private String tag;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    int viewHeight;

    private OnChooseTypeListener listener;
    public void setOnChooseTypeListener(OnChooseTypeListener listener){
        this.listener=listener;
    }

    public TaskChooseTypeAdapter(Context context,List<Task> list_task,String tag,int viewHeight){
        this.list_task=list_task;
        this.tag=tag;
        this.mContext=context;
        this.viewHeight=viewHeight;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==0){
            return new FatherViewHolder(mLayoutInflater.inflate(R.layout.style_choose_type_father,parent,false));
        }else if (viewType==1){
            return new ChildViewHolder(mLayoutInflater.inflate(R.layout.style_choose_type_child,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof FatherViewHolder){
            ((FatherViewHolder) holder).tv_choose_type_father.setText(list_task.get(position).getText());
        }else if (holder instanceof ChildViewHolder){
            ((ChildViewHolder) holder).tv_choose_type_child.setText(list_task.get(position).getText());
            final ImageView imageView=((ChildViewHolder) holder).iv_check;
            if (list_task.get(position).getIsChecked()==0){
                imageView.setVisibility(View.GONE);
            }else if (list_task.get(position).getIsChecked()==1){
                imageView.setVisibility(View.VISIBLE);
            }

            final int type=holder.getItemViewType();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener!=null)
                        listener.onChooseType(position,type,tag);

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list_task.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list_task.get(position).getType()==0){
            return 0;
        }else if (list_task.get(position).getType()==1){
            return 1;
        }
        return -1;
    }

    class FatherViewHolder extends RecyclerView.ViewHolder{
        TextView tv_choose_type_father;
        LinearLayout ll_all;
        FatherViewHolder(View view){
            super(view);
            tv_choose_type_father= (TextView) view.findViewById(R.id.tv_choose_type_father);
            ll_all= (LinearLayout) view.findViewById(R.id.ll_all);
            ViewGroup.LayoutParams params=ll_all.getLayoutParams();
            params.height=viewHeight*15/255;
            ll_all.setLayoutParams(params);
        }
    }

    class ChildViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_check;
        TextView tv_choose_type_child;
        LinearLayout ll_all;
        ChildViewHolder(View view){
            super(view);
            iv_check= (ImageView) view.findViewById(R.id.iv_check);
            tv_choose_type_child= (TextView) view.findViewById(R.id.tv_choose_type_child);
            ll_all= (LinearLayout) view.findViewById(R.id.ll_all);
            ViewGroup.LayoutParams params=ll_all.getLayoutParams();
            params.height=viewHeight*20/255;
            ll_all.setLayoutParams(params);
        }
    }
}
