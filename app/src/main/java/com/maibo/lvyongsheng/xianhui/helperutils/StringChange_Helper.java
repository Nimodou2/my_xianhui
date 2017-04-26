package com.maibo.lvyongsheng.xianhui.helperutils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/4/1 0001.
 */

public class StringChange_Helper {
    private static final String TAG="StringChange_Helper";
    private static  StringChange_Helper helper;
    private Resources resources;
    private TimeSelectorListner timeSelectorListner;

    public void setTimeSelectorListner(TimeSelectorListner timeSelectorListner) {
        this.timeSelectorListner = timeSelectorListner;
    }
    public interface TimeSelectorListner{
        public void getTimeType(TextView textView,int type,String result);
    }
    public StringChange_Helper( ) {

    }
    public static StringChange_Helper getInstance(){
        if(helper==null){
            helper=new StringChange_Helper();
        }
        return helper;
    }
    public String getChangeDateNum(String time){
        String result=null;
        if(time!=null&&time.length()>0){
            result=new String();
            result=time.substring(time.lastIndexOf("-")+1);
        }
        return result;
    }

    public void setBgColor(RelativeLayout relativeLayout, Context context, TextView textView , int type, int colornum){
        if(context!=null) {
            resources = context.getResources();
        }
        switch (type){
            case 0://linearlayout背景
                if(relativeLayout!=null){
                    if(colornum==0){
                        relativeLayout.setBackgroundResource(R.drawable.zhushou_realitive_day7);
                    }else if(colornum==1){
                        relativeLayout.setBackgroundResource(R.drawable.zhushou_realitive_day1);
                    }else if(colornum==2){
                        relativeLayout.setBackgroundResource(R.drawable.zhushou_realitive_day2);
                    }else if(colornum==3){
                        relativeLayout.setBackgroundResource(R.drawable.zhushou_realitive_day3);
                    }else if(colornum==4){
                        relativeLayout.setBackgroundResource(R.drawable.zhushou_realitive_day4);
                    }else if(colornum==5){
                        relativeLayout.setBackgroundResource(R.drawable.zhushou_realitive_day5);
                    }else if(colornum==6){
                        relativeLayout.setBackgroundResource(R.drawable.zhushou_realitive_day6);
                    }
                }
                break;
            case 1://数字的颜色
                if(textView!=null){
                    textView.setBackgroundResource(R.drawable.zhushou_textview_bottom_circle);
                    if(colornum==0){
                        textView.setTextColor(resources.getColor(R.color.zhushou_text_7));
                    }else if(colornum==1){
                        textView.setTextColor(resources.getColor(R.color.zhushou_text_1));
                        }else if(colornum==2){
                        textView.setTextColor(resources.getColor(R.color.zhushou_text_2));
                    }else if(colornum==3){
                        textView.setTextColor(resources.getColor(R.color.zhushou_text_3));
                    }else if(colornum==4){
                        textView.setTextColor(resources.getColor(R.color.zhushou_text_4));
                    }else if(colornum==5){
                        textView.setTextColor(resources.getColor(R.color.zhushou_text_5));
                    }else if(colornum==6){
                        textView.setTextColor(resources.getColor(R.color.zhushou_text_6));
                    }
                }
                break;
            case 2://小logo的背景
                if(textView!=null){
                    if(colornum==0){
                        textView.setBackgroundResource(R.drawable.zhushou_textviewbg_circle7);
                    }else if(colornum==1){
                        textView.setBackgroundResource(R.drawable.zhushou_textviewbg_circle1);
                    }else if(colornum==2){
                        textView.setBackgroundResource(R.drawable.zhushou_textviewbg_circle2);
                    }else if(colornum==3){
                        textView.setBackgroundResource(R.drawable.zhushou_textviewbg_circle3);
                    }else if(colornum==4){
                        textView.setBackgroundResource(R.drawable.zhushou_textviewbg_circle4);
                    }else if(colornum==5){
                        textView.setBackgroundResource(R.drawable.zhushou_textviewbg_circle5);
                    }else if(colornum==6){
                        textView.setBackgroundResource(R.drawable.zhushou_textviewbg_circle6);
                    }
                }

                break;
        }
    }
    public String getChangeDateForMD(String data){
        String result="";
        if(data!=null){
            String one=data.substring(5,data.lastIndexOf(" "));
            Log.e(TAG,one+"截取后的字符串");
            String mouth=one.substring(0,one.lastIndexOf("-"));
            Log.e(TAG,mouth+"截取后的字符串");
            String day=one.substring(3);
            Log.e(TAG,day+"截取后的字符串");
            if(mouth.charAt(0)=='0'){
                result=mouth.substring(1)+"月";
            }else {
                result=mouth+"月";
            }
            if(day.charAt(0)=='0'){
                result=result+day.substring(1)+"日";
            }else {
                result=result+day+"日";
            }
        }
        return result;
    }
    public void getSystemCurentTimeCompail(TextView textView,String mytime,TimeSelectorListner timeSelectorListner){
        this.timeSelectorListner=timeSelectorListner;
        Date now=new Date();
        SimpleDateFormat dateformat1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=dateformat1.format(now);
        Log.e(TAG,time+"当前系统时间");
        String Syear=time.substring(0,3);
        String Myear=mytime.substring(0,3);
        if(Syear.equals(Myear)){//年数相同
            String Sone=time.substring(5,time.lastIndexOf(" "));
            Log.e(TAG,Sone+"    当前系统分割");

            String Mone=mytime.substring(5,mytime.lastIndexOf(" "));
            Log.e(TAG,Mone+"    数据分割");

            String Smouth=Sone.substring(0,Sone.lastIndexOf("-"));
            Log.e(TAG,Smouth+"    当前系统月数");

            String Mmouth=Mone.substring(0,Mone.lastIndexOf("-"));
            Log.e(TAG,Mmouth+"    数据月数");

            if(Smouth.charAt(0)=='0'){
                Smouth=Smouth.substring(1);
            }
            if(Mmouth.charAt(0)=='0'){
                Mmouth=Mmouth.substring(1);
            }
            if(Smouth.equals(Mmouth)){//月数相同
                String Sday=Sone.substring(Sone.lastIndexOf("-")+1);
                Log.e(TAG,Sday+"    系统天数");


                String Mday=Mone.substring(3);
                Log.e(TAG,Mday+"    数据天数");

                if(Sday.charAt(0)=='0'){
                    Sday=Sday.substring(1);
                }
                if(Mday.charAt(0)=='0'){
                    Mday=Mday.substring(1);
                }
                if(Mday.equals(Sday)){
                    if(timeSelectorListner!=null) {
                        timeSelectorListner.getTimeType(textView,0,"今天  "+mytime.substring(mytime.lastIndexOf(" ")));//回调
                        Log.e(TAG,"     天数相同    今天");
                    }

                }else if(Integer.parseInt(Sday)-Integer.parseInt(Mday)==1){
                    if(timeSelectorListner!=null) {
                        timeSelectorListner.getTimeType(textView,1,"昨天  "+mytime.substring(mytime.lastIndexOf(" ")));//回调
                        Log.e(TAG,"     天数差1    昨天");
                    }
                }else if(Integer.parseInt(Sday)-Integer.parseInt(Mday)==2){
                    if(timeSelectorListner!=null) {
                        timeSelectorListner.getTimeType(textView,2,"前天  "+mytime.substring(mytime.lastIndexOf(" ")));//回调
                        Log.e(TAG,"     天数差2    前天");
                    }
                }else {
                    if(timeSelectorListner!=null){
                        timeSelectorListner.getTimeType(textView,3,mytime);//回调
                    }
                }
            }else {
                if(timeSelectorListner!=null){
                    timeSelectorListner.getTimeType(textView,3,mytime);//回调
                }
            }
        }else {
            if(timeSelectorListner!=null){
                timeSelectorListner.getTimeType(textView,3,mytime);//回调
            }
        }

    }
    public int getCompailWhithSysTime(String mytime){
        Date now=new Date();
        SimpleDateFormat dateformat1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=dateformat1.format(now);
        String Syear=time.substring(0,3);
        String Myear=mytime.substring(0,3);
        if(Syear.equals(Myear)){//年数相同
            String Sone=time.substring(5,time.lastIndexOf(" "));


            String Mone=mytime.substring(5,mytime.lastIndexOf(" "));


            String Smouth=Sone.substring(0,Sone.lastIndexOf("-"));


            String Mmouth=Mone.substring(0,Mone.lastIndexOf("-"));


            if(Smouth.charAt(0)=='0'){
                Smouth=Smouth.substring(1);
            }
            if(Mmouth.charAt(0)=='0'){
                Mmouth=Mmouth.substring(1);
            }
            if(Smouth.equals(Mmouth)){//月数相同
                String Sday=Sone.substring(Sone.lastIndexOf("-")+1);



                String Mday=Mone.substring(3);


                if(Sday.charAt(0)=='0'){
                    Sday=Sday.substring(1);
                }
                if(Mday.charAt(0)=='0'){
                    Mday=Mday.substring(1);
                }
                if(Mday.equals(Sday)){
                    return 0;

                }else if(Integer.parseInt(Sday)-Integer.parseInt(Mday)==1){
                    return 1;
                }else if(Integer.parseInt(Sday)-Integer.parseInt(Mday)==2){
                    return 2;
                }else {
                   return 3;
                }
            }else {
                return 3;
            }
        }else {
           return 3;
        }
    }
    public String getMy(String mytime){
        Date now=new Date();
        SimpleDateFormat dateformat1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=dateformat1.format(now);
        String Syear=time.substring(0,3);
        String Myear=mytime.substring(0,3);
        if(Syear.equals(Myear)){//年数相同
            String Sone=time.substring(5,time.lastIndexOf(" "));


            String Mone=mytime.substring(5,mytime.lastIndexOf(" "));


            String Smouth=Sone.substring(0,Sone.lastIndexOf("-"));


            String Mmouth=Mone.substring(0,Mone.lastIndexOf("-"));


            if(Smouth.charAt(0)=='0'){
                Smouth=Smouth.substring(1);
            }
            if(Mmouth.charAt(0)=='0'){
                Mmouth=Mmouth.substring(1);
            }
            if(Smouth.equals(Mmouth)){//月数相同
                String Sday=Sone.substring(Sone.lastIndexOf("-")+1);



                String Mday=Mone.substring(3);


                if(Sday.charAt(0)=='0'){
                    Sday=Sday.substring(1);
                }
                if(Mday.charAt(0)=='0'){
                    Mday=Mday.substring(1);
                }
                if(Mday.equals(Sday)){
                    return "今天     "+mytime.substring(mytime.lastIndexOf(" "));

                }else if(Integer.parseInt(Sday)-Integer.parseInt(Mday)==1){
                    return "昨天     "+mytime.substring(mytime.lastIndexOf(" "));
                }else if(Integer.parseInt(Sday)-Integer.parseInt(Mday)==2){
                    return "前天     "+mytime.substring(mytime.lastIndexOf(" "));
                }else {
                    return mytime;
                }
            }else {
                return mytime;
            }
        }else {
            return mytime;
        }
    }
    public String getOnlyTime(String mytime){
        if(mytime!=null&&mytime.length()>0){
            return mytime.substring(mytime.lastIndexOf(" "));
        }else {
            return "";
        }
    }

}
