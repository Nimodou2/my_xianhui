package com.maibo.lvyongsheng.xianhui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.maibo.lvyongsheng.xianhui.App;
import com.maibo.lvyongsheng.xianhui.OrderActivity;
import com.maibo.lvyongsheng.xianhui.PeopleMessageActivity;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.adapter.FinishOrderAdapter;
import com.maibo.lvyongsheng.xianhui.adapter.OrderPlanAdapter;
import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.maibo.lvyongsheng.xianhui.entity.UnFinishOrder;
import com.maibo.lvyongsheng.xianhui.implement.Util;
import com.maibo.lvyongsheng.xianhui.manager.OnRetryListener;
import com.maibo.lvyongsheng.xianhui.manager.OnShowHideViewListener;
import com.maibo.lvyongsheng.xianhui.manager.StatusLayoutManager;
import com.maibo.lvyongsheng.xianhui.myinterface.OnUnFinishItemListener;
import com.maibo.lvyongsheng.xianhui.view.MyFootView;
import com.maibo.lvyongsheng.xianhui.view.MyRefreshHeadView;
import com.maibo.lvyongsheng.xianhui.view.RecycleViewDivider;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import okhttp3.Call;

/**
 * Created by LYS on 2017/3/8.
 */

public class UnFinishedOrderFragment extends Fragment {
    private static final String TAG="unfinishifrag";
    private  Context mcontext;
    private AlertDialog alertDialog;
    private View rootView;
    @Bind(R.id.xrecy_finish)
    XRecyclerView xrecy_finish;
    FinishOrderAdapter adapter;
    SharedPreferences sp;
    String apiURL;
    String token;
    UnFinishOrder unFinishOrder;
    StatusLayoutManager statusManager;
    List<UnFinishOrder.DataBean.RowsBean> mRowsBeanMore;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    unFinishOrder = (UnFinishOrder) msg.obj;
                    List<UnFinishOrder.DataBean.RowsBean> mRowsBean = unFinishOrder.getData().getRows();
                    mRowsBeanMore.addAll(mRowsBean);
                    int pagerNumber = unFinishOrder.getData().getPageNumber();
                    int totalPage = unFinishOrder.getData().getTotalPage();
                    if (pagerNumber > 1) {
                        setMyAdapter(mRowsBeanMore);
                        xrecy_finish.loadMoreComplete();
                        if (pagerNumber == totalPage)
                            xrecy_finish.setNoMore(true);
                    } else {
                        mRowsBeanMore.clear();
                        mRowsBeanMore.addAll(mRowsBean);
                        setMyAdapter(mRowsBeanMore);
                        xrecy_finish.refreshComplete();
                    }
                    if (mRowsBean.size() > 0) statusManager.showContent();
                    else statusManager.showEmptyData();
                    break;
                case 2:
                    alertDialog.dismiss();
                    alertDialog=null;
                    break;
            }
        }
    };

    /**
     * 展示数据
     *
     * @param mRowsBean
     */
    private void setMyAdapter(List<UnFinishOrder.DataBean.RowsBean> mRowsBean) {
        adapter.setDatas(mRowsBean);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.all_main_fragment, null);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        setFiveViews();
        initView();
        initListener();
        initDatas(10, 1);
        EventBus.getDefault().register(this);
        return rootView;
    }

    /**
     * 初始化五种状态的界面
     */
    private void setFiveViews() {
        LinearLayout ll_all_main = (LinearLayout) rootView.findViewById(R.id.ll_all_main);
        statusManager = StatusLayoutManager.newBuilder(getContext())
                .contentView(R.layout.fragment_unfinished_order)
                .emptyDataView(R.layout.incloud_no_datas)
                .loadingView(R.layout.fragment_unfinished_order)
                .netWorkErrorView(R.layout.incloud_loading_error)
                .retryViewId(R.id.tv_retry)
                .onShowHideViewListener(new OnShowHideViewListener() {
                    @Override
                    public void onShowView(View view, int id) {

                    }

                    @Override
                    public void onHideView(View view, int id) {

                    }
                })
                .onRetryListener(new OnRetryListener() {
                    @Override
                    public void onRetry() {
                        initDatas(10, 1);
                    }
                })
                .build();
        ll_all_main.addView(statusManager.getRootLayout(), 0);
        ButterKnife.bind(this, rootView);
        statusManager.showContent();
    }

    /**
     * 初始化view
     */
    private void initView() {


        sp = getActivity().getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
        LinearLayoutManager manger = new LinearLayoutManager(getContext());
        xrecy_finish.setLayoutManager(manger);
        xrecy_finish.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayout.VERTICAL, 30,
                getResources().getColor(R.color.weixin_lianxiren_gray)));
        xrecy_finish.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xrecy_finish.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);

        MyRefreshHeadView myRefreshHeadView = new MyRefreshHeadView(getContext());
        myRefreshHeadView.setArrowImageView(R.mipmap.indicator_arrow);
        xrecy_finish.setRefreshHeader(myRefreshHeadView);
        xrecy_finish.setFootView(new MyFootView(getContext()));
        View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.incloud_no_datas, null);
        xrecy_finish.setEmptyView(emptyView);
        xrecy_finish.setAdapter(adapter = new FinishOrderAdapter(getContext(), 0));
        mRowsBeanMore = new ArrayList<>();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        xrecy_finish.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        token = sp.getString("token", null);
                        initDatas(10, 1);
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int pagerNumber = unFinishOrder.getData().getPageNumber();
                        int totalPage = unFinishOrder.getData().getTotalPage();
                        if (pagerNumber != 0 && totalPage != 0 && pagerNumber < totalPage) {
                            initDatas(10, pagerNumber + 1);
                        } else {
                            xrecy_finish.setNoMore(true);
                           // App.showToast(getContext(), "无更多数据");
                        }

                    }
                }, 1000);
            }
        });
        adapter.setOnClickUnFinishItem(new OnUnFinishItemListener() {
            @Override
            public void onAdviseListener(int position) {
                if (mRowsBeanMore.get(position).getPlan_list() != null && mRowsBeanMore.get(position).getPlan_list().size() > 0) {
                    initPopWindow(mRowsBeanMore.get(position).getPlan_list(), mRowsBeanMore.get(position).getCustomer_name());
                }else {
                   /* Log.e(TAG,"点击执行了");
                    if(alertDialog!=null){
                        return;
                    }*/
                    AlertDialog.Builder alertBuilder=new AlertDialog.Builder(mcontext);
                    alertBuilder.setTitle("提示:");
                    alertBuilder.setIcon(R.mipmap.testlogo);
                    alertBuilder.setMessage("暂无更多内容");
                    alertBuilder.setCancelable(false);
                    alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertBuilder.create();
                    alertDialog=alertBuilder.show();



                   /* Message message=Message.obtain();
                    message.what=2;
                    handler.sendMessageDelayed(message,1000);*/
                }

            }

            @Override
            public void onNextAdviserListener(int position, int customerID, String customerName) {
            }

            @Override
            public void onMyItemClickListener(int position, int customerID, String customerName) {
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra("customer_name", customerName);
                intent.putExtra("customer_id", customerID);
                intent.putExtra("date", mRowsBeanMore.get(position).getAdate());
                intent.putExtra("tag", 4);
                intent.putExtra("org_id",mRowsBeanMore.get(position).getOrg_id());
                startActivity(intent);
            }

            @Override
            public void onClickHeadListener(int position, int customerID) {
                Intent intent = new Intent(getActivity(), PeopleMessageActivity.class);
                intent.putExtra("customer_id", customerID);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    /**
     * 初始化数据
     */
    private void initDatas(int pageSize, int pageNumber) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getschedulelist")
                .addParams("token", token)
                .addParams("type", "undone")
                .addParams("pageSize", pageSize + "")
                .addParams("pageNumber", pageNumber + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        App.showToast(getContext(), "网络异常");
                        if (xrecy_finish != null) {
                            xrecy_finish.refreshComplete();
                            xrecy_finish.loadMoreComplete();
                        }
                        statusManager.showNetWorkError();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                        String status = jo.get("status").getAsString();
                        String message = jo.get("message").getAsString();
                        if (status.equals("ok")) {
                            Gson gson = new Gson();
                            UnFinishOrder mUFOrder = gson.fromJson(response, UnFinishOrder.class);
                            Message msg = Message.obtain();
                            msg.what = 0;
                            msg.obj = mUFOrder;
                            handler.sendMessage(msg);
                        } else {
                            App.showToast(getContext(), message);
                        }
                        if (xrecy_finish != null) {
                            xrecy_finish.refreshComplete();
                            xrecy_finish.loadMoreComplete();
                        }
                    }
                });
    }

    /**
     * 点击显示popwindow
     */
    public void initPopWindow(List<UnFinishOrder.DataBean.RowsBean.PlanListBean> planBean, String customerName) {
        View popView = getLayoutInflater(getArguments()).inflate(R.layout.popwindow_plan_advise, null);
        ListView lv_advise = (ListView) popView.findViewById(R.id.lv_advise);
        TextView tv_customer_name = (TextView) popView.findViewById(R.id.tv_customer_name);
        LinearLayout ll_close = (LinearLayout) popView.findViewById(R.id.ll_close);
        tv_customer_name.setText(customerName);
        final PopupWindow popupWindow = new PopupWindow(popView, 200, 300, true);
        popupWindow.setWidth(Util.getScreenWidth(getContext()) * 4 / 5);
        popupWindow.setHeight(Util.getScreenHeight(getContext()) * 29 / 50);
        popupWindow.showAtLocation(getLayoutInflater(getArguments()).inflate(R.layout.fragment_unfinished_order, null), Gravity.CENTER, 0, 50);
        backgroundAlpha(0.5f);
        ll_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new popupDismissListener()); //关闭事件
        popView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        lv_advise.setAdapter(new OrderPlanAdapter(getContext(), planBean));
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    class popupDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }

    }

    /**
     * 接收门店切换的通知
     *
     * @param eventDatas
     */
    public void onEvent(EventDatas eventDatas) {
        if (eventDatas.getTag().equals(Constants.SWITCH_STORES)) {
            token = sp.getString("token", null);
            apiURL = sp.getString("apiURL", null);
            initDatas(10, 1);
        }
    }
}
