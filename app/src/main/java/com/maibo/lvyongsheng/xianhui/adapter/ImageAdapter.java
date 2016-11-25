package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;


/**
 * Created by LYS on 2016/9/5.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    int screenHight;
    int[] data;
    int k=0;

    //  图片数组源
    private int[] num=new int[7];
    int[]  y=new int[7];
    int position;
    float[] values1;
    int[] height=new int[7];

    public ImageAdapter(Context c, int[] data,int position,float[] values1) {
        mContext = c;
        this.data=data;
        this.position=position;
        this.values1=values1;

        for(int i=0;i<7;i++){
            //找出最大值
            if(data[i]>k){
                k=data[i];
            }
            //获取柱状图基本数据
            if (position==3||position==4){
                num[i]=data[i]*10;
            }else{
                num[i]=data[i];
            }

        }
        int[] value=new int[5];
        for (int i=0;i<5;i++){
            if (i<3){
                value[i]=(int)values1[i]*1000;
            }else{
                value[i]=(int)values1[i]*10;
            }

        }

        screenHight = mContext.getResources().getDisplayMetrics().heightPixels;
        for(int i=0;i<7;i++){
            if (num[i]!=0) {
                //避免柱状图过长
                int heights= ((screenHight / 3) * num[i]) / (value[position]);
                if (heights>screenHight/5){
                    height[i]=screenHight/5;
                }else{
                    height[i] = heights;
                }
            }else{
                height[i]=0;
            }
            if (height[i]<screenHight/3){
                y[i]=screenHight/3-height[i]-140;
            }else{
                y[i]=0;
            }

        }


    }

    @Override
    public int getCount() {
        return 7;
    }

    // 获取图片位置
    @Override
    public Object getItem(int position) {
        return num[position];
    }

    // 获取图片ID
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=View.inflate(mContext, R.layout.zhu_zhuang_tu, null);
        TextView tv2=(TextView) view.findViewById(R.id.tv2);
        //此处动态设置圆柱的高度(数据大于0)
        //处理办法:最大值不能大于屏幕的1/3;因此按比例进行取值
        //((screenHight/3)*value[position])/(num[i]*k)
       /* if(num[position]>screenHight/3){
            tv2.getLayoutParams().height=480;
        }else {
            tv2.getLayoutParams().height = num[position];
        }*/
        tv2.getLayoutParams().height =height[position];
        LinearLayout ll=(LinearLayout)view.findViewById(R.id.ll_zhu);
        ll.setY(y[position]);
        ll.setX(60);
        view.setLayoutParams(new Gallery.LayoutParams(200, screenHight/3));      // 设置布局
        return view;
    }
}
