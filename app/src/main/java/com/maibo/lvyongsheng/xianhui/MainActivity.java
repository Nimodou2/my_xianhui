package com.maibo.lvyongsheng.xianhui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.implement.Util;
import com.maibo.lvyongsheng.xianhui.myinterface.SampleMultiplePermissionListener;
import com.maibo.lvyongsheng.xianhui.serviceholdermessage.ServiceDatas;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import cn.leancloud.chatkit.utils.LCIMConstants;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseFragment implements View.OnClickListener{

    SharedPreferences sp;
    private int mIndex;
    Fragment[] mFragments;
    ConversationListFragment converFragment;
    ContactPersonFragment LianFragment;
    SettingFragment settingFragment;
    String apiURL, token;
    String user_id = "";
    @Bind(android.R.id.content)
    ViewGroup rootView;
    @Bind(R.id.ll_msg)
    LinearLayout ll_msg;
    @Bind(R.id.ll_man)
    LinearLayout ll_man;
    @Bind(R.id.ll_setting)
    LinearLayout ll_setting;
    @Bind(R.id.imageview_msg)
    ImageView imageview_msg;
    @Bind(R.id.imageview_man)
    ImageView imageview_man;
    @Bind(R.id.imageview_setting)
    ImageView imageview_setting;
    @Bind(R.id.textview_msg)
    TextView textview_msg;
    @Bind(R.id.textview_man)
    TextView textview_man;
    @Bind(R.id.textview_setting)
    TextView textview_setting;
    @Bind(R.id.ll_tab)
    LinearLayout ll_tab;


    public PermissionListener contactsPermissionListener;
    private MultiplePermissionsListener allPermissionsListener;
    String whatPermission="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);

        CloseAllActivity.getScreenManager().pushActivity(this);

        ButterKnife.bind(this);
        createPermissionListeners();

        initView();
        //获取通知传递过来的值
//        processExtraData();
        initFragment();
        //注册消息推送服务
        registPushService();
        Dexter.continuePendingRequestsIfPossible(allPermissionsListener);

        EventBus.getDefault().register(this);
    }

    private void initView() {

        setTabHeightAndWidth();

        ll_msg.setOnClickListener(this);
        ll_man.setOnClickListener(this);
        ll_setting.setOnClickListener(this);

        sp = getSharedPreferences("baseDate", MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);

        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean("isDestroyMainActivity",false);
        editor.commit();
    }

    /**
     * 动态设置底部导航栏的宽和高
     */
    private void setTabHeightAndWidth() {
        int viewHeight= (Util.getScreenHeight(this)-getStatusBarHeight())/35;
        LinearLayout.LayoutParams params_msg= (LinearLayout.LayoutParams) imageview_msg.getLayoutParams();
        params_msg.height=(int)(viewHeight*1.6);
        params_msg.width=(int)(viewHeight*1.6);
        imageview_msg.setLayoutParams(params_msg);

        LinearLayout.LayoutParams params_man= (LinearLayout.LayoutParams) imageview_man.getLayoutParams();
        params_man.height=(int)(viewHeight*1.6);
        params_man.width=(int)(viewHeight*1.6);
        imageview_man.setLayoutParams(params_man);

        LinearLayout.LayoutParams params_setting= (LinearLayout.LayoutParams) imageview_setting.getLayoutParams();
        params_setting.height=(int)(viewHeight*1.6);
        params_setting.width=(int)(viewHeight*1.6);
        imageview_setting.setLayoutParams(params_setting);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_msg:
                setIndexSelected(0);
                selectImageResouse(imageview_msg,imageview_man,imageview_setting,0);
                selectTextColor(textview_msg,textview_man,textview_setting);
                break;
            case R.id.ll_man:
                setIndexSelected(1);
                selectImageResouse(imageview_msg,imageview_man,imageview_setting,1);
                selectTextColor(textview_man,textview_msg,textview_setting);
                break;
            case R.id.ll_setting:
                setIndexSelected(2);
                selectImageResouse(imageview_msg,imageview_man,imageview_setting,2);
                selectTextColor(textview_setting,textview_man,textview_msg);
                break;
        }
    }

    /**
     * 导航栏文字颜色选择器
     */
    private void selectTextColor(TextView a,TextView b,TextView c) {
        a.setTextColor(getResources().getColor(R.color.colorLightYellow));
        b.setTextColor(getResources().getColor(R.color.gray_weixin));
        c.setTextColor(getResources().getColor(R.color.gray_weixin));
    }

    /**
     * 导航栏按钮选择器
     */
    private void selectImageResouse(ImageView a,ImageView b,ImageView c,int d) {
        if (d==0){
            a.setImageResource(R.drawable.msg_yes);
            b.setImageResource(R.drawable.man_no);
            c.setImageResource(R.drawable.setting_no);
        }else if (d==1){
            a.setImageResource(R.drawable.msg_no);
            b.setImageResource(R.drawable.man_yes);
            c.setImageResource(R.drawable.setting_no);
        }else if (d==2){
            a.setImageResource(R.drawable.msg_no);
            b.setImageResource(R.drawable.man_no);
            c.setImageResource(R.drawable.setting_yes);
        }

    }


    private void initFragment() {
        converFragment = new ConversationListFragment();
        LianFragment = new ContactPersonFragment();
        settingFragment = new SettingFragment();

        //添加到数组
        mFragments = new Fragment[]{converFragment, LianFragment, settingFragment};

        //开启事务
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //添加首页
        ft.add(R.id.tabcontent, LianFragment).commit();
        //默认设置为第1个
        mIndex = 1;
        setIndexSelected(1);


    }

    private void setIndexSelected(int index) {

        if (mIndex == index) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //隐藏
        ft.hide(mFragments[mIndex]);
        //判断是否添加
        if (!mFragments[index].isAdded()) {
            ft.add(R.id.tabcontent, mFragments[index]).show(mFragments[index]);
        } else {
            ft.show(mFragments[index]);
        }
        ft.commit();
        //再次赋值
        mIndex = index;

    }

    /**
     * 注册Leancloud推送服务
     */
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        //processExtraData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }

    /**
     * 接收通知发送过来的数据，暂时没用
     */
    public void processExtraData() {
        //接收通知
        Intent intent = getIntent();
        String notice_id = intent.getStringExtra("notice_id");
        String notice_type = intent.getStringExtra("notice_type");
        if (!TextUtils.isEmpty(notice_id) && notice_type.equals("daily_report")) {
            //跳转到日报表界面
            Intent intent1 = new Intent(this, DayTabActivity.class);
            int noticeID = Integer.parseInt(notice_id);
            intent1.putExtra("notice_id", noticeID);
            startActivity(intent1);
        } else if (!TextUtils.isEmpty(notice_id) && notice_type.equals("project_plan")) {
            //跳转到工作界面
            Intent intent1 = new Intent(this, WorkActivity.class);
            startActivity(intent1);
        } else if (!TextUtils.isEmpty(notice_id) && notice_type.equals("common_notice")) {
            startActivity(new Intent(this, RemindActivity.class));
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
//        super.onAttachFragment(fragment);
        if (converFragment == null && fragment instanceof ConversationListFragment) {
            converFragment = (ConversationListFragment) fragment;
        } else if (LianFragment == null && fragment instanceof ContactPersonFragment) {
            LianFragment = (ContactPersonFragment) fragment;
        } else if (settingFragment == null && fragment instanceof SettingFragment) {
            settingFragment = (SettingFragment) fragment;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean("isDestroyMainActivity",true);
        editor.commit();
        EventBus.getDefault().unregister(this);
        CloseAllActivity.getScreenManager().popActivity(this);
        //每次销毁MainActivity时都要上传一下ConverstionId
        uploadConversationId();

    }

    /**
     * 上传ConversationId
     */
    private void uploadConversationId() {
        List<String> convIdList = LCIMConversationItemCache.getInstance().getSortedConversationList();
        if(convIdList.size()!=0){
            StringBuffer buffer=new StringBuffer();
            for (int i=0;i<convIdList.size();i++){
                if (i!=convIdList.size()-1){
                    buffer.append(convIdList.get(i)+",");
                }else{
                    buffer.append(convIdList.get(i));
                }
            }
            //上传
            ServiceDatas sd=new ServiceDatas(this);
            sd.uploadConversationIdToService(buffer.toString().trim());
        }
    }

    /**
     * 注册权限监听器
     */
    private void createPermissionListeners() {
//        PermissionListener feedbackViewPermissionListener = new SamplePermissionListener(this);
        MultiplePermissionsListener feedbackViewMultiplePermissionListener =
                new SampleMultiplePermissionListener(this);

        //多个权限
        if (allPermissionsListener==null){
            allPermissionsListener =
                    new CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
                            SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(rootView,
                                    "请在设置中开启权限")
                                    .withOpenSettingsButton("设置")
                                    .build());
        }
    }

    /**
     * 监听联系人点击列表点击事件，判读是否获取权限
     *
     * @param event
     */
    public void onEvent(EventDatas event) {
        if (event.getTag().equals(Constants.OPEN_ALL_PERMISSION)) {
            whatPermission=Constants.OPEN_ALL_PERMISSION;
            if (Dexter.isRequestOngoing()) {
                return;
            }
            user_id = event.getResponse();
            Dexter.checkPermissions(allPermissionsListener, Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO);
        }else if (event.getTag().equals(Constants.OPEN_CAMERA_AND_READ_EXTERNAL_STORAGE_PERMISSION)){
            whatPermission=Constants.OPEN_CAMERA_AND_READ_EXTERNAL_STORAGE_PERMISSION;
            if (Dexter.isRequestOngoing()) {
                return;
            }
            Dexter.checkPermissions(allPermissionsListener, Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }else if (event.getTag().equals(Constants.UPLOAD_CONVERSAYION_ID)){
            if (event.getResponse().equals("error")){
                App.showToast(this,"网络错误");
            }else if (event.getResponse().equals("right")){
                Log.e("right","上传成功");
            }else{
                App.showToast(this,event.getResponse());
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showPermissionRationale(final PermissionToken token) {
        new AlertDialog.Builder(this).setTitle("提醒")
                .setMessage("请开启该功能所需权限!")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                })
                .show();
    }

    /**
     * 获取权限成功
     * @param permission
     */
    public void showPermissionGranted(String permission,int number) {

        if (number==3&&whatPermission.equals(Constants.OPEN_ALL_PERMISSION)){
            if (!TextUtils.isEmpty(user_id)) {
                Intent intent = new Intent(this, LCIMConversationActivity.class);
                intent.putExtra(LCIMConstants.PEER_ID, user_id);
                startActivity(intent);
            }
        }else if (number==2&&whatPermission.equals(Constants.OPEN_CAMERA_AND_READ_EXTERNAL_STORAGE_PERMISSION)){

            startActivity(new Intent(this,MySelfInformationActivity.class));

        }

    }

    /**
     * 获取权限失败
     * @param permission
     * @param isPermanentlyDenied
     */
    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {

    }

}
