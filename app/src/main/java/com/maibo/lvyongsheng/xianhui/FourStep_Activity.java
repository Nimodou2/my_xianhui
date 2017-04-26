package com.maibo.lvyongsheng.xianhui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maibo.lvyongsheng.xianhui.fragment.ColleagueFragment;
import com.maibo.lvyongsheng.xianhui.fragment.CustomerFragment;
import com.maibo.lvyongsheng.xianhui.fragment.ProductFragment;
import com.maibo.lvyongsheng.xianhui.fragment.ProjectFragment;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.utils.NetWorkUtils;

public class FourStep_Activity extends AppCompatActivity implements View.OnClickListener{
    private int type=0;
    private TextView text_back;
    private TextView text_title;
    private ImageView image_choose_date;
    private ImageView image_search;
    private ImageView image_choose;
    private LinearLayout linearlayout_include,in_loading_error;
    private Fragment cusF,collF,projF,prodF;
    private FragmentManager manager;
    private int dateload=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_step_);
        CloseAllActivity.getScreenManager().pushActivity(this);
        initview();
        if (NetWorkUtils.isNetworkConnected(getApplicationContext())){
            initdate();
        }else{
            in_loading_error.setVisibility(View.VISIBLE);
            linearlayout_include.setVisibility(View.GONE);
        }


    }
    private void initview() {
        text_back = (TextView) findViewById(R.id.fourstep_activity_text_back);
        text_title = (TextView) findViewById(R.id.fourstep_activity_text_title);
        image_choose_date = (ImageView) findViewById(R.id.fourstep_activity_image_choose_date);
        image_search = (ImageView) findViewById(R.id.fourstep_activity_image_search);
        image_choose = (ImageView) findViewById(R.id.fourstep_activity_image_choose);
        linearlayout_include = (LinearLayout) findViewById(R.id.fourstep_activity_linearlayout_fragment_include);
        in_loading_error= (LinearLayout) findViewById(R.id.in_loading_error);
    }
    private void initdate() {
        type=getIntent().getIntExtra("type",-1);
        dateload=1;
        if(type!=-1){
            manager=getSupportFragmentManager();
            FragmentTransaction transaction=manager.beginTransaction();
            switch (type){
                case 1://顾客
                    cusF=new CustomerFragment();
                    transaction.add(R.id.fourstep_activity_linearlayout_fragment_include,cusF).commit();
                    text_title.setText("顾客");
                    break;
                case 2://同事
                    collF=new ColleagueFragment();
                    transaction.add(R.id.fourstep_activity_linearlayout_fragment_include,collF).commit();
                    image_choose_date.setVisibility(View.INVISIBLE);
                    text_title.setText("同事");
                    break;
                case 3://项目
                    projF=new ProjectFragment();
                    transaction.add(R.id.fourstep_activity_linearlayout_fragment_include,projF).commit();
                    image_choose_date.setVisibility(View.INVISIBLE);
                    text_title.setText("项目");
                    break;
                case 4://产品
                    prodF=new ProductFragment();
                    transaction.add(R.id.fourstep_activity_linearlayout_fragment_include,prodF).commit();
                    image_choose_date.setVisibility(View.INVISIBLE);
                    text_title.setText("产品");
                    break;
                default:break;
            }
        }else {
            Toast.makeText(this,"数据传输出错",Toast.LENGTH_SHORT).show();
            in_loading_error.setVisibility(View.VISIBLE);
            linearlayout_include.setVisibility(View.INVISIBLE);
        }

        text_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        image_choose_date.setOnClickListener(this);
        image_choose.setOnClickListener(this);
        image_search.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fourstep_activity_image_choose_date:
                Intent intent=new Intent(this,selectDataActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                break;
            case R.id.fourstep_activity_image_search:
                if (NetWorkUtils.isNetworkConnected(this)&&dateload==1){
                    Intent intent2=new Intent(this,SearchWorkActivity.class);
                    intent2.putExtra("tag",type-1);
                    startActivity(intent2);
                }else{
                    App.showToast(getApplicationContext(),"网络连接异常");
                }
                break;
            case R.id.fourstep_activity_image_choose:
                getCurrentFragment();//动态的展示抽屉视图
                break;

        }
    }

    private void getCurrentFragment() {
        //获取当前Fragment对象
        if (NetWorkUtils.isNetworkConnected(this)&&dateload==1){
            if (type==1){
                CustomerFragment cf=(CustomerFragment)cusF;
                cf.initPopupWindow();
            }else if (type==2){
                ColleagueFragment co=(ColleagueFragment)collF;
                co.initPopupWindow();
            }else if (type==3){
                ProjectFragment project=(ProjectFragment)projF;
                project.initPopupWindow();
            }else if (type==4){
                ProductFragment product=(ProductFragment)prodF;
                product.initPopupWindow();
            }
        }else{
            App.showToast(getApplicationContext(),"网络连接异常");
        }
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
