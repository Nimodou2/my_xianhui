package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.UnFinishOrder;
import com.maibo.lvyongsheng.xianhui.myinterface.OnUnFinishItemListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LYS on 2017/3/9.
 */

public class FinishOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG="FinishOrderAdapter";
    Context mContext;
    LayoutInflater mLayoutInflater;
    OnUnFinishItemListener listener;
    public List<UnFinishOrder.DataBean.RowsBean> getmRowsBean() {
        return mRowsBean;
    }
    List<UnFinishOrder.DataBean.RowsBean> mRowsBean;

    int tag;//0表示未完成的fragment   1表示已完成的fragment
    public FinishOrderAdapter(Context mContext, int tag) {
        this.mContext = mContext;
        this.tag = tag;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setDatas(List<UnFinishOrder.DataBean.RowsBean> mRowsBean) {
        this.mRowsBean = mRowsBean;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(mLayoutInflater.inflate(R.layout.style_customer_order, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final UnFinishOrder.DataBean.RowsBean rb = mRowsBean.get(position);
            if (!TextUtils.isEmpty(rb.getAvator_url()))
                Picasso.with(mContext).load(rb.getAvator_url()).into(((ItemViewHolder) holder).iv_circle_image);
            if (!TextUtils.isEmpty(rb.getCustomer_name()))
                ((ItemViewHolder) holder).tv_customer_name.setText(rb.getCustomer_name());
            if (!TextUtils.isEmpty(rb.getAdate()))
                ((ItemViewHolder) holder).tv_time.setText(rb.getAdate());
            int scheduleSize = 0;

            if ((scheduleSize = rb.getSchedule_list().size()) > 0) {
                ((ItemViewHolder) holder).ll_pro1.setVisibility(View.VISIBLE);
                List<UnFinishOrder.DataBean.RowsBean.ScheduleListBean> sb = rb.getSchedule_list();
                if (scheduleSize == 1) {
                    String statusText = sb.get(0).getStatus_text();
                    String proName = sb.get(0).getProject_name();
                    String techName = sb.get(0).getEngineer_name();
                    if (!TextUtils.isEmpty(statusText)){
                        ((ItemViewHolder) holder).tv_status.setText(statusText);
                        if(tag==0&&statusText.equals("已结束")){
                            ((ItemViewHolder) holder).tv_status.setText("未结单");
                        }
                    }
                    if (!TextUtils.isEmpty(proName))
                        ((ItemViewHolder) holder).tv_pro0.setText(proName);
                    if (!TextUtils.isEmpty(techName))
                        ((ItemViewHolder) holder).tv_tech0.setText(techName);
                    ((ItemViewHolder) holder).ll_pro1.setVisibility(View.GONE);
                } else if (scheduleSize == 2) {
                    int isFinish=0;
                    for (int i = 0; i < 2; i++) {
                        String statusText = sb.get(i).getStatus_text();
                        String proName = sb.get(i).getProject_name();
                        String techName = sb.get(i).getEngineer_name();
                        if(tag==0&&statusText.equals("已结束")){
                            isFinish=isFinish+1;
                        }
                        if (i == 0) {
                            if (!TextUtils.isEmpty(statusText))
                                ((ItemViewHolder) holder).tv_status.setText(statusText);
                            if (!TextUtils.isEmpty(proName))
                                ((ItemViewHolder) holder).tv_pro0.setText(proName);
                            if (!TextUtils.isEmpty(techName))
                                ((ItemViewHolder) holder).tv_tech0.setText(techName);
                        } else if (i == 1) {
                            if (!TextUtils.isEmpty(proName))
                                ((ItemViewHolder) holder).tv_pro1.setText(proName);
                            if (!TextUtils.isEmpty(techName))
                                ((ItemViewHolder) holder).tv_tech1.setText(techName);
                        }
                    }
                    if(isFinish==2&&tag==0){
                        ((ItemViewHolder) holder).tv_status.setText("未结单");
                    }
                } else if (scheduleSize > 2) {
                    int isFinish=0;

                    String statusText = sb.get(0).getStatus_text();
                    String proName = sb.get(0).getProject_name();
                    String techName = sb.get(0).getEngineer_name();

                    for(int i=0;i<scheduleSize;i++){
                        String statusText2 = sb.get(i).getStatus_text();
                        if (!TextUtils.isEmpty(statusText)){
                            if(statusText.equals("已结束")&&tag==0){
                                isFinish=isFinish+1;
                            }
                        }
                    }

                    if(isFinish==scheduleSize&&tag==0){
                        ((ItemViewHolder) holder).tv_status.setText("未结单");
                    }else {
                        ((ItemViewHolder) holder).tv_status.setText(statusText);
                    }

                    if (!TextUtils.isEmpty(proName))
                        ((ItemViewHolder) holder).tv_pro0.setText(proName);
                    if (!TextUtils.isEmpty(techName))
                        ((ItemViewHolder) holder).tv_tech0.setText(techName);
                    String proName2 = sb.get(1).getProject_name();
                    ((ItemViewHolder) holder).tv_pro1.setText(proName2 + "等共" + scheduleSize + "项");
                    ((ItemViewHolder) holder).tv_tech1.setText("");
                }

            }
            /*改这里*/
            if (rb.getPlan_list() == null || rb.getPlan_list().size() == 0) {
                ((ItemViewHolder) holder).tv_advise.setBackgroundResource(R.drawable.shap_side_weixin_gray_text_bg);
                ((ItemViewHolder) holder).tv_advise.setText("本次建议(0)");
                ((ItemViewHolder) holder).tv_advise.setTextColor(mContext.getResources().getColor(R.color.half_black));
                //可以点击显示无更多内容
                ((ItemViewHolder) holder).tv_advise.setClickable(true);
                ((ItemViewHolder) holder).tv_advise.setEnabled(true);
            } else {
                ((ItemViewHolder) holder).tv_advise.setBackgroundResource(R.drawable.shap_side_oranger_text_bg);
                ((ItemViewHolder) holder).tv_advise.setText("本次建议("+rb.getPlan_list().size()+")");
                ((ItemViewHolder) holder).tv_advise.setTextColor(mContext.getResources().getColor(R.color.main_color_version2));
                ((ItemViewHolder) holder).tv_advise.setClickable(true);
                ((ItemViewHolder) holder).tv_advise.setEnabled(true);
            }

            if (tag == 0) {
                ((ItemViewHolder) holder).tv_next_adviser.setVisibility(View.GONE);
            } else if (tag == 1) {
                ((ItemViewHolder) holder).tv_advise.setVisibility(View.GONE);
            }

            ((ItemViewHolder) holder).tv_advise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAdviseListener(position);
                }
            });
            ((ItemViewHolder) holder).tv_next_adviser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNextAdviserListener(position, rb.getCustomer_id(), rb.getCustomer_name());
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMyItemClickListener(position, rb.getCustomer_id(), rb.getCustomer_name());
                }
            });
            ((ItemViewHolder) holder).iv_circle_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickHeadListener(position, rb.getCustomer_id());
                }
            });

            /*添加新的按钮*/
            if(tag==1){
                int count=rb.getPlan_product_total()+rb.getPlan_project_total();
                if(count>0){
                    ((ItemViewHolder) holder).tv_next_adviser.setText("下次建议("+count+")");
                }else {
                    ((ItemViewHolder) holder).tv_next_adviser.setText("下次建议(0)");
                }
            }
        }

    }

    public void setOnClickUnFinishItem(OnUnFinishItemListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return mRowsBean == null ? 0 : mRowsBean.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_circle_image)
        CircleImageView iv_circle_image;
        @Bind(R.id.tv_customer_name)
        TextView tv_customer_name;
        @Bind(R.id.tv_time)
        TextView tv_time;
        @Bind(R.id.tv_status)
        TextView tv_status;
        @Bind(R.id.tv_pro0)
        TextView tv_pro0;
        @Bind(R.id.tv_pro1)
        TextView tv_pro1;
        @Bind(R.id.tv_tech0)
        TextView tv_tech0;
        @Bind(R.id.tv_tech1)
        TextView tv_tech1;
        @Bind(R.id.tv_advise)
        TextView tv_advise;
        @Bind(R.id.tv_next_adviser)
        TextView tv_next_adviser;
        @Bind(R.id.ll_pro0)
        LinearLayout ll_pro0;
        @Bind(R.id.ll_pro1)
        LinearLayout ll_pro1;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
