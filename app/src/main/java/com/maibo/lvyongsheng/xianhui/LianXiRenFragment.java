package com.maibo.lvyongsheng.xianhui;

import android.Manifest;
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

import com.maibo.lvyongsheng.xianhui.adapter.LianXiRenAdapter;
import com.maibo.lvyongsheng.xianhui.implement.LetterView;

import java.util.List;

import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.view.LCIMDividerItemDecoration;
import de.greenrobot.event.EventBus;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * Created by wli on 15/12/4.
 * 联系人页面
 */
public class LianXiRenFragment extends Fragment {

  protected SwipeRefreshLayout refreshLayout;
  protected RecyclerView recyclerView;
  //private MembersAdapter itemAdapter;
  private LianXiRenAdapter itemAdapter;
  LinearLayoutManager layoutManager;
  LetterView lv_letterview;
  SharedPreferences sp;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = initView(inflater, container);
    initAdapter();
    getPromiss();
    return view;
  }

  /**
   * 手动获取权限
   */
  private void getPromiss() {
    //询问录音
    PermissionGen.needPermission(LianXiRenFragment.this, 200,
            new String[] {
                    Manifest.permission.RECORD_AUDIO
            }
    );
  }

  /**
   * 联系人适配器
   */
  private void initAdapter() {
    layoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.addItemDecoration(new LCIMDividerItemDecoration(getContext()));
    List<LCChatKitUser> list = getLcChatKitUsers();
    itemAdapter =new LianXiRenAdapter(getContext(),list,sp.getString("displayname",null));
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
    lv_letterview.setCharacterListener(new LetterView.CharacterClickListener() {
      @Override
      public void clickCharacter(String character) {
        layoutManager.scrollToPositionWithOffset(itemAdapter.getScrollPosition(character), 0);
      }

      @Override
      public void clickArrow() {
        layoutManager.scrollToPositionWithOffset(0, 0);
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
    View view=inflater.inflate(R.layout.contact_fragment,container,false);
    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.contact_fragment_srl_list);
    sp=getActivity().getSharedPreferences("baseDate", Context.MODE_PRIVATE);
    recyclerView = (RecyclerView) view.findViewById(R.id.contact_fragment_rv_list);
    lv_letterview= (LetterView) view.findViewById(R.id.lv_letterview);
    return view;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                   int[] grantResults) {
    PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
  }
  @PermissionSuccess(requestCode = 200)
  public void doSomething(){
    //权限已获得
    // //权限已获得
    //App.showToast(getContext(),"设置权限成功");
  }
  @PermissionFail(requestCode = 200)
  public void doFailSomething(){
    App.showToast(getContext(),"设置失败，无法发送语音，请手动设置权限!");
  }

  /**
   * 获取联系人
   * @return
     */
  private List<LCChatKitUser> getLcChatKitUsers() {
    List<LCChatKitUser> list= CustomUserProvider.getInstance(getContext()).getAllUsers();
//    for (int i=0;i<list.size();i++){
//      if (list.get(i).getUserName().equals(sp.getString("displayname",null))){
//        list.remove(i);
//        i--;
//      }
//    }
    return list;
  }

  @Override
  public void onDestroy() {
    EventBus.getDefault().unregister(this);
    super.onDestroy();
  }

  @Override
  public void onResume() {
    super.onResume();
//    refreshMembers();
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
    List<LCChatKitUser> list = getLcChatKitUsers();
    itemAdapter =new LianXiRenAdapter(getContext(),list,sp.getString("displayname",null));
    //itemAdapter.notifyDataSetChanged();
    recyclerView.setAdapter(itemAdapter);
    refreshLayout.setRefreshing(false);
  }
}
