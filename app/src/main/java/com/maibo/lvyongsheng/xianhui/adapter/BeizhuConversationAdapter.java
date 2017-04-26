package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.maibo.lvyongsheng.xianhui.App;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.BeizhuDetailBean;
import com.maibo.lvyongsheng.xianhui.helperutils.StringChange_Helper;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/14.
 */

public class BeizhuConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Integer>  jt_list;
    private List<Integer>  zt_list;
    private List<Integer>  qt_list;
    private static final int IMVT_COM_MSG = 0,IMVT_TO_MSG = 1,TIME_JT=2,TIME_ZT=3,TIME_QT=4,TIME_NORMAL=5;
    //ListView视图的内容由IMsgViewType决定

    private RequestQueue requestQueue;
    private static final String TAG = "BeiConversationAdapter";
    private Context context;
    private LayoutInflater mInflater;
    private int uid;
    private BeizhuDetailBean data;
    private List<BeizhuDetailBean.DataBean> data_list;
    private StringChange_Helper stringChange_helper;
    public BeizhuConversationAdapter(Context context, BeizhuDetailBean data, int uid) {
        this.context = context;
        this.data = data;
        mInflater = LayoutInflater.from(context);
        this.uid=uid;
        if(data!=null) {
            data_list = data.getData();
        }
        requestQueue=((App)context.getApplicationContext()).getRequestQueue();
        stringChange_helper=StringChange_Helper.getInstance();
        jt_list=new ArrayList<>();
        zt_list=new ArrayList<>();
        qt_list=new ArrayList<>();
    }
    public BeizhuDetailBean getData() {
        return data;
    }

    public void setData(BeizhuDetailBean data) {
        this.data = data;
        data_list=data.getData();
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        if(position%2==0){
            String time=data_list.get(position/2).getCreate_time();
            int result=stringChange_helper.getCompailWhithSysTime(time);
            if(result==0){//今天
                jt_list.add(position);
                return TIME_JT;
            }else if(result==1){
                zt_list.add(position);
                return TIME_ZT;
            }else if(result==2){
                qt_list.add(position);
               return TIME_QT;
            }else{
                return TIME_NORMAL;
            }
        }else {
            int messageid = data_list.get((position-1)/2).getCreate_uid();
            if (messageid != uid) {
                return IMVT_COM_MSG;//对方发来的信息
            } else {
                return IMVT_TO_MSG;//自己发出的信息
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==IMVT_COM_MSG){
            return new ViewHolder(mInflater.inflate(R.layout.chatting_item_msg_text_left,parent,false));
        }else if(viewType==IMVT_TO_MSG){
            return new ViewHolder(mInflater.inflate(R.layout.chatting_item_msg_text_right,parent,false));
        }else {
            return new ViewHolder_Time(mInflater.inflate(R.layout.beizhuconversation_time_layout,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = null;
        ViewHolder_Time viewHolder_time=null;
        if(getItemViewType(position)==TIME_JT){

            viewHolder_time = (ViewHolder_Time) holder;
            BeizhuDetailBean.DataBean thisdata = data_list.get(position / 2);
            if(position==jt_list.get(0)) {
                viewHolder_time.text_time.setText(stringChange_helper.getMy(thisdata.getCreate_time()));
            }else {
                viewHolder_time.text_time.setText(stringChange_helper.getOnlyTime(thisdata.getCreate_time()));
            }
        }else if(getItemViewType(position)==TIME_ZT){
            viewHolder_time= (ViewHolder_Time) holder;
            BeizhuDetailBean.DataBean thisdata=data_list.get(position/2);
            if(position==zt_list.get(0))
            {
                viewHolder_time.text_time.setText(stringChange_helper.getMy(thisdata.getCreate_time()));
            }else {
                viewHolder_time.text_time.setText(stringChange_helper.getOnlyTime(thisdata.getCreate_time()));
            }
        }else if(getItemViewType(position)==TIME_QT){
            viewHolder_time= (ViewHolder_Time) holder;
            BeizhuDetailBean.DataBean thisdata=data_list.get(position/2);
            if(qt_list.get(0)==position){
                viewHolder_time.text_time.setText(stringChange_helper.getMy(thisdata.getCreate_time()));
            }else {
                viewHolder_time.text_time.setText(stringChange_helper.getOnlyTime(thisdata.getCreate_time()));
            }
        }else if(getItemViewType(position)==TIME_NORMAL){
            viewHolder_time= (ViewHolder_Time) holder;
            BeizhuDetailBean.DataBean thisdata=data_list.get(position/2);
            viewHolder_time.text_time.setText(stringChange_helper.getMy(thisdata.getCreate_time()));
        } else {
            viewHolder= (ViewHolder) holder;
            BeizhuDetailBean.DataBean thisdata=data_list.get((position-1)/2);
            if (thisdata.getCreate_name() != null) {
                viewHolder.tvUserName.setText(thisdata.getCreate_name());
            }
            if (thisdata.getField_value() != null) {
                viewHolder.tvContent.setText(thisdata.getField_value());
            }
            if (thisdata.getAvator_url() != null) {
                final ViewHolder finalViewHolder = viewHolder;
                ImageRequest imageRequest = new ImageRequest(thisdata.getAvator_url(), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        finalViewHolder.image_aval.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //设置加载失败图片
                        finalViewHolder.image_aval.setImageResource(R.mipmap.appointment_img);
                    }
                });
                requestQueue.add(imageRequest);
            }
        }

    }

    @Override
    public int getItemCount() {
        return data==null? 0:data_list.size()*2;
    }

    public class ViewHolder_Time extends RecyclerView.ViewHolder{
        private TextView text_time;
        public ViewHolder_Time(View itemView) {
            super(itemView);
            AutoUtils.autoSize(itemView);
            text_time= (TextView) itemView.findViewById(R.id.beizhuconversation_time_layout_text_time);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvUserName;
        private TextView tvContent;
        private ImageView image_aval;
        public ViewHolder(View itemView) {
            super(itemView);
            AutoUtils.autoSize(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.chatting_item_msg_text_left_text_username);
            tvContent = (TextView) itemView.findViewById(R.id.chatting_item_msg_text_left_text_content);
            image_aval= (ImageView) itemView.findViewById(R.id.chatting_item_msg_text_left_image_useraval);
        }
    }
    public void setBeizhuDetailData(List<BeizhuDetailBean.DataBean> setdata){
        if(setdata!=null){
            data_list.addAll(setdata);
            notifyDataSetChanged();
        }
    }
    public int getBeizhuDatalistSize(){
        return data_list.size();
    }
}
