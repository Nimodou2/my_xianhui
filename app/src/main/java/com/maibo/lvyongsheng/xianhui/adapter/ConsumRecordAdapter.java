package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.SaleTab;

import java.util.List;


/**
 * Created by LYS on 2016/10/6.
 */
public class ConsumRecordAdapter extends BaseAdapter {
    private Context context;
    private List<SaleTab> list;
    private int viewHeight;
    public ConsumRecordAdapter(Context context, List<SaleTab> list,int viewHeight){
        this.context=context;
        this.list=list;
        this.viewHeight=viewHeight;
    }
    @Override
    public int getCount() {
        return list.size();
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
        View v=View.inflate(context.getApplicationContext(), R.layout.style_list_detail,null);
        LinearLayout ll_time = (LinearLayout) v.findViewById(R.id.ll_time);
        LinearLayout ll_all= (LinearLayout) v.findViewById(R.id.ll_all);
        TextView name=(TextView) v.findViewById(R.id.name);
        TextView numbers=(TextView) v.findViewById(R.id.numbers);
        SaleTab sale=list.get(i);

        ViewGroup.LayoutParams params=ll_all.getLayoutParams();
        params.height=viewHeight*20/255;
        ll_all.setLayoutParams(params);
        if (i==0){
            TextView tv=new TextView(context);
            setMySelfTextView(tv);
            tv.setText(sale.getSaledate());
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setBackgroundResource(R.color.weixin_lianxiren_gray);
            tv.setPadding(30,0,0,0);
            tv.setTextSize(14);
            ll_time.addView(tv,0);
            name.setText(sale.getFullname());
            numbers.setText(sale.getAmount()+"元");
            return v;
        }else if(i>0){
            SaleTab sale2=list.get(i-1);
            if(sale.getSaledate().equals(sale2.getSaledate())){
                name.setText(sale.getFullname());
                numbers.setText(sale.getAmount()+"元");
                return v;
            }else{
                TextView tv=new TextView(context);
                setMySelfTextView(tv);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setText(sale.getSaledate());
                tv.setBackgroundResource(R.color.weixin_lianxiren_gray);
                tv.setPadding(30,0,0,0);
                tv.setTextSize(14);
                ll_time.addView(tv,0);
                name.setText(sale.getFullname());
                numbers.setText(sale.getAmount()+"元");
                return v;
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
}
