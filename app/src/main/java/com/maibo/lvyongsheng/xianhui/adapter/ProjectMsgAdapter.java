package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;


/**
 * Created by LYS on 2016/10/8.
 */
public class ProjectMsgAdapter extends BaseAdapter {
    private Context context;
    private String[] left;
    private String[] right;
    private int what;
    private int viewHeight;
    public ProjectMsgAdapter(Context context,String[] left,String[] right,int what,int viewHeight){
        this.context=context;
        this.left=left;
        this.right=right;
        this.what=what;
        this.viewHeight=viewHeight;
    }
    @Override
    public int getCount() {
        return left.length;
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
        if(what==1){
            //项目详细信息
            if(i<left.length-1){
                View v=View.inflate(context, R.layout.style_list_detail,null);
                TextView name=(TextView) v.findViewById(R.id.name);
                TextView numbers=(TextView)v.findViewById(R.id.numbers);
                LinearLayout ll_all= (LinearLayout) v.findViewById(R.id.ll_all);
                setViewHeight(ll_all);
                name.setText(left[i]);
                numbers.setText(right[i]);
                return v;
            }else{
                View v=View.inflate(context,R.layout.style_day_table_list,null);
                TextView name=(TextView) v.findViewById(R.id.name);
                TextView numbers=(TextView)v.findViewById(R.id.numbers);
                LinearLayout ll_item= (LinearLayout) v.findViewById(R.id.ll_item);
                setViewHeight(ll_item);
                name.setText(left[i]);
                numbers.setText(right[i]);
                return v;
            }
        }else if(what==2){
            //报表参数
            View v=View.inflate(context, R.layout.style_list_detail,null);
            TextView name=(TextView) v.findViewById(R.id.name);
            TextView numbers=(TextView)v.findViewById(R.id.numbers);
            LinearLayout ll_all= (LinearLayout) v.findViewById(R.id.ll_all);
            setViewHeight(ll_all);
            name.setText(left[i]);
            numbers.setText(right[i]);
            return v;
        }else if(what==3){
            //销售权限
            if(left.length==2){
                if(i==0){
                    View v=View.inflate(context,R.layout.style_day_table_list,null);
                    TextView name=(TextView) v.findViewById(R.id.name);
                    TextView numbers=(TextView)v.findViewById(R.id.numbers);
                    LinearLayout ll_item= (LinearLayout) v.findViewById(R.id.ll_item);
                    setViewHeight(ll_item);
                    name.setText(left[i]);
                    numbers.setText(right[i]);
                    return v;
                }else{
                    View v=View.inflate(context, R.layout.style_list_detail,null);
                    TextView name=(TextView) v.findViewById(R.id.name);
                    TextView numbers=(TextView)v.findViewById(R.id.numbers);
                    LinearLayout ll_all= (LinearLayout) v.findViewById(R.id.ll_all);
                    setViewHeight(ll_all);
                    name.setText(left[i]);
                    numbers.setText(right[i]);
                    return v;
                }
            }else if(left.length==4){

                if (i==0){
                    View v=View.inflate(context,R.layout.style_day_table_list,null);
                    TextView name=(TextView) v.findViewById(R.id.name);
                    TextView numbers=(TextView)v.findViewById(R.id.numbers);
                    LinearLayout ll_item= (LinearLayout) v.findViewById(R.id.ll_item);
                    setViewHeight(ll_item);
                    name.setText(left[i]);
                    numbers.setText(right[i]);
                    return v;
                }else if(i==3){
                    View v=View.inflate(context,R.layout.style_day_table_list,null);
                    TextView name=(TextView) v.findViewById(R.id.name);
                    TextView numbers=(TextView)v.findViewById(R.id.numbers);
                    LinearLayout ll_item= (LinearLayout) v.findViewById(R.id.ll_item);
                    setViewHeight(ll_item);
                    name.setText(left[i]);
                    numbers.setText(right[i]);
                    return v;
                }

            }

        }
       return  null;
    }

    /**
     * 动态设置条目的高度
     * @param ll_all
     */
    private void setViewHeight(LinearLayout ll_all) {
        ViewGroup.LayoutParams params=ll_all.getLayoutParams();
        params.height=viewHeight*20/255;
        ll_all.setLayoutParams(params);
    }
}
