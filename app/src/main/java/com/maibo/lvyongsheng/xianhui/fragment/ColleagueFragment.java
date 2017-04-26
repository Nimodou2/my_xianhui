package com.maibo.lvyongsheng.xianhui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.AllMessageActivity;
import com.maibo.lvyongsheng.xianhui.App;
import com.maibo.lvyongsheng.xianhui.OrderActivity;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.adapter.ColleagueAdapter;
import com.maibo.lvyongsheng.xianhui.adapter.MyGridViewAdapter;
import com.maibo.lvyongsheng.xianhui.entity.Employee;
import com.maibo.lvyongsheng.xianhui.entity.Order;
import com.maibo.lvyongsheng.xianhui.entity.SelectEntity;
import com.maibo.lvyongsheng.xianhui.entity.SelectEntitys;
import com.maibo.lvyongsheng.xianhui.implement.MyProgressDialog;
import com.maibo.lvyongsheng.xianhui.view.WorkRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by LYS on 2016/9/29.
 */
public class ColleagueFragment extends Fragment implements WorkRefreshListView.OnRefreshListener{
    WorkRefreshListView lv_customer_list;
    SharedPreferences sp;
    String token,apiURL;
    List<Employee> list1;
    List<Order> collOrder;
    Employee employee;
    ColleagueAdapter myAdapter;
    MyProgressDialog myDialog;
    ProgressDialog dialog;
    //+++++++++++++++++++++++++++++++++++++++++++++++++++
    TextView tv_open_close;
    List<SelectEntitys> ses;
    List<SelectEntity> seTwoList;
    private PopupWindow popupWindow;
    //标记是否展开
    int isOpen=0;
    //记录点击状态
    String buffer1="";
    String buffer2="";
    String buffer3="";
    MyGridViewAdapter adapter1,adapter2,adapter3;
    GridView gd_list,gd_list2,gd_list3,gd_list4;
    TextView tv_choose_result;
    Boolean isLoadingMore=false;
    int currentPageNum;
    int totalPage;

    int screenHeight;
    //-----------------------------------------------------

    Handler  handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case 1:
                   List<Employee> list=(List<Employee>) msg.obj;
                   currentPageNum=msg.arg1;
                   totalPage=msg.arg2;
                   if (isLoadingMore&&list!=null){
                       list1.addAll(list);
                       myAdapter.notifyDataSetChanged();
                   }else{
                       list1.clear();
                       list1=list;
                       lv_customer_list.setAdapter(myAdapter=new ColleagueAdapter(getContext(),list1,0));
                   }
                   lv_customer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                           employee=list1.get(i-1);
                           Intent intent = new Intent(getActivity(), AllMessageActivity.class);
                           Bundle bundle=new Bundle();
                           bundle.putSerializable("Employee",employee);
                           intent.putExtras(bundle);
                           intent.putExtra("tag",1);
                           startActivity(intent);
                       }
                   });
                   lv_customer_list.completeRefresh();
                   break;
               case 2:
                   collOrder=(List<Order>) msg.obj;
                   //跳转到订单页面
                   Intent intent = new Intent(getActivity(), OrderActivity.class);
                   Bundle bundle=new Bundle();
                   bundle.putSerializable("collOrder",(Serializable) collOrder);
                   bundle.putSerializable("Employee",(Serializable) employee);
                   intent.putExtra("collName",employee.getDisplay_name());
                   intent.putExtras(bundle);
                   intent.putExtra("tag",2);
                   startActivity(intent);
                   break;
               case 3:
                   ses=(List<SelectEntitys>) msg.obj;
                   //用于展开和闭合
                   SelectEntitys seTwo=ses.get(1);
                   seTwoList=new ArrayList<>();
                   if (msg.arg1==1){
                       tv_choose_result.setText("筛选结果："+ses.get(0).getFilterResult()+"人");
                   }
                   if (ses.get(1).getList().size()>6){
                       for (int i=0;i<6;i++){
                           seTwoList.add(seTwo.getList().get(i));
                       }
                   }else{
                       seTwoList=seTwo.getList();
                   }
                   if (gd_list!=null)
                       gd_list.setAdapter(adapter1=new MyGridViewAdapter(getActivity(),ses.get(0).getList()));
                   if (gd_list2!=null){
                       if (isOpen==0){
                           gd_list2.setAdapter(adapter2=new MyGridViewAdapter(getActivity(),seTwoList));
                       }else{
                           gd_list2.setAdapter(adapter2=new MyGridViewAdapter(getActivity(),ses.get(1).getList()));
                       }
                   }
                   if (gd_list3!=null)
                       gd_list3.setAdapter(adapter1=new MyGridViewAdapter(getActivity(),ses.get(2).getList()));
                   break;
           }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer,container,false);
        list1=new ArrayList<>();
        myDialog=new MyProgressDialog(getActivity());
        //屏幕高度
     /*   WorkActivity parentActivity= (WorkActivity) getActivity();
        screenHeight= Util.getScreenHeight(getContext())-parentActivity.getStatusBarHeight();*/
