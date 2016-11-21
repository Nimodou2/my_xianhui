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
import com.maibo.lvyongsheng.xianhui.entity.Card;
import com.maibo.lvyongsheng.xianhui.entity.Product;

import java.util.HashMap;
import java.util.List;


/**
 * Created by LYS on 2016/9/17.
 */
public class ProductAddListViewExpandAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Product> dataPr;
    private int[] sel;
    private ExpandableListView expandableListView;

    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;
    //记录选中的对象
    private static HashMap<Integer,Integer> itemID;

    class ViewHolder {

        TextView tvName;
        CheckBox cb;
        TextView card_name,card_style,price,yu_ci;
    }
    public ProductAddListViewExpandAdapter(Context context, List<Product> dataPr, int[] sel, ExpandableListView expandableListView){
        this.dataPr = dataPr;
        this.context = context;
        this.sel = sel;
        this.expandableListView=expandableListView;
        isSelected = new HashMap<Integer, Boolean>();
        itemID=new HashMap<>();

        // 初始化数据
        initDate();
    }

    public void initDate(){
        for(int i=0;i<dataPr.size();i++){

            getIsSelected().put(i, false);
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


        ViewHolder holder;
        final Product bean = dataPr.get(i);
        LayoutInflater inflater = LayoutInflater.from(context);
        if (view == null) {
            view = inflater.inflate(R.layout.style_add_project_item_plan, null);
            holder = new ViewHolder();
            holder.cb = (CheckBox) view.findViewById(R.id.checkBox);
            holder.tvName = (TextView) view.findViewById(R.id.tv_project_name);
            view.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) view.getTag();
        }

        holder.tvName.setText(bean.getFullname());
        if (bean.getCard_list().size()!=0){
           holder.tvName.setTextColor(Color.GREEN);
        }

        //将服务器中已经有的项目在checkBox中选中
        for (int j=0;j<sel.length;j++){

            if (sel[j]==bean.getItem_id()){
                isSelected.put(i, true);
                itemID.put(i,bean.getItem_id());
                setItemID(itemID);
            }
        }

        // 监听checkBox并根据原来的状态来设置新的状态
        holder.cb.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (isSelected.get(i)) {
                    isSelected.put(i, false);
                    setIsSelected(isSelected);
                    itemID.put(i,null);
                    setItemID(itemID);

                } else {
                    isSelected.put(i, true);
                    setIsSelected(isSelected);
                    itemID.put(i,bean.getItem_id());
                    setItemID(itemID);
                }

            }
        });

        //可有效解决Groupview被抢夺焦点的问题
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.groupExpand);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (b)
                {
                    expandableListView.collapseGroup(i);

                } else
                {
                    expandableListView.expandGroup(i);
                }
            }
        });

        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(getIsSelected().get(i));
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if(view==null){
            holder=new ViewHolder();
            view=View.inflate(context,R.layout.style_card_list,null);
            holder.card_name=(TextView) view.findViewById(R.id.card_name);
            holder.card_style=(TextView) view.findViewById(R.id.card_style);
            holder.price=(TextView) view.findViewById(R.id.price);
            holder.yu_ci=(TextView) view.findViewById(R.id.yu_ci);

            view.setTag(holder);
        }else{
            holder=(ViewHolder) view.getTag();
        }

        Card card=dataPr.get(i).getCard_list().get(i1);
        holder.card_name.setText(card.getFullname());
        holder.card_style.setText(card.getCard_class());
        holder.price.setText(card.getPrice());
        holder.yu_ci.setText(card.getTimes()+"");
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
    public HashMap<Integer,Integer> getItemID(){
        return itemID;
    }
    public void setItemID(HashMap<Integer,Integer> itemID){
        ProductAddListViewExpandAdapter.itemID=itemID;
    }

}
