package com.maibo.lvyongsheng.xianhui.helperutils;

/**
 * Created by Administrator on 2017/4/25.
 */

public class MyItemTouchHelper extends SonOfItemTouchHelper{
    private MyItemTouchHelperCallBack itemTouchHelpCallback;

    public MyItemTouchHelper(MyItemTouchHelperCallBack.OnItemTouchCallbackListener onItemTouchCallbackListener) {
        super(new MyItemTouchHelperCallBack(onItemTouchCallbackListener));
        itemTouchHelpCallback = (MyItemTouchHelperCallBack) getCallback();
    }

    /**
     * 设置是否可以被拖拽
     *
     * @param canDrag 是true，否false
     */
    public void setDragEnable(boolean canDrag) {
        itemTouchHelpCallback.setDragEnable(canDrag);
    }

    /**
     * 设置是否可以被滑动
     *
     * @param canSwipe 是true，否false
     */
    public void setSwipeEnable(boolean canSwipe) {
        itemTouchHelpCallback.setSwipeEnable(canSwipe);
    }
}
