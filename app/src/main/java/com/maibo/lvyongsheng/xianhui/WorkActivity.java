package com.maibo.lvyongsheng.xianhui;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.fragment.ColleagueFragment;
import com.maibo.lvyongsheng.xianhui.fragment.CustomerFragment;
import com.maibo.lvyongsheng.xianhui.fragment.ProductFragment;
import com.maibo.lvyongsheng.xianhui.fragment.ProjectFragment;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.implement.Util;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by LYS on 2016/9/12.
 */
public class WorkActivity extends FragmentActivity implements View.OnClickListener{

    TextView btn_customer,btn_partner,btn_project,btn_product;
    LinearLayout ll_head,tab;
    ViewPager vp;
    TextView back;
    ImageView iv_search,iv_choose;
    List<Fragment> data;
    ImageView iv_over;
    //屏幕宽度
    int screenWidth;
    //当前选中的项
    int currenttab=-1;

    private int bmpw = 0; // 游标宽度
    private int offset = 0;// // 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号

    Fragment cusF,collF,projF,prodF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        CloseAllActivity.getScreenManager().pushActivity(this);

        cusF=new CustomerFragment();
        collF=new ColleagueFragment();
        projF=new ProjectFragment();
        prodF=new ProductFragment();

        btn_customer=(TextView) findViewById(R.id.btn_customer);
        btn_partner=(TextView) findViewById(R.id.btn_partner);
        btn_project=(TextView) findViewById(R.id.btn_project);
        btn_product=(TextView) findViewById(R.id.btn_product);
        ll_head= (LinearLayout) findViewById(R.id.ll_head);
        iv_search= (ImageView) findViewById(R.id.iv_search);
        iv_choose= (ImageView) findViewById(R.id.iv_choose);
        iv_over=(ImageView) findViewById(R.id.iv_over);
        back= (TextView) findViewById(R.id.back);
        tab= (LinearLayout) findViewById(R.id.tab);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setHeightAndWidth();





        iv_choose.setOnClickListener(this);
        iv_search.setOnClickListener(this);

        btn_customer.setOnClickListener(this);
        btn_partner.setOnClickListener(this);
        btn_project.setOnClickListener(this);
        btn_product.setOnClickListener(this);
        vp=(ViewPager) findViewById(R.id.vp_mywork);
        data=new ArrayList<>();
        data.add(cusF);
        data.add(collF);
        data.add(projF);
        data.add(prodF);

        //初始化指示器位置
        initCursorPos();
        //iv_over.getLayoutParams().width=screenWidth/4-30;
       /* RelativeLayout.LayoutParams imageParams=new RelativeLayout.LayoutParams(screenWidth/2, btn2.getMeasuredHeight());
        iv_over.setLayoutParams(imageParams);*/
        vp.setOffscreenPageLimit(4);
        vp.setAdapter(new MyFragmentAdapters(getSupportFragmentManager()));
        vp.setOnPageChangeListener(new MyPagerChangerListener());

    }

    /**
     * 动态设置宽高
     */
    private void setHeightAndWidth() {
        ViewGroup.LayoutParams params=ll_head.getLayoutParams();
        params.height=((Util.getScreenHeight(this)-getStatusBarHeight())/35)*2;
        ll_head.setLayoutParams(params);

        ViewGroup.LayoutParams params_tab=tab.getLayoutParams();
        params_tab.height= (int) (((Util.getScreenHeight(this)-getStatusBarHeight())/35)*2.4);
        tab.setLayoutParams(params_tab);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_customer:
                changeView(0);
                break;
            case R.id.btn_partner:
                changeView(1);
                break;
            case R.id.btn_project:
                changeView(2);
                break;
            case R.id.btn_product:
                changeView(3);
                break;
            case R.id.iv_search:
                //跳转到搜索界面
                Intent intent=new Intent(this,SearchWorkActivity.class);
                intent.putExtra("tag",currIndex);
                startActivity(intent);
                break;
            case R.id.iv_choose:
                //获取当前Fragment对象
                if (currIndex==0){
                    CustomerFragment cf=(CustomerFragment)data.get(0);
                    cf.initPopupWindow();
                }else if (currIndex==1){
                    ColleagueFragment co=(ColleagueFragment)data.get(1);
                    co.initPopupWindow();
                }else if (currIndex==2){
                    ProjectFragment project=(ProjectFragment)data.get(2);
                    project.initPopupWindow();
                }else if (currIndex==3){
                    ProductFragment product=(ProductFragment)data.get(3);
                    product.initPopupWindow();
                }
                break;
        }
    }
    //初始化指示器位置
    public void initCursorPos() {
        // 初始化动画
        iv_over = (ImageView) findViewById(R.id.iv_over);
        screenWidth=getResources().getDisplayMetrics().widthPixels;
        btn_partner.measure(0, 0);
        iv_over.getLayoutParams().width=screenWidth/4;
        bmpw = screenWidth/4;// 获取图片宽度

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 4 - bmpw);// 计算偏移量

        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        iv_over.setImageMatrix(matrix);// 设置动画初始位置
    }

    //ViewPager适配器
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
    }
    //手动设置ViewPager要显示的视图
    private void changeView(int desTab)
    {
        vp.setCurrentItem(desTab, true);
    }

    class MyPagerChangerListener implements ViewPager.OnPageChangeListener{

        int one = bmpw;// 页卡1 -> 页卡2 偏移量
        int two = one * 2;// 页卡1 -> 页卡3 偏移量
        int three=one*3;//页面1->页面4偏移量

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            Animation animation = null;
            switch (position) {
                case 0:
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, 0, 0, 0);
                    }else if (currIndex==3){
                        animation = new TranslateAnimation(three, 0, 0, 0);
                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, one, 0, 0);
                    }else if (currIndex==3){
                        animation = new TranslateAnimation(three, one, 0, 0);
                    }
                    break;
                case 2:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, two, 0, 0);
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(one, two, 0, 0);
                    }else if (currIndex==3){
                        animation = new TranslateAnimation(three, two, 0, 0);
                    }
                    break;
                case 3:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, three, 0, 0);
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(one, three, 0, 0);
                    }else if (currIndex==2){
                        animation = new TranslateAnimation(two, three, 0, 0);
                    }
                    break;
            }
            currIndex = position;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            iv_over.startAnimation(animation);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }

    /**
     * 获取状态栏高度
     * @return
     */
    public  int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result =  getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
