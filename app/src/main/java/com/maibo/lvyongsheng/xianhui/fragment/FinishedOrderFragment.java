package com.maibo.lvyongsheng.xianhui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.maibo.lvyongsheng.xianhui.AddTabActivity;
import com.maibo.lvyongsheng.xianhui.App;
import com.maibo.lvyongsheng.xianhui.OrderActivity;
import com.maibo.lvyongsheng.xianhui.PeopleMessageActivity;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.adapter.FinishOrderAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import okhttp3.Call;

import static com.maibo.lvyongsheng.xianhui.utils.ZzLetterSideBar.TAG;

/**
 * Created by LYS on 2017/3/8.
 */

public class FinishedOrderFragment extends Fragment {
    private View rootView;
    @Bind(R.id.xrecy_finish)
    XRecyclerView xrecy_finish;
    FinishOrderAdapter adapter;
    SharedPreferences sp;
    String apiURL;
    String token;
    StatusLayoutManager statusManager;
    UnFinishOrder unFinishOrder;
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
                        if (pagerNumber == totalPage)
                            xrecy_finish.setNoMore(true);
                    } else {
                        mRowsBeanMore.clear();
                        mRowsBeanMore.addAll(mRowsBean);
                        setMyAdapter(mRowsBeanMore);
                    }
                    if (mRowsBean.size() > 0) statusManager.showContent();
                    else statusManager.showEmptyData();
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
        xrecy_finish.setAdapter(adapter = new FinishOrderAdapter(getContext(), 1));
        mRowsBeanMore = new ArrayList<>();
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
     * 初始化监听器
     */
    private void initListener() {
        xrecy_finish.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
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
                            App.showToast(getContext(), "无更多数据");
                        }

                    }
                }, 1000);
            }
        });
        adapter.setOnClickUnFinishItem(new OnUnFinishItemListener() {
            @Override
            public void onAdviseListener(int position) {
                if (mRowsBeanMore.get(position).getPlan_list() != null)
                    initPopWindow(mRowsBeanMore.get(position).getPlan_list());
            }
            //下次建议的点击反馈
            @Override
            public void onNextAdviserListener(int position, int customerID, String customerName) {
                //这里拿到position，来设置相应的数据
                Intent intent = new Intent(getActivity(), AddTabActivity.class);
                intent.putExtra("customer_id", customerID);
                intent.putExtra("customer_name", customerName);
                intent.putExtra("position",position);
                startActivity(intent);
            }

            @Override
            public void onMyItemClickListener(int position, int customerID, String customerName) {
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra("customer_name", customerName);
                intent.putExtra("customer_id", customerID);
                intent.putExtra("date", mRowsBeanMore.get(position).getAdate());
                intent.putExtra("tag", 5);
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
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 初始化数据
     */
    private void initDatas(int pageSize, int pageNumber) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getschedulelist")
                .addParams("token", token)
                .addParams("type", "done")
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
    public void initPopWindow(List<UnFinishOrder.DataBean.RowsBean.PlanListBean> planBean) {
        View popView = getLayoutInflater(getArguments()).inflate(R.layout.popwindow_unfinish_advise, null);
        ListView lv_advise = (ListView) popView.findViewById(R.id.lv_advise);
        TextView create_time = (TextView) popView.findViewById(R.id.create_time);
        PopupWindow popupWindow = new PopupWindow(popView, 200, 300, true);
        popupWindow.setWidth(Util.getScreenWidth(getContext()) * 3 / 4);
        popupWindow.setHeight(Util.getScreenHeight(getContext()) / 4);
        popupWindow.showAtLocation(getLayoutInflater(getArguments()).inflate(R.layout.fragment_unfinished_order, null), Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);
        popupWindow.setOnDismissListener(new FinishedOrderFragment.popupDismissListener()); //关闭事件
        popView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        if (!TextUtils.isEmpty(planBean.get(0).getCreate_date()))
            create_time.setText(planBean.get(0).getCreate_date());
        List<Map<String, Object>> simList = new ArrayList<>();
        for (int i = 0; i < planBean.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            String projectName = planBean.get(i).getProject_name();
            String creater = planBean.get(i).getCreator();
            map.put("icon", R.mipmap.finish);
            if (!TextUtils.isEmpty(projectName)) map.put("project", projectName);
            else map.put("project", "");
            if (!TextUtils.isEmpty(creater)) map.put("creater", creater);
            else map.put("creater", "");
            simList.add(map);
        }
        lv_advise.setAdapter(new SimpleAdapter(getContext(), simList, R.layout.style_order_poplist,
                new String[]{"icon", "project", "creater"}, new int[]{R.id.imageView, R.id.tv_pro, R.id.tv_creater}));
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
        }else if(eventDatas.getTag().equals(Constants.GET_CHANGE_TOTAL)){
            if(eventDatas.getResult().equals("ok")){
                if(adapter!=null){
                    Log.e(TAG,"这个是list的接收  "+eventDatas.getProduct_total()+eventDatas.getProject_total());
                    List<UnFinishOrder.DataBean.RowsBean> mylist=adapter.getmRowsBean();
                    UnFinishOrder.DataBean.RowsBean bean=mylist.get(eventDatas.getPosition());
                    bean.setPlan_project_total(eventDatas.getProject_total());
                    Log.e(TAG,"这个是list的接收project  "+eventDatas.getProject_total());
                    bean.setPlan_product_total(eventDatas.getProduct_total());
                    Log.e(TAG,"这个是list的接收product  "+eventDatas.getProduct_total());
                    mylist.remove(eventDatas.getPosition());
                    mylist.add(eventDatas.getPosition(),bean);
                    adapter.setDatas(mylist);
                }
            }else {
                Toast.makeText(getActivity(),"设置失败",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
