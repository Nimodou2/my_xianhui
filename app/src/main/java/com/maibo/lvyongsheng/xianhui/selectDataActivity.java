package com.maibo.lvyongsheng.xianhui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * Created by LYS on 2017/1/5.
 */

public class selectDataActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.tv_body)
    TextView tv_body;
    @Bind(R.id.date_picker)
    DatePicker datePicker;
    @Bind(R.id.ll_start_date)
    LinearLayout ll_start_date;
    @Bind(R.id.tv_start_dot)
    TextView tv_start_dot;
    @Bind(R.id.tv_start_date)
    TextView tv_start_date;
    @Bind(R.id.ll_end_date)
    LinearLayout ll_end_date;
    @Bind(R.id.tv_end_dot)
    TextView tv_end_dot;
    @Bind(R.id.tv_end_date)
    TextView tv_end_date;
    @Bind(R.id.tv_reset)
    TextView tv_reset;
    @Bind(R.id.tv_confirm)
    TextView tv_confirm;
    //标明选择的是开始还是结束,0:代表：开始；1：代表：结束；
    int status = 0;
    String start_date = "";
    String end_date = "";
    long start_million_date=0;
    long end_million_date=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        //setTheme(R.style.Transparent);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select_data);
        animotion1();
        animotion2();
        CloseAllActivity.getScreenManager().pushActivity(this);

        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        tv_body.setOnClickListener(this);
        ll_start_date.setOnClickListener(this);
        ll_end_date.setOnClickListener(this);
        tv_reset.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        //初始化日历时间
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH);
        int day = ca.get(Calendar.DAY_OF_MONTH);
        datePickerListener(year, month, day);
        tv_start_date.setText(formatDate(year,month+1,day));
        tv_end_date.setText(formatDate(year,month+1,day));
        start_date=formatDate(year,month+1,day);
        end_date=formatDate(year,month+1,day);
        start_million_date=getMillionDate(year,month+1,day);
        end_million_date=getMillionDate(year,month+1,day);

    }

    /**
     * 日期选择监听器
     * @param year
     * @param month
     * @param day
     */
    private void datePickerListener(int year, int month, int day) {
        datePicker.init(year, month , day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                if (status == 0) {
                    start_date=formatDate(year, monthOfYear+1, dayOfMonth);
                    tv_start_date.setText(start_date);
                    start_million_date=getMillionDate(year,monthOfYear+1,dayOfMonth);
                } else {
                    //此处需要判断，结束时间必须大于开始时间
                    end_million_date=getMillionDate(year,monthOfYear+1,dayOfMonth);
                    if (end_million_date-start_million_date<0){
                        App.showToast(getApplicationContext(),"结束日期必须大于开始日期");
                    }else{
                        end_date=formatDate(year, monthOfYear+1, dayOfMonth);
                        tv_end_date.setText(end_date);
                    }
                }
            }
        });
    }

    /**
     * 格式化日期
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    private String formatDate(int year, int monthOfYear, int dayOfMonth) {
        // 日期转换为毫秒
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = year + "-" + monthOfYear + "-" + dayOfMonth;
        try {
            long timeStart = sdf.parse(start).getTime();
            //毫秒转化为日期
            String datess = sdf.format(timeStart);
            return datess;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 日期转换为毫秒
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     * @return
     */
    private long getMillionDate(int year, int monthOfYear, int dayOfMonth){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = year + "-" + monthOfYear + "-" + dayOfMonth;
        try {
            long timeStart = sdf.parse(start).getTime();
            return timeStart;
        } catch (Exception e) {
            return 0;
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        animotion1();
//        animotion2();
//    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    private void animotion1() {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                , Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f);
        translateAnimation.setDuration(300);
        animationSet.addAnimation(translateAnimation);
        ll_head.startAnimation(animationSet);

    }

    private void animotion2() {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                , Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        translateAnimation.setDuration(300);
        animationSet.addAnimation(translateAnimation);
        datePicker.startAnimation(animationSet);
    }

    private void animotion11() {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                , Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f);
        translateAnimation.setDuration(300);
        animationSet.addAnimation(translateAnimation);
        ll_head.startAnimation(animationSet);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void animotion22() {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                , Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        translateAnimation.setDuration(300);
        animationSet.addAnimation(translateAnimation);
        datePicker.startAnimation(animationSet);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_body:
                animotion11();
                animotion22();
                break;
            case R.id.ll_start_date:
                tv_start_dot.setVisibility(View.VISIBLE);
                tv_end_dot.setVisibility(View.INVISIBLE);
                status = 0;
                break;
            case R.id.ll_end_date:
                tv_start_dot.setVisibility(View.INVISIBLE);
                tv_end_dot.setVisibility(View.VISIBLE);
                status = 1;
                break;
            case R.id.tv_reset:
                //还原列表当前状态
                EventDatas eventDatas1 = new EventDatas(Constants.SELECT_DATA_ACTIVITY_RESET, "null");
                EventBus.getDefault().post(eventDatas1);
                animotion11();
                animotion22();
                break;
            case R.id.tv_confirm:
                //筛选
                if (end_million_date-start_million_date<0){
                    App.showToast(getApplicationContext(),"结束日期必须大于开始日期");
                }else{
                    String dates = start_date+","+end_date;
                    EventDatas eventDatas2 = new EventDatas(Constants.SELECT_DATA_ACTIVITY_CONFIRM, dates);
                    EventBus.getDefault().post(eventDatas2);
                    animotion11();
                    animotion22();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            animotion11();
            animotion22();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
