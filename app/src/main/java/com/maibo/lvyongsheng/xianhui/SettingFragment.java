package com.maibo.lvyongsheng.xianhui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.Call;

/**
 * Created by LYS on 2016/9/7.
 */
public class SettingFragment extends Fragment implements View.OnClickListener {
//    AvatarImageView avatarImageView;

    TextView tv_edite,tv_change,tv_myname;
    SharedPreferences sp,sp1;
    String apiURL;
    String token;
    String avator_url;
    View view;
    CircleImageView iv_circle_image;
    String myname;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.setting_fragment,container,false);
        CloseAllActivity.getScreenManager().pushActivity(getActivity());
        init(view);
        return view;
    }

    private void init(View view) {
        //初始化控件
        iv_circle_image= (CircleImageView) view.findViewById(R.id.iv_circle_image);
        tv_change =(TextView) view.findViewById(R.id.tv_change);
        tv_myname= (TextView) view.findViewById(R.id.tv_myname);
        tv_edite= (TextView) view.findViewById(R.id.tv_edite);
        tv_edite.setOnClickListener(this);
        //获取到了图片的正确位置
        sp= getActivity().getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        sp1=getActivity().getSharedPreferences("cropPicturnPath",Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
        avator_url = sp.getString("avator_url",null);
        myname=sp.getString("displayname",null);
        if (!TextUtils.isEmpty(myname))
            tv_myname.setText(myname);
        //下载头像
        if (!TextUtils.isEmpty(avator_url)){
            OkHttpUtils
                    .get()
                    .url(avator_url)
                    .build()
                    .execute(new BitmapCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }
                        @Override
                        public void onResponse(Bitmap response, int id) {
                            Drawable drawable =new BitmapDrawable(response);
                            iv_circle_image.setImageDrawable(drawable);
                        }
                    });
        }

        //退出监听事件
        tv_change.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_change:
                startActivity(new Intent(getActivity(),AgentActivity.class));
                break;
            case R.id.tv_edite:
                PermissionGen.needPermission(SettingFragment.this, 50,
                        new String[] {
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }
                );
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        init(view);
//        int isChangeDoor=sp.getInt("isChangeDoor",-1);
//        if (isChangeDoor==1){
//            init(view);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                     int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
    @PermissionSuccess(requestCode = 50)
    public void doSomething(){
        //权限已获得
            startActivity(new Intent(getActivity(),MySelfInformationActivity.class));
    }
    @PermissionFail(requestCode = 50)
    public void doFailSomething(){
        App.showToast(getContext(),"请设置权限");
    }
}
