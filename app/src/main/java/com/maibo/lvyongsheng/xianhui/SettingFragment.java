package com.maibo.lvyongsheng.xianhui;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.implement.Util;
import com.maibo.lvyongsheng.xianhui.utils.Md5;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import de.greenrobot.event.EventBus;
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

    TextView tv_edite,tv_change,tv_myname,tv_change_password;
    TextView tv_feedback_problem,tv_about_app;
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
        if (view==null){
            view=inflater.inflate(R.layout.setting_fragment,container,false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
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
        tv_change_password= (TextView) view.findViewById(R.id.tv_change_password);
        LinearLayout ll_setting= (LinearLayout) view.findViewById(R.id.ll_setting);
        tv_feedback_problem= (TextView) view.findViewById(R.id.tv_feedback_problem);
        tv_about_app= (TextView) view.findViewById(R.id.tv_about_app);

        MainActivity parentActivity=(MainActivity)getActivity();
        ViewGroup.LayoutParams params=ll_setting.getLayoutParams();
        params.height=((Util.getScreenHeight(getContext())-parentActivity.getStatusBarHeight())/35)*2;
        ll_setting.setLayoutParams(params);

        tv_edite.setOnClickListener(this);
        tv_change_password.setOnClickListener(this);
        tv_feedback_problem.setOnClickListener(this);
        tv_about_app.setOnClickListener(this);
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

                EventDatas eventDatas=new EventDatas(Constants.OPEN_CAMERA_AND_READ_EXTERNAL_STORAGE_PERMISSION,"");
                EventBus.getDefault().post(eventDatas);
                break;
            case R.id.tv_change_password:
                //弹出密码验证框
                showDialog();
                break;
            case R.id.tv_feedback_problem:
                //跳转到问题反馈界面
                startActivity(new Intent(getActivity(),FeedbackProblemActivity.class));
                break;
            case R.id.tv_about_app:
                //跳转到关于闲惠界面
                startActivity(new Intent(getActivity(),IntrduceAppActivity.class));
//                startActivity(new Intent(getActivity(),StartProjectActivity.class));
                break;
        }
    }

    /**
     * 验证原密码
     */
    private void showDialog() {
        View views=View.inflate(getActivity(),R.layout.dialog_identify_password_style,null);
        final EditText et_original_password= (EditText) views.findViewById(R.id.et_original_password);
        final AlertDialog alertDialog=new AlertDialog.Builder(getActivity())
                .setView(views)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .show();
        Button btn=alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String et_text=et_original_password.getText().toString().trim();
                if (!TextUtils.isEmpty(et_text)){
                    //验证密码
                    String userName=sp.getString("userName",null);
                    String publicKey="1addfcf4296d60f0f8e0c81cea87a099";
                    String sign=new Md5().getSign2(userName,"employee",et_text,publicKey);
                    indentfyOriPassword(userName,"employee",et_text,sign);
                    alertDialog.dismiss();

                }else{
                    App.showToast(getActivity(),"不能为空!");
                }

            }
        });
    }

    /**
     * 验证原始密码
     */
    private void indentfyOriPassword(final String userName, String type, String password, String sign) {
        OkHttpUtils
                .post()
                .url("http://sso.sosys.cn:8080/mybook/rest/verifyloginpassword")
                .addParams("username",userName)
                .addParams("type",type)
                .addParams("password",password)
                .addParams("sign",sign)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject=new JsonParser().parse(response).getAsJsonObject();
                        String status="";
                        String message="";
                        if (!jsonObject.get("status").isJsonNull())
                            status=jsonObject.get("status").getAsString();
                        if (!jsonObject.get("message").isJsonNull())
                            message=jsonObject.get("message").getAsString();
                        if (status.equals("ok")){
                            //跳转到修改密码界面
                            Intent intent=new Intent(getActivity(),UpdataPasswordActivity.class);
                            intent.putExtra("tag","changePassword");
                            intent.putExtra("userName",userName);
                            startActivity(intent);
                        }else{
                            App.showToast(getActivity(),message);
                        }
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        init(view);
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
