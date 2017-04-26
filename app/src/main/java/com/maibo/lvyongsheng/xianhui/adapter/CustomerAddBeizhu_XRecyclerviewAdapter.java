package com.maibo.lvyongsheng.xianhui.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.google.gson.Gson;
import com.maibo.lvyongsheng.xianhui.App;
import com.maibo.lvyongsheng.xianhui.BeizhuConversationActivity;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.BeizhuAddForBack;
import com.maibo.lvyongsheng.xianhui.entity.BeizhuDetailBean;
import com.maibo.lvyongsheng.xianhui.entity.BeizhuListBean;
import com.maibo.lvyongsheng.xianhui.helperutils.BeizhuRecyclerAnimation;
import com.maibo.lvyongsheng.xianhui.helperutils.StringChange_Helper;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/4/14.
 */

public class CustomerAddBeizhu_XRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static  final String TAG="XRecyclerviewAdapter ";
    private String token;
    private String apiURL;
    private RequestQueue requestQueue;
    private Context context;
    private LayoutInflater inflater;
    private List<BeizhuListBean.DataBean> list_all;
    public static final int TYPE_NORMAL=0,TYPE_ADD=1;//2种类型
    private PopupWindow popuwindow;
    private int widththis;
    private static BeizhuRecyclerAnimation aimationThisListner;
    private StringChange_Helper stringChange_helper;
    private boolean haveSame=false;
    private  OptionsPickerView pvCustomOptions;//这个是选择器
    private String[] strChoice=new String[]{"年收入","身高体重","兴趣爱好","行为特征","饮食习惯","家族史","身体情况"};
    private List<String> choiceItem=new ArrayList<>();
    private AlertDialog alertDialog;
    public void initDataChoice(){
        for(int i=0;i<strChoice.length;i++){
            choiceItem.add(strChoice[i]);
        }
    }
    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    private int customer_id;
    // 0.职业1.地址2.正常3添加的按钮
    //设置监听 使外部能以又返回值的启动activity
    private OnAddClicklistner onAddClicklistner;
    private RecyclerView recyclerView;
    private boolean isHaveFoot=false;//判断是否含有add视图item
    public boolean isHaveFoot() {
        return isHaveFoot;
    }
    public   interface GetChange{
        public void getChangeResult(boolean result);
    }
    public List<BeizhuListBean.DataBean> getList_all() {
        return list_all;
    }
    public  GetChange getChange;
    public void setHaveFoot(boolean haveFoot,GetChange getChange) {
        this.getChange=getChange;
        isHaveFoot = haveFoot;
        if(isHaveFoot) {
            list_all.add(new BeizhuListBean.DataBean());
        }else {
            list_all.remove(list_all.size()-1);
        }
        getChange.getChangeResult(isHaveFoot);
        notifyDataSetChanged();
    }
    public void setAimationThisListner(BeizhuRecyclerAnimation aimationThisListner){
        this.aimationThisListner=aimationThisListner;
    }

    public interface OnAddClicklistner {
        public void onclick(RecyclerView recyclerview,View itemview,int position, List<BeizhuListBean.DataBean> data);
    }
    public void setOnAddClicklistner(OnAddClicklistner onAddClicklistner){
        this.onAddClicklistner=onAddClicklistner;
    }
    public CustomerAddBeizhu_XRecyclerviewAdapter(Context context, List<BeizhuListBean.DataBean> list_all) {
        this.context = context;
        this.list_all = list_all;
        inflater=LayoutInflater.from(context);
        this.token=context.getSharedPreferences("baseDate", Context.MODE_PRIVATE).getString("token", null);
        this.requestQueue=((App)context.getApplicationContext()).getRequestQueue();
        this.apiURL= context.getSharedPreferences("baseDate", Context.MODE_PRIVATE).getString("apiURL", null);
        stringChange_helper=StringChange_Helper.getInstance();
        initDataChoice();
    }

    @Override
    public int getItemViewType(int position) {
        if(position==list_all.size()-1&&isHaveFoot){
            return TYPE_ADD;
        }else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder_TypeContent(inflater.inflate(R.layout.beizhu_xrecycler_item_layout_content,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)){
                case TYPE_NORMAL:
                    initNormal(holder,position);
                    break;
                case TYPE_ADD:
                    initAdd(holder,position);
                    break;
                default:break;
            }
    }

    private void initAdd(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder_TypeContent myholder= (ViewHolder_TypeContent) holder;
        myholder.text_status.setText("添加后请选择数据类型");
        myholder.text_context.setVisibility(View.GONE);
        myholder.text_byuser.setVisibility(View.GONE);
        myholder.text_createtime.setVisibility(View.GONE);
        myholder.circle_image.setImageResource(R.mipmap.addbton_green);
        myholder.autor.setVisibility(View.VISIBLE);
        myholder.circle_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickerView();
                //showPopuwindow(recyclerView);
            }
        });
        ViewTreeObserver vto = myholder.autor.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                myholder.autor.getViewTreeObserver().removeOnPreDrawListener(this);
                widththis = myholder.autor.getMeasuredWidth();
                thissetAnimationToRight(myholder.itemView,widththis);
                return true;
            }
        });


    }

    private void initNormal(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder_TypeContent myholder= (ViewHolder_TypeContent) holder;
        if(list_all.get(position).getField_name()!=null){
            myholder.text_status.setText(list_all.get(position).getField_name());
        }

        if(list_all.get(position).getLast_add_name()!=null){
            myholder.text_byuser.setText((String)list_all.get(position).getLast_add_name());
        }else {
            if(list_all.get(position).getCreate_name()!=null){
                myholder.text_byuser.setText(list_all.get(position).getCreate_name());
            }else {
                myholder.text_byuser.setText("创建人未知");
            }
        }

        if(list_all.get(position).getLast_add_value()!=null){
            myholder.text_context.setText((String)list_all.get(position).getLast_add_value());
        }else {
            myholder.text_context.setText("暂无人员添加数据");
        }

        if(list_all.get(position).getLast_add_time()!=null){
            String change=stringChange_helper.getChangeDateForMD((String)list_all.get(position).getLast_add_time());
            myholder.text_createtime.setText(change);
        }else {
            if(list_all.get(position).getCreate_time()!=null){
                String change=stringChange_helper.getChangeDateForMD(list_all.get(position).getCreate_time());
                myholder.text_createtime.setText(change);
            }else {
                myholder.text_byuser.setText("创建时间未知");
            }
        }

        if(getChange!=null&&isHaveFoot){
            myholder.circle_image.setImageResource(R.mipmap.deletebton_red);
            myholder.autor.setVisibility(View.VISIBLE);
            ViewTreeObserver vto = myholder.autor.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    myholder.autor.getViewTreeObserver().removeOnPreDrawListener(this);
                    widththis = myholder.autor.getMeasuredWidth();
                    thissetAnimationToRight(myholder.itemView,widththis);
                    return true;
                }
            });
            myholder.itemView.setEnabled(false);
            myholder.circle_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog(position);
                    //deleteBeizhulistOne(token,position);
                }
            });
        }else if(getChange!=null&&!isHaveFoot&&recyclerView.getScrollState()==RecyclerView.SCROLL_STATE_IDLE){
            thissetAnimationToLeft(myholder.itemView,widththis);
            myholder.autor.setVisibility(View.VISIBLE);
        }
        if(!isHaveFoot){
            myholder.itemView.setEnabled(true);
            myholder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downBeizhuDetail(token,apiURL,position,0,0,null);
                }
            });
        }
    }

    //下载数据,并判断类型需不需要跳转0是点击item 后下载并跳转，1是增加后的下载并加入数据
    private void downBeizhuDetail(final String token, String apiURL, final int position, final int type, final int thisid, final String filename) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiURL + "/rest/employee/getcustomerextdetail", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e(TAG,s+"下载的回传");
                if (s != null) {
                    BeizhuDetailBean beizhudetail=new Gson().fromJson(s,BeizhuDetailBean.class);
                    if(type==0) {
                        Intent intent = new Intent(context, BeizhuConversationActivity.class);
                        intent.putExtra("bean", beizhudetail);
                        intent.putExtra("field_id",list_all.get(position).getField_id());
                        context.startActivity(intent);
                    }else {
                        BeizhuListBean.DataBean newdatabean=new BeizhuListBean.DataBean();
                        BeizhuDetailBean.DataBean detailast=beizhudetail.getData().get(beizhudetail.getData().size()-1);
                        newdatabean.setLast_add_time(detailast.getCreate_time());
                        newdatabean.setLast_add_value(detailast.getField_value());
                        newdatabean.setLast_add_name(detailast.getCreate_name());
                        newdatabean.setField_name(filename);
                        newdatabean.setField_id(thisid);
                        list_all.add(list_all.size()-1,newdatabean);
                        setHaveFoot(!isHaveFoot,getChange);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                {
                    if(type==0) {
                        map.put("token", token);
                        map.put("field_id", list_all.get(position).getField_id() + "");
                    }else {
                        map.put("token", token);
                        map.put("field_id", thisid+"");
                    }
                }
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }
    //增加一个条目
    public void addBeizhulistOne(final String token ,final String fielname){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiURL + "/rest/employee/addcustomerext", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s != null) {
                    BeizhuAddForBack back=new Gson().fromJson(s,BeizhuAddForBack.class);
                    if(back.getStatus().equals("ok")){
                        Log.e(TAG,"添加字段成功");
                        //downBeizhuDetail(token,apiURL,0,1,Integer.parseInt(back.getData().getField_id()),fielname);
                        downBeizhuListbean();
                    }else {
                        Toast.makeText(context,"网络出错，请重新添加...",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                {
                    map.put("token", token);
                    map.put("customer_id",customer_id+"");
                    map.put("field_name",fielname);
                }
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }
    //删除一条数据
    public void deleteBeizhulistOne (final String token, final int position){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiURL + "/rest/employee/removecustomerext", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        if(jsonObject.getString("status").equals("ok")){
                            Log.e(TAG,"删除成功");
                            list_all.remove(position);
                            alertDialog.dismiss();
                            notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                {
                    map.put("token", token);
                    map.put("field_id",list_all.get(position).getField_id()+"");
                }
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void showPickerView(){
        pvCustomOptions = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
               // String tx = cardItem.get(options1).getPickerViewText();
                String tx=choiceItem.get(options1);
                Log.e(TAG,"完成的点击回传"+tx);
                if(tx!=null&&tx.length()>0){
                    //开启网络上传
                    for(int i=0;i<list_all.size();i++){
                        if(list_all.get(i).getField_name()!=null&&list_all.get(i).getField_name().equals(tx)){
                            Toast.makeText(context,"该字段已经拥有",Toast.LENGTH_SHORT).show();
                            haveSame=true;
                        }
                    }
                    if(!haveSame) {
                        addBeizhulistOne(token, tx);
                        haveSame=false;
                    }else {
                        haveSame=false;
                    }
                }
            }
        }).setLayoutRes(R.layout.pickerview_for_beizhu, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.pickerview_for_beizhu_tv_done);
                        final EditText edit = (EditText) v.findViewById(R.id.pickerview_for_beizhu_edit_zdy);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.pickerview_for_beizhu_iv_cancle);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String edittext=edit.getText().toString().trim();
                                if(edittext!=null&&edittext.length()>0){
                                    //在这里执行上传
                                    for (int i=0;i<list_all.size();i++){
                                        if(list_all.get(i).getField_name()!=null&&list_all.get(i).getField_name().equals(edittext)){
                                            Toast.makeText(context,"该字段已经拥有",Toast.LENGTH_SHORT).show();
                                            haveSame=true;
                                        }
                                    }
                                    if(!haveSame){
                                        addBeizhulistOne(token,edittext);
                                        haveSame=false;
                                    }else {
                                        haveSame=false;
                                    }
                                    pvCustomOptions.dismiss();
                                }else {
                                    pvCustomOptions.returnData();
                                }
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.dismiss();
                            }
                        });
                    }
                })
                .isDialog(true)
                .build();
        pvCustomOptions.setPicker(choiceItem);//添加数据
        pvCustomOptions.show();
    }




    public void showPopuwindow(View showview){
        final View  popuview=inflater.inflate(R.layout.beizhuaddpopuwindow,null);
        popuwindow=new PopupWindow(popuview);
        popuwindow.setWidth(AbsListView.LayoutParams.MATCH_PARENT);
        popuwindow.setHeight(AbsListView.LayoutParams.WRAP_CONTENT);
        Button button_do= (Button) popuview.findViewById(R.id.beizhuaddpopuwindow_button_add);
        Button button_cancle= (Button) popuview.findViewById(R.id.beizhuaddpopuwindow_button_cancle);
        final EditText edit= (EditText) popuview.findViewById(R.id.beizhuaddpopuwindow_edit_field_name);
        button_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String field_value=edit.getText().toString().trim();
                if(field_value!=null&&field_value.length()>0){
                    addBeizhulistOne(token,field_value);
                    //popuwindow.dismiss();
                }
            }
        });
        button_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuwindow.dismiss();
            }
        });
        popuwindow.setFocusable(true);
        //表示点击除popupWindow之外的空白处时，窗口是否消失
        popuwindow.setOutsideTouchable(false);
        //设置弹出窗体需要软键盘，
        popuwindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//再设置模式，和Activity的一样，覆盖，调整大小。
        popuwindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //popupWindow.setAnimationStyle(R.style.donghua);设置动画
        popuwindow.showAtLocation(showview, Gravity.BOTTOM, 0, 0);
    }
    public void downBeizhuListbean(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiURL + "/rest/employee/getcustomerextlist", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s != null) {
                    Log.e(TAG,"重新下载的回     "+s);
                    BeizhuListBean beizhuListBean=new Gson().fromJson(s,BeizhuListBean.class);
                    List<BeizhuListBean.DataBean> newlistbean=new ArrayList<>();
                    if(beizhuListBean.getData().size()==1){
                        newlistbean.add(beizhuListBean.getData().get(0));
                    }else {
                        for (int i = list_all.size()-1; i < beizhuListBean.getData().size(); i++) {
                            newlistbean.add(beizhuListBean.getData().get(i));
                        }
                    }
                    for(int i=0;i<newlistbean.size();i++){
                        list_all.add(list_all.size()-1,newlistbean.get(i));
                    }
                    setHaveFoot(false,getChange);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                {
                    map.put("token",token);
                    map.put("customer_id",customer_id+"");
                }
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }
    @Override
    public int getItemCount() {

        return list_all==null ? 0:list_all.size();
    }

    public class  ViewHolder_TypeContent extends RecyclerView.ViewHolder{
        private TextView text_status;
        private TextView text_context;
        private TextView text_createtime;
        private TextView text_byuser;
        private CircleImageView circle_image;
        private AutoRelativeLayout  autor;
        public ViewHolder_TypeContent(View itemView) {
            super(itemView);
            //加这句话才有用
            AutoUtils.autoSize(itemView);
            text_byuser= (TextView) itemView.findViewById(R.id.beizhu_xrecyclerview_item_layout_content_textbyusername);
            text_context= (TextView) itemView.findViewById(R.id.beizhu_xrecyclerview_item_layout_content_textnewcotext);
            text_createtime= (TextView) itemView.findViewById(R.id.beizhu_xrecyclerview_item_layout_content_textcreatetime);
            text_status=(TextView)itemView.findViewById(R.id.beizhu_xrecyclerview_item_layout_content_textstatus);
            circle_image= (CircleImageView) itemView.findViewById(R.id.beizhu_xrecyclerview_item_layout_content_cirimage);
            autor= (AutoRelativeLayout) itemView.findViewById(R.id.beizhu_xrecyclerview_item_layout_content_autor);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView=recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        //资源回收
        if(recyclerView!=null&&onAddClicklistner!=null){
            recyclerView=null;
            onAddClicklistner=null;
        }
    }
    //添加新加入的
/*    public void setBeizhuList(int position, List<BeizhuListBean.DataBean> listthis){
       *//* list_all.get(position).getBeizhuBeenlist().clear();
        list_all.get(position).getBeizhuBeenlist().addAll(listthis);*//*
        list_all.get(position).setBeizhuBeenlist(listthis);
        notifyDataSetChanged();
    }*/

    public void thissetAnimationToRight(View v,int with){
        if(v.getAnimation()==null) {
            ObjectAnimator oar3 = ObjectAnimator.ofFloat(v, "translationX", new float[]{-with, 0});
            oar3.setDuration(300);
            oar3.setRepeatCount(0);
            oar3.setRepeatMode(ObjectAnimator.REVERSE);
            oar3.start();
        }
    }

    public void thissetAnimationToLeft(View v,int with){
        if(v.getAnimation()==null) {
            ObjectAnimator oar3 = ObjectAnimator.ofFloat(v, "translationX", new float[]{0, -with});
            oar3.setDuration(300);
            oar3.setRepeatCount(0);
            oar3.setRepeatMode(ObjectAnimator.REVERSE);
            oar3.start();
        }
    }
    public void setList_all(List<BeizhuListBean.DataBean> list_all) {
        this.list_all = list_all;
        notifyDataSetChanged();
    }

    public void showDeleteDialog(final int position){
        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(context);
        alertBuilder.setTitle("提示:");
        alertBuilder.setIcon(R.mipmap.testlogo);
        alertBuilder.setMessage("确定要删除这个备注么？");
        alertBuilder.create();//创建这个对话框
        //为对话框设置消极、积极、与中立 的按钮（是接口自定义的我们直接用）
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteBeizhulistOne(token,position);
            }
        });
        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog=alertBuilder.show();//显示这个对话框
    }
}
