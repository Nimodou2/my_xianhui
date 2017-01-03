package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.Cards;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Created by LYS on 2016/10/6.
 */
public class CustomerDetailAdapter extends BaseAdapter {

    private Context context;
    private List<Cards> list;
    private List<Cards> huiYuan;
    private List<Cards> liaoCheng;
    private int viewHeight;
    //Map<String,Integer> map;
//    List<String> listA;
//    List<Integer> listB;
    //记录有没有会员卡
    int a = 0;
    //记录有没有疗程卡
    int b = 0;


    public CustomerDetailAdapter(Context context, List<Cards> list, int viewHeight) {
        this.context = context;
        this.list = list;
        this.viewHeight = viewHeight;
        huiYuan = new ArrayList<>();
        liaoCheng = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCard_sort() == 1) {
                a = 1;
                huiYuan.add(list.get(i));
            } else if (list.get(i).getCard_sort() == 3) {
                b = 1;
                liaoCheng.add(list.get(i));
            }
        }

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        //填充数据
        if (a == 1 && b == 0) {
            Cards cards = huiYuan.get(i);
            return cards;
        } else if (a == 1 && b == 1) {
            if (i == 0) {
                Cards cards = huiYuan.get(i);

                return cards;
            } else if (i > 0 && i < huiYuan.size()) {
                Cards cards = huiYuan.get(i);

                return cards;
            } else if (i == huiYuan.size()) {
                Cards cards = liaoCheng.get(i - huiYuan.size());

                return cards;
            } else {
                Cards cards = liaoCheng.get(i - huiYuan.size());

                return cards;
            }
        } else if (a == 0 && b == 1) {
            Cards cards = liaoCheng.get(i);
            return cards;
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = View.inflate(context.getApplicationContext(), R.layout.style_day_table_list, null);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.numbers = (TextView) view.findViewById(R.id.numbers);
            holder.ll_cards_type = (LinearLayout) view.findViewById(R.id.ll_cards_type);
            holder.ll_item= (LinearLayout) view.findViewById(R.id.ll_item);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ViewGroup.LayoutParams params=holder.ll_item.getLayoutParams();
        params.height=viewHeight*20/255;
        holder.ll_item.setLayoutParams(params);
        //填充数据
        if (a == 1 && b == 0) {
            Cards cards = huiYuan.get(i);
            if (i == 0) {
                TextView tv = new TextView(context);
                setMySelfTextView(tv);
                tv.setText("会员卡");
                tv.setBackgroundResource(R.color.weixin_lianxiren_gray);
                tv.setPadding(30, 0, 0, 0);
                tv.setTextSize(14);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                holder.ll_cards_type.addView(tv, 0);
                holder.name.setText(cards.getFullname());
                holder.numbers.setText("余" + cards.getAmount() + "元");
                return view;
            } else {
                holder.name.setText(cards.getFullname());
                holder.numbers.setText("余" + cards.getAmount() + "元");
                return view;
            }
        } else if (a == 1 && b == 1) {
            if (i == 0) {
                Cards cards = huiYuan.get(i);
                TextView tv = new TextView(context);
                setMySelfTextView(tv);
                tv.setText("会员卡");
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setBackgroundResource(R.color.weixin_lianxiren_gray);
                tv.setPadding(30, 0, 0, 0);
                tv.setTextSize(14);
                holder.ll_cards_type.addView(tv, 0);
                holder.name.setText(cards.getFullname());
                holder.numbers.setText("余" + cards.getAmount() + "元");
                return view;
            } else if (i > 0 && i < huiYuan.size()) {
                Cards cards = huiYuan.get(i);
                holder.name.setText(cards.getFullname());
                holder.numbers.setText("余" + cards.getAmount() + "元");
                return view;
            } else if (i == huiYuan.size()) {
                Cards cards = liaoCheng.get(i - huiYuan.size());
                TextView tv = new TextView(context);
                setMySelfTextView(tv);
                tv.setText("疗程卡");
                tv.setBackgroundResource(R.color.weixin_lianxiren_gray);
                tv.setPadding(30, 0, 0, 0);
                tv.setTextSize(14);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                holder.ll_cards_type.addView(tv, 0);
                holder.name.setText(cards.getFullname());
                int yuci = 0;
                for (int j = 0; j < cards.getCard().size(); j++)
                    yuci += cards.getCard().get(j).getTimes();
                holder.numbers.setText(yuci + "次");
                return view;
            } else {
                Cards cards = liaoCheng.get(i - huiYuan.size());
                holder.name.setText(cards.getFullname());
                int yuci = 0;
                for (int j = 0; j < cards.getCard().size(); j++)
                    yuci += cards.getCard().get(j).getTimes();
                holder.numbers.setText(yuci + "次");
                return view;
            }
        } else if (a == 0 && b == 1) {
            if (i == 0) {
                TextView tv = new TextView(context);
                setMySelfTextView(tv);
                tv.setText("疗程卡");
                tv.setBackgroundResource(R.color.weixin_lianxiren_gray);
                tv.setPadding(30, 0, 0, 0);
                tv.setTextSize(14);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                Cards cards = liaoCheng.get(i);
                holder.name.setText(cards.getFullname());
                int yuci = 0;
                for (int j = 0; j < cards.getCard().size(); j++)
                    yuci += cards.getCard().get(j).getTimes();
                holder.numbers.setText(yuci + "次");
                return view;
            } else {
                Cards cards = liaoCheng.get(i);
                holder.name.setText(cards.getFullname());
                int yuci = 0;
                for (int j = 0; j < cards.getCard().size(); j++)
                    yuci += cards.getCard().get(j).getTimes();
                holder.numbers.setText(yuci + "次");
                return view;
            }
        }

        return null;
    }

    /**
     * 动态设置自定义Textview的高度
     *
     * @param tv
     */
    private void setMySelfTextView(TextView tv) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = viewHeight * 15 / 255;
        tv.setLayoutParams(params);
    }

    class ViewHolder {
        TextView name, numbers;
        LinearLayout ll_cards_type,ll_item;
    }

    //排序
    class MyComparator implements Comparator {
        //这里的o1和o2就是list里任意的两个对象，然后按需求把这个方法填完整就行了
        @Override
        public int compare(Object o1, Object o2) {
            Cards lhs = (Cards) o1;
            Cards rhs = (Cards) o2;
            if (lhs.getItem_id() > rhs.getItem_id()) {
                return 1;
            }
            if (lhs.getItem_id() == rhs.getItem_id()) {
                return 0;
            }
            if (lhs.getItem_id() < rhs.getItem_id()) {
                return -1;
            }
            return 0;
        }
    }
}
