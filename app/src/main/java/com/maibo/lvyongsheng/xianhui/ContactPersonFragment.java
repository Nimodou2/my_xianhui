package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.adapter.LianXiRenAdapter;
import com.maibo.lvyongsheng.xianhui.implement.Util;
import com.maibo.lvyongsheng.xianhui.myinterface.OnLetterTouchListener;
import com.maibo.lvyongsheng.xianhui.utils.ZzLetterSideBar;

import java.util.List;

import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.view.LCIMDividerItemDecoration;

/**
 * Created by wli on 15/12/4.
 * 联系人页面
 */
public class ContactPersonFragment extends Fragment {

  protected SwipeRefreshLayout refreshLayout;
  protected RecyclerView recyclerView;
  private LianXiRenAdapter itemAdapter;
  LinearLayoutManager layoutManager;
  LinearLayout ll_content_title;
  SharedPreferences sp;
  ZzLetterSideBar sidebar;
  TextView tv_dialog;
  View views;
  int viewHeight;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = initView(inflater, container);
    //获取屏幕高度
    viewHeight= (Util.getScreenHeight(getContext())-getStatusBarHeight())/35;

    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.contact_fragment_srl_list);
    ll_content_title=(LinearLayout)view.findViewById(R.id.ll_content_title);
    LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) ll_content_title.getLayoutParams();
    params.height=viewHeight*2;
    ll_content_title.setLayoutParams(params);

    sp=getActivity().getSharedPreferences("baseDate", Context.MODE_PRIVATE);
    recyclerView = (RecyclerView) view.findViewById(R.id.contact_fragment_rv_list);
    tv_dialog= (TextView) view.findViewById(R.id.tv_dialog);
    sidebar= (ZzLetterSideBar) view.findViewById(R.id.sidebar);
    initAdapter();
    return view;
  }

  /**
   * 联系人适配器
   */
  private void initAdapter() {
    layoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.addItemDecoration(new LCIMDividerItemDecoration(getContext()));
    List<LCChatKitUser> list = CustomUserProvider.getInstance().getAllUsers();
    itemAdapter =new LianXiRenAdapter(getContext(),list,sp.getString("displayname",null),viewHeight);
    recyclerView.setAdapter(itemAdapter);
    //设置卷内的颜色
    refreshLayout.setColorSchemeResources(R.color.colorLightYellow,
            R.color.colorLightYellow, R.color.colorLightYellow, R.color.colorLightYellow);
    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refreshMembers();
      }
    });

    sidebar.setLetterTouchListener(new OnLetterTouchListener() {
      @Override
      public void onLetterTouch(String letter, int position) {
        if (letter.equals("↑")){
          layoutManager.scrollToPositionWithOffset(0, 0);
        }else{
          layoutManager.scrollToPositionWithOffset(itemAdapter.getScrollPosition(letter), 0);
        }
        tv_dialog.setVisibility(View.VISIBLE);
        tv_dialog.setText(letter);
      }

      @Override
      public void onActionUp() {
        tv_dialog.setVisibility(View.GONE);
      }
    });
  }

  /**
   * 初始化View
   * @param inflater
   * @param container
   * @return
     */
  @NonNull
  private View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
    if (views==null){
      views=inflater.inflate(R.layout.contact_fragment,container,false);
    }
    //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
    ViewGroup parent = (ViewGroup) views.getParent();
    if (parent != null) {
      parent.removeView(views);
    }
    return views;
  }

  /**
   * 获取联系人
   * @return
     */

  @Override
  public void onResume() {
    super.onResume();
    int isChangeDoor=sp.getInt("isChangeDoor",-1);
    if (isChangeDoor==1){

      refreshMembers();
      SharedPreferences.Editor editor=sp.edit();
      editor.putInt("isChangeDoor",0);
      editor.commit();
    }
  }

  //swipeRefreshLayout用于数据刷新
  private void refreshMembers() {
    //更新成员列表
    List<LCChatKitUser> list = CustomUserProvider.getInstance().getAllUsers();
    String displayname=sp.getString("displayname",null);
    if (displayname!=null){
      itemAdapter =new LianXiRenAdapter(getActivity(),list,displayname,viewHeight);
      recyclerView.setAdapter(itemAdapter);
      refreshLayout.setRefreshing(false);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  public  int getStatusBarHeight() {
    int result = 0;
    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result =  getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }
}
