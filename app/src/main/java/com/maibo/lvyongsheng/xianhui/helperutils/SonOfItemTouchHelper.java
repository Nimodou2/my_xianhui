package com.maibo.lvyongsheng.xianhui.helperutils;

import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Administrator on 2017/4/25.
 */

public class SonOfItemTouchHelper extends ItemTouchHelper {
    private Callback mCallback;
    public SonOfItemTouchHelper(Callback callback) {
        super(callback);
        this.mCallback=callback;
    }
    public Callback getCallback() {
        return mCallback;
    }
}
