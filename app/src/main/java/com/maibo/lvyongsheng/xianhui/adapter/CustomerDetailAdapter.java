package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.Cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by LYS on 2016/10/6.
 */
public class CustomerDetailAdapter extends BaseAdapter{

    private Context context;
    private List<Cards> list;
    private List<Cards> huiYuan;
    private List<Cards> liaoCheng;
   //Map<String,Integer> map;
    List<String> listA;
    List<Integer> listB;
    //记录有没有会员卡
    int a=0;
    //记录有没有疗程卡
    int b=0;


    public CustomerDetailAdapter(Context context, List<Cards> list){
        this.context=context;
        this.list=list;
        huiYuan=new ArrayList<>();
        liaoCheng=new ArrayList<>();
        for (int i=0;i<list.size();i++){
            if (list.get(i).getCard_sort()==1){
                a=1;
                huiYuan.add(list.get(i));
            }else if (list.get(i).getCard_sort()==3){
                b=1;
                liaoCheng.add(list.get(i));
            }
        }
        //统计同一张卡的使用次数
        Collections.sort(liaoCheng, new MyComparator());
        int num=1;
        //map=new HashMap<>();
        listA=new ArrayList<>();
        listB=new ArrayList<>();
        for (int j=1;j<liaoCheng.size();j++){
            if(j<liaoCheng.size()-1){
                if (liaoCheng.get(j-1).getItem_id()==liaoCheng.get(j).getItem_id()){
                    num++;
                    continue;
                }
                //map.put(liaoCheng.get(j-1).getFullname(),num);
                listA.add(liaoCheng.get(j-1).getFullname());
                listB.add(num);
                num=1;
            }else{
                //单独处理最后两组的比较
                if (liaoCheng.get(liaoCheng.size()-2).getItem_id()==liaoCheng.get(liaoCheng.size()-1).getItem_id()){
                    //map.put(liaoCheng.get(liaoCheng.size()-1).getFullname(),num+1);
                    listA.add(liaoCheng.get(liaoCheng.size()-1).getFullname());
                    listB.add(num+1);
                }else{
                    //map.put(liaoCheng.get(liaoCheng.size()-2).getFullname(),num);
                    listA.add(liaoCheng.get(liaoCheng.size()-2).getFullname());
                    listB.add(num);
                    //map.put(liaoCheng.get(liaoCheng.size()-1).getFullname(),1);
                    listA.add(liaoCheng.get(liaoCheng.size()-1).getFullname());
                    listB.add(1);

                }
            }
        }

    };
    @Override
    public int getCount() {
        return huiYuan.size()+listA.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null){
            holder=new ViewHolder();
            view=View.inflate(context.getApplicationContext(), R.layout.style_day_table_list,null);
            holder.name=(TextView) view.findViewById(R.id.name);
            holder.numbers=(TextView) view.findViewById(R.id.numbers);
            holder.ll_cards_type=(LinearLayout) view.findViewById(R.id.ll_cards_type);
            view.setTag(holder);
        }else{
            holder=(ViewHolder) view.getTag();
        }
       /* //填充数据
        if(a==1&&b==0){
            if(i==0){
                TextView textView = new TextView(context);
                textView.setText("会员卡");
                textView.setTextSize(14);
                textView.setPadding(40,30,10,30);
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundResource(R.color.danHuangse);
                return textView;
            }else{
                Cards cards=huiYuan.get(i-1);
                holder.name.setText(cards.getFullname());
                holder.numbers.setText("余"+cards.getAmount()+"元");
                return view;
            }

        }else if (a==1&&b==1){
            if (i==0){
                TextView textView = new TextView(context);
                textView.setText("会员卡");
                textView.setTextSize(14);
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundResource(R.color.danHuangse);
                textView.setPadding(40,30,10,30);
                return textView;
            }else if(i>0&&i<huiYuan.size()+1){
                Cards cards=huiYuan.get(i-1);
                holder.name.setText(cards.getFullname());
                holder.numbers.setText("余"+cards.getAmount()+"元");
                return view;
            }else if(i==huiYuan.size()+1){
                TextView textView = new TextView(context);
                textView.setText("疗程卡");
                textView.setBackgroundResource(R.color.danHuangse);
                textView.setTextSize(14);
                textView.setTextColor(Color.BLACK);
                textView.setPadding(40,30,10,30);
                return textView;
            }else if(i>1+huiYuan.size()){
                Cards cards=liaoCheng.get(i-huiYuan.size()-2);
                holder.name.setText(cards.getFullname());
                holder.numbers.setText("余"+cards.getAmount()+"元");
                return view;
            }
        }else if (a==0&&b==1){
            if (i==0){
                TextView textView = new TextView(context);
                textView.setText("疗程卡");
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundResource(R.color.danHuangse);
                textView.setTextSize(14);
                textView.setPadding(40,30,10,30);
                return textView;
            }else{
                Cards cards=liaoCheng.get(i-1);
                holder.name.setText(cards.getFullname());
                holder.numbers.setText("余"+cards.getAmount()+"元");
                return view;
            }
        }*/
        //填充数据
        if (a==1&&b==0){
            Cards cards=huiYuan.get(i);
            if (i==0){
                TextView tv=new TextView(context);
                tv.setText("会员卡");
                tv.setBackgroundResource(R.color.weixin_lianxiren_gray);
                tv.setPadding(30,10,10,10);
                tv.setTextSize(16);
                holder.ll_cards_type.addView(tv,0);
                holder.name.setText(cards.getFullname());
                holder.numbers.setText("余"+cards.getAmount()+"元");
                return view;
            }else{
                holder.name.setText(cards.getFullname());
                holder.numbers.setText("余"+cards.getAmount()+"元");
                return view;
            }
        }else if(a==1&&b==1){
            if (i==0){
                Cards cards=huiYuan.get(i);
                TextView tv=new TextView(context);
                tv.setText("会员卡");
                tv.setBackgroundResource(R.color.weixin_lianxiren_gray);
                tv.setPadding(30,10,10,10);
                tv.setTextSize(16);
                holder.ll_cards_type.addView(tv,0);
                holder.name.setText(cards.getFullname());
                holder.numbers.setText("余"+cards.getAmount()+"元");
                return view;
            }else if(i>0&&i<huiYuan.size()){
                Cards cards=huiYuan.get(i);
                holder.name.setText(cards.getFullname());
                holder.numbers.setText("余"+cards.getAmount()+"元");
                return view;
            }else if(i==huiYuan.size()){
                TextView tv=new TextView(context);
                tv.setText("疗程卡");
                tv.setBackgroundResource(R.color.weixin_lianxiren_gray);
                tv.setPadding(30,10,10,10);
                tv.setTextSize(16);
                holder.ll_cards_type.addView(tv,0);
                holder.name.setText(listA.get(0));
                holder.numbers.setText(listB.get(0)+"次");
                return view;
            }else{
                holder.name.setText(listA.get(i-huiYuan.size()));
                holder.numbers.setText(listB.get(i-huiYuan.size())+"次");
                return view;
            }
        }else if(a==0&&b==1){
            if(i==0){
                TextView tv=new TextView(context);
                tv.setText("疗程卡");
                tv.setBackgroundResource(R.color.weixin_lianxiren_gray);
                tv.setPadding(30,10,10,10);
                tv.setTextSize(16);
                holder.ll_cards_type.addView(tv,0);
                holder.name.setText(listA.get(0));
                holder.numbers.setText(listB.get(0)+"次");
                return view;
            }else{
                holder.name.setText(listA.get(i));
                holder.numbers.setText(listB.get(i)+"次");
                return view;
            }
        }

        return null;
    }
    class ViewHolder{
        TextView name,numbers;
        LinearLayout ll_cards_type;
    }
    //排序
    class MyComparator implements Comparator
    {
        //这里的o1和o2就是list里任意的两个对象，然后按需求把这个方法填完整就行了
        @Override
        public int compare(Object o1, Object o2) {
            Cards lhs=(Cards) o1;
            Cards rhs=(Cards) o2;
            if (lhs.getItem_id() > rhs.getItem_id())
            {
                return 1;
            }
            if (lhs.getItem_id() == rhs.getItem_id())
            {
                return 0;
            }
            if (lhs.getItem_id() < rhs.getItem_id())
            {
                return -1;
            }
            return 0;
        }
    }
}
