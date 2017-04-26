package com.maibo.lvyongsheng.xianhui.helperutils;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Administrator on 2017/4/21.
 */

public class MYRecyclerViewAnimation extends DefaultItemAnimator {
    //返回当前是否有动画正在运行。
    @Override
    public boolean isRunning() {
        return super.isRunning();
    }
    //当有动画需要执行时调用
    @Override
    public void runPendingAnimations() {
        super.runPendingAnimations();
    }
    //添加元素时调用，通常返回true。
    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        return super.animateAdd(holder);
    }
    //列表项位置移动时调用
    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        return super.animateMove(holder, fromX, fromY, toX, toY);
    }
    //移除数据时调用。
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        return super.animateRemove(holder);
    }
    //列表项数据发生改变时调用
    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
    }
    //当某个动画需要被立即停止时调用，这里一般做视图的状态恢复
    @Override
    public void endAnimations() {
        super.endAnimations();
    }
    //当某个动画需要被立即停止时调用，这里一般做视图的状态恢复
    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        super.endAnimation(item);
    }
}
