package com.maibo.lvyongsheng.xianhui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.uuzuche.lib_zxing.activity.CaptureActivity;

public class MainActivity extends BaseFragment {

  RadioGroup radiogrop;
  RadioButton btn1, btn2, btn3;
  SharedPreferences sp;
  private int mIndex;
  Fragment[] mFragments;
  ConversationListFragment converFragment;
  LianXiRenFragment LianFragment;
  SettingFragment settingFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.new_activity_main);
    initView();
    //获取通知传递过来的值
    processExtraData();
    initFragment();
    setBound();
    //注册消息推送服务
    registPushService();
    radiogroupCheckListener();
  }

  private void initView() {
    radiogrop = (RadioGroup) findViewById(R.id.rg_menu);
    btn1 = (RadioButton) findViewById(R.id.button1);
    btn2 = (RadioButton) findViewById(R.id.button2);
    btn3 = (RadioButton) findViewById(R.id.button3);
    sp = getSharedPreferences("baseDate", MODE_PRIVATE);
  }

  private void radiogroupCheckListener() {
    radiogrop.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
          case R.id.button1:
            setIndexSelected(0);
            break;
          case R.id.button2:
            setIndexSelected(1);
            break;
          case R.id.button3:
            setIndexSelected(2);
            break;
        }
      }
    });
  }

  private void setBound() {
    Drawable drawable_msg = getResources().getDrawable(R.drawable.select_msg);
    Drawable drawable_man = getResources().getDrawable(R.drawable.select_man);
    Drawable drawable_setting = getResources().getDrawable(R.drawable.select_setting);
    //第一0是距左边距离，第二0是距上边距离，40分别是长宽
//    drawable_msg.setBounds(0, 0, 50, 50);
//    drawable_man.setBounds(0, 0, 50, 50);
//    drawable_setting.setBounds(0, 0, 50, 50);

    RadioButton button1 =(RadioButton)this.findViewById(R.id.button1);
    RadioButton button2 =(RadioButton)this.findViewById(R.id.button2);
    RadioButton button3 =(RadioButton)this.findViewById(R.id.button3);
    Drawable d1 = button1.getCompoundDrawables()[1];  //此处取的是Android:drawableTop的图片
    Drawable d2 = button2.getCompoundDrawables()[1];
    Drawable d3 = button3.getCompoundDrawables()[1];
    drawable_msg.setBounds(0, 0, d1.getIntrinsicWidth()/2, d1.getIntrinsicHeight()/2);
    drawable_man.setBounds(0, 0, d2.getIntrinsicWidth()/2, d2.getIntrinsicHeight()/2);
    drawable_setting.setBounds(0, 0, d3.getIntrinsicWidth()/2, d3.getIntrinsicHeight()/2);

    btn1.setCompoundDrawables(null, drawable_msg, null, null);
    btn2.setCompoundDrawables(null, drawable_man, null, null);
    btn3.setCompoundDrawables(null, drawable_setting, null, null);
  }

  private void initFragment() {
    converFragment =new ConversationListFragment();
    LianFragment =new LianXiRenFragment();
    settingFragment =new SettingFragment();

    //添加到数组
    mFragments = new Fragment[]{converFragment,LianFragment,settingFragment};

    //开启事务
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    //添加首页
    ft.add(R.id.tabcontent,LianFragment).commit();
    //默认设置为第1个
    mIndex=1;
    setIndexSelected(1);


  }
  private void setIndexSelected(int index) {

    if(mIndex==index){
      return;
    }
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction ft = fragmentManager.beginTransaction();
    //隐藏
    ft.hide(mFragments[mIndex]);
    //判断是否添加
    if(!mFragments[index].isAdded()){
      ft.add(R.id.tabcontent,mFragments[index]).show(mFragments[index]);
    }else {
      ft.show(mFragments[index]);
    }
    ft.commit();
    //再次赋值
    mIndex=index;

  }

  private void registPushService() {
    // 设置默认打开的 Activity
    PushService.setDefaultPushCallback(this, MainActivity.class);
    //订阅频道
    //在登录成功后注册频道，只要在保存 Installation 之前调用 PushService.subscribe 方法
    PushService.subscribe(this, sp.getString("guid", null), MainActivity.class);
    // 保存 installation 到服务器
    AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
      @Override
      public void done(AVException e) {
        AVInstallation.getCurrentInstallation().saveInBackground();
        String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
      }
    });
  }

  //打电话
  public void callPhone() {
    //检查权限
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
      //权限未获得
      //用于给用户一个申请权限的解释，该方法只有在用户在上一次已经拒绝过你的这个权限申请。也就是说，用户已经拒绝一次了，你又弹个授权框，你需要给用户一个解释，为什么要授权，则使用该方法。
      if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
        callPhone(); //重新请求一次
      } else {
        //请求权限
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
      }
    } else {
      //权限已获得
      Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
      startActivityForResult(intent, 1);
    }
  }
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                         int[] grantResults) {

    if (requestCode == 100) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        //如果有多个权限需要授权，会存储在grantResults数组中
        callPhone();
      } else {
        Toast.makeText(MainActivity.this, "授权被取消", Toast.LENGTH_SHORT).show();
      }
      return;
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
      setIntent(intent);
      processExtraData();
  }
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      finish();
    }
    return true;
  }
    //接收数据
    public void processExtraData(){
        //接收通知
        Intent intent = getIntent();
        String notice_id=intent.getStringExtra("notice_id");
        String notice_type=intent.getStringExtra("notice_type");
        if (!TextUtils.isEmpty(notice_id)&&notice_type.equals("daily_report")){
        //跳转到日报表界面
          Intent intent1=new Intent(this,DayTabActivity.class);
          int noticeID=Integer.parseInt(notice_id);
          intent1.putExtra("notice_id",noticeID);
          startActivity(intent1);
        }else if (!TextUtils.isEmpty(notice_id)&&notice_type.equals("project_plan")){
        //跳转到工作界面
          Intent intent1=new Intent(this,WorkActivity.class);
          startActivity(intent1);
        }else if (!TextUtils.isEmpty(notice_id)&&notice_type.equals("common_notice")){
          startActivity(new Intent(this,RemindActivity.class));
        }
    }

  @Override
  public void onAttachFragment(Fragment fragment) {
    super.onAttachFragment(fragment);
    if (converFragment==null && fragment instanceof ConversationListFragment){
      converFragment=(ConversationListFragment)fragment;
    }else if (LianFragment==null && fragment instanceof LianXiRenFragment){
      LianFragment=(LianXiRenFragment)fragment;
    }else if(settingFragment==null && fragment instanceof SettingFragment){
      settingFragment=(SettingFragment) fragment;
    }

  }
}
