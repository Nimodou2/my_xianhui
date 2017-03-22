package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.CustomerConsumeActivity;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.Card;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.maibo.lvyongsheng.xianhui.entity.Project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * Created by LYS on 2016/9/17.
 */
public class ProjectAddListViewExpandAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Project> dataPr;
    private int[] sel;
    private ExpandableListView expandableListView;
    private int viewHeight;
    private int customer_id;
    private String customer_name;
    int what_view = -1;

    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;
    //记录选中的对象
    private static HashMap<Integer, Integer> itemID;

    class ViewHolder {

        TextView tvName, tv_times;
        CheckBox cb;
        TextView card_name, card_style, price, yu_ci;
        LinearLayout ll_all, ll_child_all;
    }

    public ProjectAddListViewExpandAdapter(Context context, List<Project> dataPr, int[] sel, ExpandableListView expandableListView,
                                           int viewHeight, int customer_id, String customer_name) {
        this.dataPr = dataPr;
        this.context = context;
        this.sel = sel;
        this.expandableListView = expandableListView;
        this.viewHeight = viewHeight;
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        isSelected = new HashMap<Integer, Boolean>();
        itemID = new HashMap<>();

        // 初始化数据
        initDate();
    }

    /**
     * 预装checkBox的状态
     */
    public void initDate() {
        for (int i = 0; i < dataPr.size(); i++) {
            Project bean = dataPr.get(i);
            if (bean.getItem_id() == -1 || bean.getItem_id() == -2 || bean.getItem_id() == -3)
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
        if (dataPr.get(i).getCard_list() == null) return 0;
        else return dataPr.get(i).getCard_list().size();
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
    public View getGroupView(final int i, final boolean b, final View view, final ViewGroup viewGroup) {
        //1、第一个条目，"消费记录"
        //2、展示最多三条消息记录
        //3、点击查看更多消费记录
        //4、项目计划头部，“项目计划”
        //5、项目计划列表
        final Project bean = dataPr.get(i);
        LayoutInflater inflater = LayoutInflater.from(context);
        if (bean.getItem_id() == -1) {
            TextView tv1 = new TextView(context);
            tv1.setPadding(20, 15, 0, 15);
            tv1.setTextSize(14);
            tv1.setTextColor(context.getResources().getColor(R.color.text_color));
            tv1.setBackgroundColor(context.getResources().getColor(R.color.weixin_lianxiren_gray));
            tv1.setText(bean.getFullname());
            return tv1;

        } else if (bean.getItem_id() == -2) {
            View view2 = inflater.inflate(R.layout.style_list_project_history, null);
            TextView name2 = (TextView) view2.findViewById(R.id.name);
            TextView number2 = (TextView) view2.findViewById(R.id.numbers);
            name2.setText(bean.getFullname());
            String newDate = bean.getDate().substring(5).replace(".", "/");
            number2.setText(newDate);
            return view2;
        } else if (bean.getItem_id() == -3) {

            TextView tv1 = new TextView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tv1.setGravity(Gravity.RIGHT);
            tv1.setLayoutParams(params);
            tv1.setPadding(0, 10, 20, 10);
            tv1.setTextColor(context.getResources().getColor(R.color.text_color_blue));
            tv1.setBackgroundColor(Color.WHITE);
            tv1.setTextSize(12);
            tv1.setText(bean.getFullname());
            //可有效解决Groupview被抢夺焦点的问题
            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (b) {
                        expandableListView.collapseGroup(i);

                    } else {
                        expandableListView.expandGroup(i);
                    }
                }
            });
            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转到消费界面
                    Intent intent = new Intent(context, CustomerConsumeActivity.class);
                    intent.putExtra("tag", 1);
                    intent.putExtra("customer_id", customer_id);
                    intent.putExtra("customer_name", customer_name);
                    context.startActivity(intent);
                }
            });

            return tv1;
        } else if (bean.getItem_id() == -4) {

            TextView tv1 = new TextView(context);
            tv1.setPadding(20, 15, 0, 15);
            tv1.setTextSize(14);
            tv1.setTextColor(context.getResources().getColor(R.color.text_color));
            tv1.setBackgroundColor(context.getResources().getColor(R.color.weixin_lianxiren_gray));
            tv1.setText(bean.getFullname());
            return tv1;
        } else {
//            ViewHolder holder;
//            if (view == null) {
//                view = inflater.inflate(R.layout.style_add_project_item_plan, null);
//                holder = new ViewHolder();
//                holder.cb = (CheckBox) view.findViewById(R.id.checkBox);
//                holder.tvName = (TextView) view.findViewById(R.id.tv_project_name);
//                holder.tv_times = (TextView) view.findViewById(R.id.tv_times);
//                holder.ll_all = (LinearLayout) view.findViewById(R.id.ll_all);
//                view.setTag(holder);
//            } else {
//                // 取出holder
//                holder = (ViewHolder) view.getTag();
//            }
            final View view3 = inflater.inflate(R.layout.style_add_project_item_plan, null);
            CheckBox cb = (CheckBox) view3.findViewById(R.id.checkBox);
            TextView tvName = (TextView) view3.findViewById(R.id.tv_project_name);
            TextView tv_times = (TextView) view3.findViewById(R.id.tv_times);
            LinearLayout ll_all = (LinearLayout) view3.findViewById(R.id.ll_all);
            ViewGroup.LayoutParams params = ll_all.getLayoutParams();
            params.height = viewHeight * 20 / 255;
            ll_all.setLayoutParams(params);
            tvName.setText(bean.getFullname());
            if (bean.getCard_list().size() != 0) {
                tvName.setTextColor(Color.rgb(15, 135, 255));
                int cardTimes = 0;
                for (int p = 0; p < bean.getCard_list().size(); p++) {
                    cardTimes += bean.getCard_list().get(p).getTimes();
                }
                tv_times.setVisibility(View.VISIBLE);
                tv_times.setText(cardTimes + "次");
            } else {
                tv_times.setVisibility(View.INVISIBLE);
                tvName.setTextColor(Color.BLACK);
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
                        EventDatas eventDatas = new EventDatas(Constants.PLAN_PROJECT_ADAPTER, noNull + "", "0", buffer);
                        EventBus.getDefault().post(eventDatas);
                    } else {
                        //状态变更
                        EventDatas eventDatas = new EventDatas(Constants.PLAN_PROJECT_ADAPTER, noNull + "", "1", buffer);
                        EventBus.getDefault().post(eventDatas);
                    }

                }

            });

            //可有效解决Groupview被抢夺焦点的问题
            final LinearLayout layout = (LinearLayout) view3.findViewById(R.id.groupExpand);
            final LinearLayout ll_first = (LinearLayout) view3.findViewById(R.id.ll_first);
            //处理被点击条目和子条目变色
            if (what_view == i) {
                ll_first.setBackgroundColor(context.getResources().getColor(R.color.expend_first));
            } else {
                ll_first.setBackgroundColor(Color.WHITE);
            }

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (b) {
                        expandableListView.collapseGroup(i);//折叠
                        what_view = i;
                    } else {
                        expandableListView.expandGroup(i);//展开
                        what_view = i;
                    }
                }
            });
            // 根据isSelected来设置checkbox的选中状况
            cb.setChecked(getIsSelected().get(i));
            return view3;
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
        if (i == what_view) {
            holder.ll_child_all.setBackgroundColor(context.getResources().getColor(R.color.expend_two));
        } else {
            holder.ll_child_all.setBackgroundColor(Color.WHITE);
        }

        holder.card_name.setText(card.getFullname());
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
        ProjectAddListViewExpandAdapter.isSelected = isSelected;
    }

    public HashMap<Integer, Integer> getItemID() {
        return itemID;
    }

    public void setItemID(HashMap<Integer, Integer> itemID) {
        ProjectAddListViewExpandAdapter.itemID = itemID;
    }
}
