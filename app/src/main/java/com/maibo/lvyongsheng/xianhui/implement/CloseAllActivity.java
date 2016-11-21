package com.maibo.lvyongsheng.xianhui.implement;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by LYS on 2016/10/18.
 */
public class CloseAllActivity {
    private static Stack<Activity> mActivityStack = new Stack<Activity>();
    private static CloseAllActivity instance = new CloseAllActivity();

    private CloseAllActivity() {
    }

    public static CloseAllActivity getScreenManager() {
        return instance;
    }

    // 弹出当前activity并销毁
    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            mActivityStack.remove(activity);
            activity = null;
        }
    }

    // 将当前Activity推入栈中
    public void pushActivity(Activity activity) {
        mActivityStack.add(activity);
    }

    // 退出栈中所有Activity
    public void clearAllActivity() {
        while (!mActivityStack.isEmpty()) {
            Activity activity = mActivityStack.pop();
            if (activity != null) {
                activity.finish();
            }
        }
    }
}
