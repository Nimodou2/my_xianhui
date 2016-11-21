package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.leancloud.chatkit.LCChatKitUser;

/**
 * Created by wli on 15/8/14.
 * 成员列表 Adapter
 */
public class MembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  /**
   * 所有 Adapter 成员的list
   */
  private List<MemberItem> memberList = new ArrayList<MemberItem>();

  /**
   * 在有序 memberList 中 MemberItem.sortContent 第一次出现时的字母与位置的 map
   */
  private Map<Character, Integer> indexMap = new HashMap<Character, Integer>();
  public static final int TYPE_HEADER = 0;
  public static final int TYPE_NORMAL = 1;
  private View mHeaderView;
  private OnItemClickListener mListener;
  public void setOnItemClickListener(OnItemClickListener li) {
    mListener = li;
  }

  /**
   * 简体中文的 Collator
   */
  Collator cmp = Collator.getInstance(Locale.SIMPLIFIED_CHINESE);

  public MembersAdapter() {}
  Context context;
  public MembersAdapter(Context context) {
    this.context=context;
  }

  /**
   * 设置成员列表，然后更新索引
   * 此处会对数据以 空格、数字、字母（汉字转化为拼音后的字母） 的顺序进行重新排列
   */
  public void setMemberList(List<LCChatKitUser> userList) {
    memberList.clear();
    if (null != userList) {
      for (LCChatKitUser user : userList) {
        MemberItem item = new MemberItem();
        item.lcChatKitUser = user;
        item.sortContent = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
        //此处为排序后的成员列表
        memberList.add(item);
      }
    }
    Collections.sort(memberList, new SortChineseName());
    updateIndex();
    //作用：按首字母分组
    updateInitialsVisible(memberList);
  }
  //设置头部信息
  public void setHeaderView(View headerView) {
    mHeaderView = headerView;
    notifyItemInserted(0);
  }
  //获取头部信息
  public View getHeaderView() {
    return mHeaderView;
  }
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {/*
    if (viewType==0){
      MyViewHolder0 holder = new MyViewHolder0(LayoutInflater.from(
              context).inflate(R.layout.style_lianxiren_search, parent,
              false));
      return holder;
    }else if(viewType==2){
      MyViewHolder2 holder = new MyViewHolder2(LayoutInflater.from(
              context).inflate(R.layout.style_lianxiren_zhushou, parent,
              false));
      return holder;
    }else if(viewType==3){
      MyViewHolder3 holder = new MyViewHolder3(LayoutInflater.from(
              context).inflate(R.layout.style_lianxiren_remind, parent,
              false));
      return holder;
    }else if(viewType==4){
      MyViewHolder4 holder = new MyViewHolder4(LayoutInflater.from(
              context).inflate(R.layout.style_lianxiren_work, parent,
              false));
      return holder;
    } else*/
      return new ContactItemHolder(parent.getContext(), parent);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
   /* if(holder instanceof MyViewHolder2){
      ((MyViewHolder2) holder).ll_zhu_shou.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Log.e("LYS:","keyike");
        }
      });
    }
    if (position>3)*/
      ((ContactItemHolder) holder).bindData(memberList.get(position).lcChatKitUser);
  }
  @Override
  public int getItemViewType(int position) {
    if(position == 0) return 0;
    else if (position==1) return 2;
    else if (position==2) return 3;
    else if (position==3) return 4;
    return 1;
  }
  @Override
  public int getItemCount() {
    return memberList.size();
  }

  /**
   * 获取索引 Map
   */
  public Map<Character, Integer> getIndexMap() {
    return indexMap;
  }

  /**
   * 更新索引 Map
   */
  private void updateIndex() {
    Character lastCharcter = '#';
    indexMap.clear();
    for (int i = 0; i < memberList.size(); i++) {
      Character curChar = Character.toLowerCase(memberList.get(i).sortContent.charAt(0));
      if (!lastCharcter.equals(curChar)) {
        indexMap.put(curChar, i);
      }
      lastCharcter = curChar;
    }
  }

  public class SortChineseName implements Comparator<MemberItem> {

    @Override
    public int compare(MemberItem str1, MemberItem str2) {
      if (null == str1) {
        return -1;
      }
      if (null == str2) {
        return 1;
      }
      if (cmp.compare(str1.sortContent, str2.sortContent)>0){
        return 1;
      }else if (cmp.compare(str1.sortContent, str2.sortContent)<0){
        return -1;
      }
      return 0;
    }
  }
  public static class MemberItem {
    public LCChatKitUser lcChatKitUser;
    public String sortContent;
    public boolean initialVisible;
  }

  //**********************************新增
  /**
   * 必须要排完序后，否则没意义
   * @param list
   */
  private void updateInitialsVisible(List<MemberItem> list) {
    if (null != list && list.size() > 0) {
      char lastInitial = ' ';
      for (MemberItem item : list) {
        if (!TextUtils.isEmpty(item.sortContent)) {
          item.initialVisible = (lastInitial != item.sortContent.charAt(0));
          lastInitial = item.sortContent.charAt(0);
        } else {
          item.initialVisible = true;
          lastInitial = ' ';
        }
      }
    }
  }
  interface OnItemClickListener {
    void onItemClick(int position);
  }
 /* class MyViewHolder0 extends RecyclerView.ViewHolder
  {
    public MyViewHolder0(View view)
    {
      super(view);

    }
  }
  class MyViewHolder2 extends RecyclerView.ViewHolder
  {
    LinearLayout  ll_zhu_shou;
    TextView tv_zhushou;
    public MyViewHolder2(View view)
    {
      super(view);
      ll_zhu_shou= (LinearLayout) view.findViewById(R.id.ll_zhu_shou);
      tv_zhushou= (TextView) view.findViewById(R.id.tv_zhushou);
    }

  }*/
  /*class MyViewHolder3 extends RecyclerView.ViewHolder
  {
    public MyViewHolder3(View view)
    {
      super(view);

    }
  }
  class MyViewHolder4 extends RecyclerView.ViewHolder
  {
    public MyViewHolder4(View view)
    {
      super(view);

    }
  }*/
}