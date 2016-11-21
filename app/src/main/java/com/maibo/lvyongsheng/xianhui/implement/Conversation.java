package com.maibo.lvyongsheng.xianhui.implement;

import android.view.View;

import cn.leancloud.chatkit.adapter.LCIMCommonListAdapter;
import cn.leancloud.chatkit.viewholder.LCIMCommonViewHolder;

/**
 * Created by LYS on 2016/11/2.
 */
public class Conversation<T> extends LCIMCommonListAdapter<LCIMCommonViewHolder> {
    public OnLongClickChildListener listener;

    public Conversation(Class<?> vhClass) {
        super(vhClass);
    }

    public void setOnChildListener(OnLongClickChildListener listener) {
        this.listener = listener;
    }


    // 删除条目
    public void removeConversation(int position){
        dataList.remove(dataList.get(position));
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(LCIMCommonViewHolder holder, final int position) {
        if (position >= 0 && position < dataList.size()) {
            holder.bindData(dataList.get(position));
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.setOnLongClickChild(position);
                return true;
            }
        });

    }

    public interface OnLongClickChildListener{
        void setOnLongClickChild(int position);
    }
}