//        FourStep_Activity parent_activity= (FourStep_Activity) getActivity();
      //  screenHeight=Util.getScreenHeight(getContext()) - parent_activity.getStatusBarHeight();
//        WindowManager.LayoutParams params=myDialog.getWindow().getAttributes();
////        params.y=50;
////        params.x=50;
//        params.gravity=Gravity.BOTTOM;
//        myDialog.getWindow().setAttributes(params);
        dialog=new ProgressDialog(getActivity());
        dialog.setMessage("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
        lv_customer_list =(WorkRefreshListView) view.findViewById(R.id.lv_customer_list);
        lv_customer_list.setOnRefreshListener(this);
        sp=getActivity().getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token=sp.getString("token",null);
        apiURL=sp.getString("apiURL",null);
        getServiceData("","","",0,1);
        getChooseData("","","",0);
        return view;
    }


    @Override
    public void onPullRefresh() {
        //下拉刷新
        isLoadingMore=false;
        getServiceData("","","",0,1);
    }

    @Override
    public void onLoadingMore() {
        //上滑加载更多
        isLoadingMore=true;
        if (currentPageNum!=totalPage){
            getServiceData("","","",0,currentPageNum+1);
        }else{
            App.showToast(getActivity(),"已加载全部!");
            lv_customer_list.completeRefresh();
        }
    }

    //获取助手同事列表
    public void getServiceData(String org_id, String project_id, String entry_time, final int isDialog,int pageNum){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/gethelperworkerlist")
                .addParams("token",token)
                .addParams("org_id",org_id)
                .addParams("project_id",project_id)
                .addParams("entry_time",entry_time)
                .addParams("pageNumber",pageNum+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
//                        Log.e("同事:",response);
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String msg_status=jsonObject.get("status").getAsString();
                        String message=jsonObject.get("message").getAsString();
                        if (msg_status.equals("ok")){
                        JsonObject data=jsonObject.get("data").getAsJsonObject();
                        int pageNumber=-1;
                        int totalPage=-1;
                            if (!data.get("pageNumber").isJsonNull())
                                pageNumber=data.get("pageNumber").getAsInt();
                            if (!data.get("totalPage").isJsonNull())
                                totalPage=data.get("totalPage").getAsInt();
                            JsonArray rows = data.get("rows").getAsJsonArray();
                            if (rows!=null){
                            List<Employee> list=new ArrayList<>();
                        for (JsonElement jsonElement:rows){
                            JsonObject jo=jsonElement.getAsJsonObject();
                            String job="";
                            boolean user_level=false;
                            String mobile="";
                            String user_code="";
                            String entry_date="";
                            String org_name="";
                            int org_id=-1;
                            int user_id=-1;
                            String display_name="";
                            String avator_url="";
                            String guid="";
                            int status=-1;
                            Double project_hours=-1.00;
                            int project_qty=-1;
                            String schedule_time="";
                            if (!jo.get("job").isJsonNull())
                                job=jo.get("job").getAsString();
                            if (!jo.get("user_level").isJsonNull())
                                user_level=jo.get("user_level").getAsBoolean();
                            if (!jo.get("mobile").isJsonNull())
                                mobile=jo.get("mobile").getAsString();
                            if (!jo.get("user_code").isJsonNull())
                                user_code=jo.get("user_code").getAsString();
                            if (!jo.get("entry_date").isJsonNull())
                                entry_date=jo.get("entry_date").getAsString();
                            if (!jo.get("org_name").isJsonNull())
                                org_name=jo.get("org_name").getAsString();
                            if (!jo.get("org_id").isJsonNull())
                                org_id=jo.get("org_id").getAsInt();
                            if (!jo.get("user_id").isJsonNull())
                                user_id=jo.get("user_id").getAsInt();
                            if (!jo.get("display_name").isJsonNull())
                                display_name=jo.get("display_name").getAsString();
                            if (!jo.get("avator_url").isJsonNull())
                                avator_url=jo.get("avator_url").getAsString();
                            if (!jo.get("guid").isJsonNull())
                                guid=jo.get("guid").getAsString();
                            if (!jo.get("status").isJsonNull())
                                status=jo.get("status").getAsInt();
                            if (!jo.get("project_hours").isJsonNull())
                                project_hours=jo.get("project_hours").getAsDouble();
                            if (!jo.get("project_qty").isJsonNull())
                                project_qty=jo.get("project_qty").getAsInt();
                            if (!jo.get("schedule_time").isJsonNull())
                                schedule_time=jo.get("schedule_time").getAsString();

                            list.add(new Employee(user_id,display_name,avator_url,job,user_level,mobile,user_code,
                                    entry_date,org_name,status,project_hours,project_qty,schedule_time));
                        }
                        Message msg=Message.obtain();
                        msg.what=1;
                        msg.obj=list;
                        msg.arg1=pageNumber;
                        msg.arg2=totalPage;
                        handler.sendMessage(msg);
                        if (isDialog==1){
                            dialog.dismiss();
                        }
                    }
                        }else{
                            App.showToast(getActivity(),message);
                        }
                    }
                });
    }
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //获取筛选条件
    public void getChooseData(String org_id, String project_id, String entry_time, final int isDialog){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/gethelperworkerlistsearchinfo")
                .addParams("token",token)
                .addParams("org_id",org_id)
                .addParams("project_id",project_id)
                .addParams("entry_time",entry_time)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject=new JsonParser().parse(response).getAsJsonObject();
                        JsonArray data=jsonObject.get("data").getAsJsonArray();
                        List<SelectEntitys> entitys_list=new ArrayList<SelectEntitys>();
                        for (JsonElement je:data){
                            String name="";
                            String param="";
                            String filterResult="";
                            JsonObject jo=je.getAsJsonObject();
                            if (!jo.get("name").isJsonNull()){
                                name=jo.get("name").getAsString();
                            }
                            if (!jo.get("param").isJsonNull()){
                                param=jo.get("param").getAsString();
                            }
                            if (!jo.get("filterResult").isJsonNull()){
                                filterResult=jo.get("filterResult").getAsString();
                            }
                            JsonArray list=jo.get("list").getAsJsonArray();
                            List<SelectEntity> entity_list=new ArrayList<SelectEntity>();
                            for (JsonElement je2:list){
                                String value="";
                                String text="";
                                Boolean selected=false;
                                Boolean disabled=false;
                                JsonObject jo2=je2.getAsJsonObject();
                                if (!jo2.get("value").isJsonNull()){
                                    value=jo2.get("value").getAsString();
                                }
                                if (!jo2.get("text").isJsonNull()){
                                    text=jo2.get("text").getAsString();
                                }
                                if (jo2.has("selected")){
                                    if (!jo2.get("selected").isJsonNull()){
                                        selected=jo2.get("selected").getAsBoolean();
                                    }
                                }else if (jo2.has("disabled")){
                                    if (!jo2.get("disabled").isJsonNull()){
                                        disabled=jo2.get("disabled").getAsBoolean();
                                    }
                                }

                                entity_list.add(new SelectEntity(value,text,selected,disabled));
                            }
                            entitys_list.add(new SelectEntitys(name,param,filterResult,entity_list));

                        }
                        Message msg=Message.obtain();
                        msg.what=3;
                        msg.obj=entitys_list;
                        msg.arg1=0;
                        if (isDialog==1){
                            msg.arg1=1;
                            myDialog.dismiss();
                        }
                        handler.sendMessage(msg);
                    }
                });
    }
    public void initPopupWindow(){
        View popupWindowView = getLayoutInflater(getArguments()).inflate(R.layout.style_pop, null);
        //设置popwindow的宽和高
        popupWindow = new PopupWindow(popupWindowView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height=wm.getDefaultDisplay().getHeight();
        //设置popwindow出现和消失的动画效果
        popupWindow.setAnimationStyle(R.style.AnimationRightFade);
        //菜单背景色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(dw);
        //显示位置
        //宽度
        popupWindow.setWidth(width*6/7);
        //高度
        popupWindow.setHeight(height-getStatusBarHeight());
        popupWindow.showAtLocation(getLayoutInflater(getArguments()).inflate(R.layout.fragment_customer, null), Gravity.RIGHT, 0,0);
        //设置背景半透明
        backgroundAlpha(0.5f);
        //关闭事件
        popupWindow.setOnDismissListener(new popupDismissListener());

        popupWindowView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*if( popupWindow!=null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                    popupWindow=null;
                }*/
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });
        contralPopWindowData(popupWindowView,popupWindow);
    }
    private void contralPopWindowData(View popupWindowView,final PopupWindow popupWindow) {
        //获取popwindow里面的组件
        gd_list= (GridView) popupWindowView.findViewById(R.id.gd_list);
        gd_list2= (GridView) popupWindowView.findViewById(R.id.gd_list2);
        gd_list3= (GridView) popupWindowView.findViewById(R.id.gd_list3);
        gd_list4= (GridView) popupWindowView.findViewById(R.id.gd_list4);
        TextView tv_name1= (TextView) popupWindowView.findViewById(R.id.tv_name1);
        TextView tv_name2= (TextView) popupWindowView.findViewById(R.id.tv_name2);
        TextView tv_name3= (TextView) popupWindowView.findViewById(R.id.tv_name3);
        TextView tv_name4= (TextView) popupWindowView.findViewById(R.id.tv_name4);
        tv_open_close = (TextView) popupWindowView.findViewById(R.id.tv_open_close);
        tv_choose_result= (TextView) popupWindowView.findViewById(R.id.tv_choose_tesult);
        if (ses.size()>0)
            tv_choose_result.setText("筛选结果："+ses.get(0).getFilterResult()+"人");

        gd_list.setAdapter(adapter1=new MyGridViewAdapter(getActivity(),ses.get(0).getList()));
        tv_name1.setText(ses.get(0).getName());
        gd_list2.setAdapter(adapter2=new MyGridViewAdapter(getActivity(),seTwoList));
        tv_name2.setText(ses.get(1).getName());
        gd_list3.setAdapter(adapter3=new MyGridViewAdapter(getActivity(),ses.get(2).getList()));
        tv_name3.setText(ses.get(2).getName());
        gd_list4.setVisibility(View.GONE);
        tv_name4.setVisibility(View.GONE);
        //记录点击状态
        gd_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /**
                 * 需求：1、保留刷新前状态；2、保留其它项选中状态；
                 *
                 */
                if (!ses.get(0).getList().get(i).getDisabled()){
                    myDialog.show();
                }
                if (!ses.get(0).getList().get(i).getSelected()&&!ses.get(0).getList().get(i).getDisabled()){
                    //刷新适配器，显示筛选结果
                    buffer1 +=","+ses.get(0).getList().get(i).getValue();
                    String values11=buffer1.substring(1);
                    String values22="";
                    String values33="";
                    if (buffer2.length()>0)
                        values22=buffer2.substring(1);
                    if (buffer3.length()>0)
                        values33=buffer3.substring(1);
                    getChooseData(values11,values22,values33,1);
                    //getServiceData(values11,values22,values33);
                }else if (ses.get(0).getList().get(i).getSelected()&&!ses.get(0).getList().get(i).getDisabled()){
                    //刷新适配器，显示筛选结果
                    buffer1=buffer1.replace(","+ses.get(0).getList().get(i).getValue(),"");
                    String values11="";
                    String values22="";
                    String values33="";
                    if (buffer1.lastIndexOf(",")!=-1){
                        values11=buffer1.substring(1);
                    }
                    if (buffer2.length()>0)
                        values22=buffer2.substring(1);
                    if (buffer3.length()>0)
                        values33=buffer3.substring(1);
                    getChooseData(values11,values22,values33,1);
                    //getServiceData(values11,values22,values33);
                }

            }
        });

        gd_list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!ses.get(1).getList().get(i).getDisabled()){
                    myDialog.show();
                }
                if (!ses.get(1).getList().get(i).getSelected()&&!ses.get(1).getList().get(i).getDisabled()){
                    //刷新适配器，显示筛选结果
                    buffer2 +=","+ses.get(1).getList().get(i).getValue();
                    String values11="";
                    String values22=buffer2.substring(1);
                    String values33="";
                    if (buffer1.length()>0)
                        values11=buffer1.substring(1);
                    if (buffer3.length()>0)
                        values33=buffer3.substring(1);
                    getChooseData(values11,values22,values33,1);
                    //getServiceData(values11,values22,values33);
                }else if (ses.get(1).getList().get(i).getSelected()&&!ses.get(1).getList().get(i).getDisabled()){
                    //刷新适配器，显示筛选结果
                    buffer2=buffer2.replace(","+ses.get(1).getList().get(i).getValue(),"");
                    String values11="";
                    String values22="";
                    String values33="";
                    if (buffer2.lastIndexOf(",")!=-1){
                        values22=buffer2.substring(1);
                    }
                    if (buffer1.length()>0)
                        values11=buffer1.substring(1);
                    if (buffer3.length()>0)
                        values33=buffer3.substring(1);
                    getChooseData(values11,values22,values33,1);
                    //getServiceData(values11,values22,values33);
                }
            }
        });
        gd_list3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!ses.get(2).getList().get(i).getDisabled()){
                    myDialog.show();
                }
                if (!ses.get(2).getList().get(i).getSelected()&&!ses.get(2).getList().get(i).getDisabled()){
                    //刷新适配器，显示筛选结果
                    buffer3 +=","+ses.get(2).getList().get(i).getValue();
                    String values11="";
                    String values22="";
                    String values33=buffer3.substring(1);
                    if (buffer1.length()>0)
                        values11=buffer1.substring(1);
                    if (buffer2.length()>0)
                        values22=buffer2.substring(1);
                    getChooseData(values11,values22,values33,1);
                    //getServiceData(values11,values22,values33);
                }else if (ses.get(2).getList().get(i).getSelected()&&!ses.get(2).getList().get(i).getDisabled()){
                    //刷新适配器，显示筛选结果
                    buffer3=buffer3.replace(","+ses.get(2).getList().get(i).getValue(),"");
                    String values11="";
                    String values22="";
                    String values33="";
                    if (buffer3.lastIndexOf(",")!=-1){
                        values33=buffer3.substring(1);
                    }
                    if (buffer1.length()>0)
                        values11=buffer1.substring(1);
                    if (buffer2.length()>0)
                        values22=buffer2.substring(1);
                    getChooseData(values11,values22,values33,1);
                    //getServiceData(values11,values22,values33);
                }
            }
        });
        Button reset= (Button) popupWindowView.findViewById(R.id.reset);
        Button certain= (Button) popupWindowView.findViewById(R.id.certain);
        //判断是否显示展开
        if (ses.get(1).getList().size()>6){
            tv_open_close.setVisibility(View.VISIBLE);
        }
        tv_open_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen==0){
                    //点击展开
                    tv_open_close.setText("闭合");
                    gd_list2.setAdapter(adapter2=new MyGridViewAdapter(getActivity(),ses.get(1).getList()));
                    isOpen=1;
                }else if (isOpen==1){
                    //闭合
                    tv_open_close.setText("展开");
                    gd_list2.setAdapter(adapter2=new MyGridViewAdapter(getActivity(),seTwoList));
                    isOpen=0;
                }
            }
        });

        //重置
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //带空参数重新请求服务器
                myDialog.show();
                getChooseData("","","",1);
              buffer1="";
              buffer2="";
              buffer3="";

            }
        });
        //确定
        certain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                //一次性筛选
                String values11="";
                String values22="";
                String values33="";
                if (buffer1.lastIndexOf(",")!=-1){
                    values11=buffer1.substring(1);
                }
                if (buffer2.lastIndexOf(",")!=-1){
                    values22=buffer2.substring(1);
                }
                if (buffer3.lastIndexOf(",")!=-1){
                    values33=buffer3.substring(1);
                }
                isLoadingMore=false;
                getServiceData(values11,values22,values33,1,1);
                popupWindow.dismiss();

            }
        });
    }
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }
    class popupDismissListener implements PopupWindow.OnDismissListener{

        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }

    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    //----------------------------------------------------------------

}
