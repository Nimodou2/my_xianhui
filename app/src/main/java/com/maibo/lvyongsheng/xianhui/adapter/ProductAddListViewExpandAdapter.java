package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.Card;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.maibo.lvyongsheng.xianhui.entity.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;


/**
 * Created by LYS on 2016/9/17.
 */
public class ProductAddListViewExpandAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Product> dataPr;
    private int[] sel;
    private ExpandableListView expandableListView;
    private int what_view = -1;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;
    //记录选中的对象
    private static HashMap<Integer, Integer> itemID;
    private int viewHeight;

    class ViewHolder {

        TextView tvName;
        CheckBox cb;
        TextView card_name, card_style, price, yu_ci;
        LinearLayout ll_all, ll_child_all;
    }

    public ProductAddListViewExpandAdapter(Context context, List<Product> dataPr, int[] sel, ExpandableListView expandableListView, int viewHeight) {
        this.dataPr = dataPr;
        this.context = context;
        this.sel = sel;
        this.expandableListView = expandableListView;
        this.viewHeight = viewHeight;
        isSelected = new HashMap<Integer, Boolean>();
        itemID = new HashMap<>();

        // 初始化数据
        initDate();
    }

    public void initDate() {
        for (int i = 0; i < dataPr.size(); i++) {
            Product bean = dataPr.get(i);
            if (bean.getItem_id() == -1)
                continue;
            int p = 0;
            for (int j = 0; j < sel.length; j++) {
                if (sel[j] == bean.getItem_id()) {
                    isSelected.put(i, true);
                    itemID.put(i, bean.getItem_id());
                    setItemID(itemID);
                    p = 1;
                }
            }
            if (p == 0) {
                isSelected.put(i, false);
            }
        }
    }

    @Override
    public int getGroupCount() {
        return dataPr.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return dataPr.get(i).getCard_list().size();
    }

    @Override
    public Object getGroup(int i) {
        return dataPr.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return dataPr.get(i).getCard_list();
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int i, final boolean b, View view, ViewGroup viewGroup) {

//        ViewHolder holder;
//        if (view == null) {
//            view = inflater.inflate(R.layout.style_add_project_item_plan, null);
//            holder = new ViewHolder();
//            holder.cb = (CheckBox) view.findViewById(R.id.checkBox);
//            holder.tvName = (TextView) view.findViewById(R.id.tv_project_name);
//            holder.ll_all= (LinearLayout) view.findViewById(R.id.ll_all);
//            view.setTag(holder);
//        } else {
//            // 取出holder
//            holder = (ViewHolder) view.getTag();
//        }


        final Product bean = dataPr.get(i);
        LayoutInflater inflater = LayoutInflater.from(context);
        if (bean.getItem_id() == -1) {
            TextView tv1 = new TextView(context);
            tv1.setPadding(20, 15, 0, 15);
            tv1.setTextSize(14);
            tv1.setTextColor(context.getResources().getColor(R.color.text_color));
            tv1.setBackgroundColor(context.getResources().getColor(R.color.weixin_lianxiren_gray));
            tv1.setText(bean.getFullname());
            return tv1;
        } else {
            View view2 = inflater.inflate(R.layout.style_add_project_item_plan, null);
            CheckBox cb = (CheckBox) view2.findViewById(R.id.checkBox);
            TextView tvName = (TextView) view2.findViewById(R.id.tv_project_name);
            LinearLayout ll_all = (LinearLayout) view2.findViewById(R.id.ll_all);
            LinearLayout ll_first = (LinearLayout) view2.findViewById(R.id.ll_first);
            ViewGroup.LayoutParams params = ll_all.getLayoutParams();
            params.height = viewHeight * 20 / 255;
            ll_all.setLayoutParams(params);

            tvName.setText(bean.getFullname());
            if (bean.getCard_list().size() != 0) {
                tvName.setTextColor(Color.GREEN);
            }
            // 监听checkBox并根据原来的状态来设置新的状态
            cb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (isSelected.get(i)) {
                        isSelected.put(i, false);
                        setIsSelected(isSelected);
                        itemID.put(i, null);
                        setItemID(itemID);

                    } else {
                        isSelected.put(i, true);
                        setIsSelected(isSelected);
                        itemID.put(i, bean.getItem_id());
                        setItemID(itemID);
                    }

                    Set<Map.Entry<Integer, Integer>> entries = itemID.entrySet();
                    int total = 0;
                    int noNull = 0;
                    String buffer = "";
                    for (Map.Entry<Integer, Integer> entry : entries) {
                        if (entry.getValue() == null) continue;
                        noNull++;
                        buffer += "," + entry.getValue();
                        for (int k = 0; k < sel.length; k++) {
                            if (entry.getValue() == sel[k]) {
                                total++;
                            }
                        }
                    }
                    if (total == noNull && noNull == sel.length) {
                        //状态未变更
                        EventDatas eventDatas = new EventDatas(Constants.PLAN_PRODUCT_ADAPTER, noNull + "", "0", buffer);
                        EventBus.getDefault().post(eventDatas);
                    } else {
                        //状态变更
                        EventDatas eventDatas = new EventDatas(Constants.PLAN_PRODUCT_ADAPTER, noNull + "", "1", buffer);
                        EventBus.getDefault().post(eventDatas);
                    }

                }
            });

            //处理被点击条目和子条目变色
            /*if (what_view == i) {
                ll_first.setBackgroundColor(context.getResources().getColor(R.color.main_color_version2));
                tvName.setTextColor(Color.WHITE);

            } else {
                ll_first.setBackgroundColor(Color.WHITE);
                tvName.setTextColor(Color.GRAY);
                if (bean.getCard_list().size() != 0) {
                    tvName.setTextColor(Color.GREEN);
                }else {
                    tvName.setTextColor(Color.GRAY);
                }
            }*/
            //可有效解决Groupview被抢夺焦点的问题
            LinearLayout layout = (LinearLayout) view2.findViewById(R.id.groupExpand);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (b) {
                        //折叠子分组
                        expandableListView.collapseGroup(i);
                        what_view = i;

                    } else {
                        //展示子分组
                        expandableListView.expandGroup(i);
                        what_view = i;
                    }
                }
            });

            // 根据isSelected来设置checkbox的选中状况
            cb.setChecked(getIsSelected().get(i));
            return view2;
        }
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.style_card_list, null);
            holder.card_name = (TextView) view.findViewById(R.id.card_name);
            holder.card_style = (TextView) view.findViewById(R.id.card_style);
            holder.price = (TextView) view.findViewById(R.id.price);
            holder.yu_ci = (TextView) view.findViewById(R.id.yu_ci);
            holder.ll_child_all = (LinearLayout) view.findViewById(R.id.ll_child_all);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ViewGroup.LayoutParams params = holder.ll_child_all.getLayoutParams();
        params.height = viewHeight * 25 / 255;
        holder.ll_child_all.setLayoutParams(params);

        Card card = dataPr.get(i).getCard_list().get(i1);
        if (card == null) return null;

        //处理点击父条目，子条目按需求变色
       /* if (i == what_view) {
            holder.ll_child_all.setBackgroundColor(context.getResources().getColor(R.color.main_color_slow_version1));
        } else {
            holder.ll_child_all.setBackgroundColor(Color.WHITE);
        }*/
        holder.card_style.setText(card.getCard_class());
        holder.price.setText(card.getPrice());
        holder.yu_ci.setText(card.getTimes() + "");
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ProductAddListViewExpandAdapter.isSelected = isSelected;
    }

    public HashMap<Integer, Integer> getItemID() {
        return itemID;
    }

    public void setItemID(HashMap<Integer, Integer> itemID) {
        ProductAddListViewExpandAdapter.itemID = itemID;
    }

}
