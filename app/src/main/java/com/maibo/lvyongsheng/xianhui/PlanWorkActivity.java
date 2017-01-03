package com.maibo.lvyongsheng.xianhui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.fragment.PlanFragment;
import com.maibo.lvyongsheng.xianhui.fragment.YuYueFragment;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by LYS on 2016/9/12.
 */
public class PlanWorkActivity extends FragmentActivity implements View.OnClickListener{

    Button btn1,btn2;
    TextView back,tv_certain;
    ViewPager vp;
    List<Fragment> data;
    ImageView iv_over;
    //屏幕宽度
    int screenWidth;
    //当前选中的项
    int currenttab=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_work);
        CloseAllActivity.getScreenManager().pushActivity(this);
        btn1=(Button) findViewById(R.id.btn1);
        btn2=(Button) findViewById(R.id.btn2);
        back= (TextView) findViewById(R.id.back);
        tv_certain= (TextView) findViewById(R.id.tv_certain);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_certain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        iv_over=(ImageView) findViewById(R.id.iv_over);

        //iv_over.setVisibility(View.GONE);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        vp=(ViewPager) findViewById(R.id.vp_mywork);
        data=new ArrayList<>();
        data.add(new PlanFragment());
        data.add(new YuYueFragment());


        screenWidth=getResources().getDisplayMetrics().widthPixels;
        btn2.measure(0, 0);

        //iv_over.getLayoutParams().width=screenWidth/4;
       /* RelativeLayout.LayoutParams imageParams=new RelativeLayout.LayoutParams(screenWidth/2, btn2.getMeasuredHeight());
        iv_over.setLayoutParams(imageParams);*/
        vp.setAdapter(new MyFragmentAdapters(getSupportFragmentManager()));

    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn1:
                btn1.setBackgroundResource(R.drawable.shap_left_blue_bg);
                btn1.setTextColor(Color.WHITE);
                btn2.setBackgroundResource(R.drawable.white_soild_right);
                btn2.setTextColor(Color.rgb(5,115,226));
                changeView(0);
                break;
            case R.id.btn2:

                btn1.setBackgroundResource(R.drawable.white_soild_left);
                btn1.setTextColor(Color.rgb(5,115,226));
                btn2.setBackgroundResource(R.drawable.shap_right_blue_bg);
                btn2.setTextColor(Color.WHITE);
                changeView(1);
                break;
        }
    }

    //VuewPager适配器
    class MyFragmentAdapters extends FragmentStatePagerAdapter{
        public MyFragmentAdapters(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return data.get(position);
        }

        @Override
        public int getCount() {
            return data.size();
        }
        /**
         * 每次更新完成ViewPager的内容后，调用该接口，此处复写主要是为了让导航按钮上层的覆盖层能够动态的移动
         */
        @Override
        public void finishUpdate(ViewGroup container)
        {
            super.finishUpdate(container);//这句话要放在最前面，否则会报错
            //获取当前的视图是位于ViewGroup的第几个位置，用来更新对应的覆盖层所在的位置
            int currentItem=vp.getCurrentItem();
            if (currentItem==currenttab)
            {

                return ;
            }
            imageMove(vp.getCurrentItem()+1);
            currenttab=vp.getCurrentItem();
        }

    }
    /**
     * 移动覆盖层
     * @param moveToTab 目标Tab，也就是要移动到的导航选项按钮的位置
     * 第一个导航按钮对应0，第二个对应1，以此类推
     */
    private void imageMove(int moveToTab)
    {
        if(moveToTab==1){
            btn1.setBackgroundResource(R.drawable.shap_left_blue_bg);
            btn1.setTextColor(Color.WHITE);
            btn2.setBackgroundResource(R.drawable.white_soild_right);
            btn2.setTextColor(Color.rgb(5,115,226));
        }else if(moveToTab==2){
            btn1.setBackgroundResource(R.drawable.white_soild_left);
            btn1.setTextColor(Color.rgb(5,115,226));
            btn2.setBackgroundResource(R.drawable.shap_right_blue_bg);
            btn2.setTextColor(Color.WHITE);
        }
        int startPosition=0;
        int movetoPosition=0;

        startPosition=(currenttab+1)*(screenWidth/4);
        movetoPosition=moveToTab*(screenWidth/4);
        //平移动画
        TranslateAnimation translateAnimation=new TranslateAnimation(startPosition,movetoPosition, 0, 0);
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(100);
        //iv_over.startAnimation(translateAnimation);
    }
    //手动设置ViewPager要显示的视图
    private void changeView(int desTab)
    {
        vp.setCurrentItem(desTab, true);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
