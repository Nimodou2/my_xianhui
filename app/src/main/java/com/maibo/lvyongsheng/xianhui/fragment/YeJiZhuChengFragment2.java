package com.maibo.lvyongsheng.xianhui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.maibo.lvyongsheng.xianhui.ItemDetailActivity;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by LYS on 2016/9/12.
 */
public class YeJiZhuChengFragment2 extends Fragment {


    private PieChart mPieChart;
    String cardName;
    List<List<List<List<Card>>>> pie_data;
    List<List<Card>> pieData;
    int position;
    Integer[] baseColor={Color.rgb(21,147,242),Color.rgb(255,128,64),Color.rgb(128,255,128),Color.rgb(255,128,192),Color.rgb(255,0,0),
            Color.rgb(0,128,64),Color.rgb(0,64,128),Color.rgb(128,0,64),Color.rgb(64,128,128),Color.rgb(255,128,0),
            Color.rgb(128,128,192),Color.rgb(64,0,64),Color.rgb(255,255,128),Color.rgb(218,201,101),Color.rgb(222,90,228)};

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.yeji_zhucheng_fragment, null);
        mPieChart = (PieChart)view.findViewById(R.id.chart2);
        //获取数据
        ItemDetailActivity it=(ItemDetailActivity)getActivity();
        Intent intent=getActivity().getIntent();
        Bundle bundle=intent.getExtras();
        pie_data=(List<List<List<List<Card>>>>) bundle.get("pie");
        position = intent.getIntExtra("position",-1);

        //判断点击的目标
        if(position==0){
            cardName="顾客类型";
            pieData=pie_data.get(it.getTest()).get(0);
        }else if(position==1){
            pieData=pie_data.get(it.getTest()).get(1);
            cardName="员工类型";
        }else if(position==2){
            pieData=pie_data.get(it.getTest()).get(2);
            cardName="周转周期";
        }else if(position==3){
            pieData=pie_data.get(it.getTest()).get(3);
            cardName="客户类型";
        }else if(position==4){
            pieData=pie_data.get(it.getTest()).get(4);
            cardName="个人产值";
        }
        PieData mPieData =getPieData(100,pieData);
        showChart(mPieChart, mPieData,cardName);


        return view;
    }

    private void showChart(PieChart pieChart, PieData pieData,String cardName) {

        pieChart.setHoleRadius(60f);  //半径
        pieChart.setTransparentCircleRadius(30f); // 半透明圈
        pieChart.setHoleRadius(40); //实心圆

        Description description=new Description();
        description.setText("");
        pieChart.setDescription(description);
        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationAngle(0); // 初始旋转角度

        pieChart.setRotationEnabled(false); // 可以手动旋转
        pieChart.setUsePercentValues(true);  //显示成百分比
        pieChart.setCenterText(cardName);  //饼状图中间的文字

        //设置数据
        pieChart.setData(pieData);

        Legend mLegend = pieChart.getLegend();  //设置比例图
        mLegend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);  //最右边显示
        //mLegend.setForm(LegendForm.LINE);  //设置比例图的形状，默认是方形
        mLegend.setXEntrySpace(7f);
        mLegend.setYEntrySpace(5f);
        mLegend.setYOffset(30f);
       // pieChart.animateXY(1000, 1000);  //设置动画
    }

    /**
     * 把饼分几份
     * */
    private PieData getPieData(float range,List<List<Card>> data) {
        Log.e("data1,size:",data.get(1).size()+"");
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();  //yVals用来表示封装每个饼块的实际数据
        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容
        for (int i = 0; i < data.get(1).size(); i++) {
            xValues.add(data.get(1).get(i).getFullname());  //饼块上显示成Quarterly1, Quarterly2, Quarterly3, Quarterly4
            yValues.add(new PieEntry(data.get(1).get(i).getAmounts(), data.get(1).get(i).getFullname()));
        }


        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, "图例");//显示在比例图上
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
        ArrayList<Integer> colors = new ArrayList<Integer>();

        // 饼图颜色
        for (int i=0;i<data.get(1).size();i++){
            colors.add(baseColor[i]);
        }

        pieDataSet.setColors(colors);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px); // 选中态多出的长度

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.WHITE);
        return pieData;
    }
    public static String getRandColorCode(){
        String r,g,b;
        Random random = new Random();
        r = Integer.toHexString(random.nextInt(256)).toUpperCase();
        g = Integer.toHexString(random.nextInt(256)).toUpperCase();
        b = Integer.toHexString(random.nextInt(256)).toUpperCase();

        r = r.length()==1 ? "0" + r : r ;
        g = g.length()==1 ? "0" + g : g ;
        b = b.length()==1 ? "0" + b : b ;

        return r+g+b;
    }
}
