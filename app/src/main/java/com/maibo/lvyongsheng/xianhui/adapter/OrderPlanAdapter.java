package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.UnFinishOrder;

import java.util.List;

/**
 * Created by LYS on 2017/3/20.
 */

public class OrderPlanAdapter extends BaseAdapter {
    Context mContext;
    List<UnFinishOrder.DataBean.RowsBean.PlanListBean> planList;

    public OrderPlanAdapter(Context mContext, List<UnFinishOrder.DataBean.RowsBean.PlanListBean> planList) {
        this.mContext = mContext;
        this.planList = planList;
    }

    @Override
    public int getCount() {
        return planList == null ? 0 : planList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.style_order_plan, null);
        LinearLayout ll_all = (LinearLayout) view.findViewById(R.id.ll_all);
        ImageView iv_status = (ImageView) view.findViewById(R.id.iv_status);
        final TextView tv_plan_status = (TextView) view.findViewById(R.id.tv_plan_status);
        final TextView tv_project_name = (TextView) view.findViewById(R.id.tv_project_name);
        tv_project_name.setVisibility(View.VISIBLE);
        UnFinishOrder.DataBean.RowsBean.PlanListBean pb = planList.get(position);
        final int status = pb.getStatus();
        if (status == 3) {
            iv_status.setImageResource(R.mipmap.finish);
        } else if (status == 2) {
            iv_status.setImageResource(R.mipmap.exclamationmark);
        } else {
            iv_status.setImageResource(R.mipmap.questionmark);
        }
        tv_project_name.setText(pb.getProject_name());
        ll_all.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tv_plan_status.setVisibility(View.VISIBLE);
                        tv_project_name.setVisibility(View.GONE);
                        tv_plan_status.setAlpha(0.5f);
                        if (status == 3) {
                            tv_plan_status.setText("有计划、有预约");
                        } else if (status == 2) {
                            tv_plan_status.setText("有计划、未预约");
                        } else {
                            tv_plan_status.setText("未计划、有预约");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        tv_plan_status.setVisibility(View.GONE);
                        tv_project_name.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        tv_plan_status.setVisibility(View.GONE);
                        tv_project_name.setVisibility(View.VISIBLE);
                        break;
                }
                return true;
            }
        });
        return view;
    }
}
