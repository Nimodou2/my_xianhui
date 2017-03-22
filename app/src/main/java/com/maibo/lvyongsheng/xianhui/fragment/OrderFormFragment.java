package com.maibo.lvyongsheng.xianhui.fragment;

import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by LYS on 2017/1/13.
 */

public class OrderFormFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.ll_head)
    LinearLayout ll_head;

    @Bind(R.id.tab)
    LinearLayout tab;
    @Bind(R.id.tv_unfinish)
    TextView tv_unfinish;
    @Bind(R.id.tv_finish)
    TextView tv_finish;
    @Bind(R.id.iv_over)
    ImageView iv_over;
    @Bind(R.id.vp_mywork)
    ViewPager vp_mywork;

    private View rootView;
    private UnFinishedOrderFragment mUnFinished;
    private FinishedOrderFragment mFinished;
    List<Fragment> fragmentList;

    private int bmpw = 0; // 游标宽度
    private int offset = 0;// // 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_order_form, null);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        currIndex = 0;
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    /**
     * 初始化View
     */
    private void initView() {
        mUnFinished = new UnFinishedOrderFragment();
        mFinished = new FinishedOrderFragment();
        fragmentList = new ArrayList<>();
        fragmentList.add(mUnFinished);
        fragmentList.add(mFinished);
        tv_unfinish.setOnClickListener(this);
        tv_finish.setOnClickListener(this);
        initCursorPos();
        vp_mywork.setOffscreenPageLimit(2);
        vp_mywork.setAdapter(new MyFragmentAdapters(getChildFragmentManager()));
        vp_mywork.setOnPageChangeListener(new MyPagerChangerListener());

    }

    //初始化指示器位置
    public void initCursorPos() {
        // 初始化动画
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        tv_finish.measure(0, 0);
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tv_finish.measure(spec, spec);
        int measuredWidth = tv_finish.getMeasuredWidth();


        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) iv_over.getLayoutParams();
        params.width=measuredWidth;
        params.leftMargin=screenWidth / 4-(measuredWidth)/2;
        iv_over.setLayoutParams(params);

        bmpw = screenWidth / 2;// 获取图片宽度

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 8 );// 计算偏移量

        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        iv_over.setImageMatrix(matrix);// 设置动画初始位置
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_unfinish:
                changeView(0);
                setTabColor(tv_unfinish,tv_finish);
                break;
            case R.id.tv_finish:
                changeView(1);
                setTabColor(tv_finish,tv_unfinish);
                break;
        }
    }

    /**
     * 设置tab的字体颜色
     */
    private void setTabColor(TextView one,TextView two) {
        one.setTextColor(getResources().getColor(R.color.textcolor3));
        two.setTextColor(getResources().getColor(R.color.textcolor4));
    }

    //ViewPager适配器
    class MyFragmentAdapters extends FragmentStatePagerAdapter {
        public MyFragmentAdapters(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        /**
         * 每次更新完成ViewPager的内容后，调用该接口，此处复写主要是为了让导航按钮上层的覆盖层能够动态的移动
         */
        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    /**
     * ViewPager监听器
     */
    class MyPagerChangerListener implements ViewPager.OnPageChangeListener {

        int one = bmpw;// 页卡1 -> 页卡2 偏移量

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
                    }
                    setTabColor(tv_unfinish,tv_finish);
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    }
                    setTabColor(tv_finish,tv_unfinish);
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

    /**
     * 手动设置ViewPager要显示的视图
     *
     * @param desTab
     */
    private void changeView(int desTab) {
        vp_mywork.setCurrentItem(desTab, true);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
