package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.LCChatKitUser;
import okhttp3.Call;

/**
 * 登陆页面
 */
public class LoginActivity extends Activity implements View.OnClickListener{

  protected EditText nameView,passwordView;
  protected TextView loginButton;
  private TextView btnLogin,tv_back;
  LinearLayout tv_change_shop;
  LinearLayout ll_logig_interface;
  private SharedPreferences sp,sp1;
  private final String APP_ID  = "wciU4iW0lEVmc9EJ9WzmhyGw-gzGzoHsz";
  private final String APP_KEY = "eXUtSMYSxVCJhE4IHOiGWabv";
  String passwords;
  ProgressDialog dialog;

  Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what){
        case 0:
          //弹出对话框提醒可短信登录
          final AlertDialog aDialog=new AlertDialog.Builder(LoginActivity.this).create();
          aDialog.setTitle("提示");
          aDialog.setMessage("用户不存在!");
          aDialog.setButton("短信登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              //跳转到验证界面
              Intent intent=new Intent(LoginActivity.this,IdentifyActivity.class);
              intent.putExtra("password",passwords);
              startActivity(intent);
            }
          });
          aDialog.setButton2("重试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              aDialog.dismiss();
            }
          });
          aDialog.show();
          break;
        case 1:
          String guID=(String)msg.obj;
          //预加载
          LCChatKit.getInstance().setProfileProvider(CustomUserProvider.getInstance(LoginActivity.this));
          AVOSCloud.setDebugLogEnabled(true);
          AVIMClient.setMessageQueryCacheEnable(true);
          LCChatKit.getInstance().init(getApplicationContext(), APP_ID, APP_KEY);
          LCChatKit.getInstance().open(guID, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {}});
          Intent intent = new Intent(LoginActivity.this, MainActivity.class);
          startActivity(intent);
          finish();
          dialog.dismiss();
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    dialog=new ProgressDialog(this);
    dialog.setMessage("加载中...");
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    dialog.setCancelable(true);
    dialog.setIndeterminate(false);

    sp=getSharedPreferences("baseDate",MODE_PRIVATE);
    sp1=getSharedPreferences("changeAccount",MODE_PRIVATE);
    nameView = (EditText) findViewById(R.id.activity_login_et_username);
    passwordView = (EditText) findViewById(R.id.activity_login_et_password);
    tv_back= (TextView) findViewById(R.id.tv_back);
    tv_back.setOnClickListener(this);
    nameView.setHintTextColor(Color.rgb(186,186,188));
    passwordView.setHintTextColor(Color.rgb(186,186,188));
    loginButton = (TextView) findViewById(R.id.activity_login_btn_login);
    btnLogin = (TextView) findViewById(R.id.btn_login);
    ll_logig_interface= (LinearLayout) findViewById(R.id.ll_logig_interface);
    tv_change_shop= (LinearLayout) findViewById(R.id.tv_change_shop);
    ll_logig_interface.setVisibility(View.VISIBLE);
    tv_change_shop.setVisibility(View.GONE);

    //判断登录状态
    int isSuccess=sp.getInt("success",0);
    String guid=sp.getString("guid",null);
    if(isSuccess==1){
      ll_logig_interface.setVisibility(View.INVISIBLE);
      //预加载
      if (CustomUserProvider.partUsers.size()>0){
        tv_change_shop.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
      }else{
        //此处显示启动页
        tv_change_shop.setVisibility(View.VISIBLE);
        getMyCustomerList(sp.getString("apiURL",null),sp.getString("token",null)
                ,sp.getString("displayname",null),sp.getString("guid",null));
      }
    }else{
      ll_logig_interface.setVisibility(View.VISIBLE);
      //普通账户密码登录
      btnLogin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          //获取输入框内的用户名和密码
          dialog.show();
          String names = nameView.getText().toString().trim();
          passwords = passwordView.getText().toString().trim();
          getDate(names,passwords);
          //使软键盘主动消失
          InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
          imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
      });

    }


  }

  /**
   * 首次登录
   * @param name
   * @param password
     */
  private void getDate(final String name,final String password){
    OkHttpUtils
            .post()
            .url("http://sso.sosys.cn:8080/mybook/rest/loginmobile")
            .addParams("mobile",name)
            .addParams("password",password)
            .addParams("type","employee")
            .build()
            .execute(new StringCallback()
            {
              @Override
              public void onError(Call call, Exception e, int id) {

              }

              @Override
              public void onResponse(String response, int id) {
                Log.e("Login:",response);
                SharedPreferences.Editor editor=sp.edit();
                SharedPreferences.Editor editor1=sp1.edit();

                //登录判断
                JsonObject obj = new JsonParser().parse(response).getAsJsonObject();
                String status=obj.get("status").getAsString().trim();
                if(status.equals("error")){
                  Message msg=Message.obtain();
                  msg.what=0;
                  msg.arg1=0;
                  handler.sendMessage(msg);
                }else{
                  JsonObject data = obj.get("data").getAsJsonObject();
                  String token =data.get("token").getAsString();
                  String apiURL =data.get("api_url").getAsString();
                  String guID =data.get("guid").getAsString();
                  String avator_url = data.get("avator_url").getAsString();
                  int init_login_password=data.get("init_login_password").getAsInt();
                  String displayname=data.get("display_name").getAsString();
                 if (init_login_password==0){
                   //保存登录成功状态、基础数据
                   //Log.e("普通登录:",response);
                   editor.putString("userName", name);
                   editor.putString("password", password);
                   editor1.putString("userName", name);
                   editor1.putString("password", password);
                   editor.putInt("success",1);
                   editor.putString("token", token);
                   editor.putString("apiURL",apiURL);
                   editor.putString("guid",guID);
                   editor.putString("avator_url",avator_url);
                   editor.putString("displayname",displayname);
                   editor.commit();
                   editor1.commit();
                   getMyCustomerList(apiURL,token,displayname,guID);
                 }else{
                   //带上电话号码（userName）
                   Intent intent=new Intent(getApplicationContext(), UpdataPasswordActivity.class);
                   intent.putExtra("userName",name);
                   startActivity(intent);
                   finish();
                 }

                }
              }
            });
  }

  @Override
  public void onClick(View view) {
    //返回到WelcomeGuideActivity界面
    startActivity(new Intent(this,SplashActivity.class));
    finish();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {

    if (keyCode==KeyEvent.KEYCODE_BACK){
      startActivity(new Intent(this,SplashActivity.class));
      finish();
      return false;
    }else{
      return super.onKeyDown(keyCode, event);
    }
  }

  //获取用户体系
  public void getMyCustomerList(String apiURL,String token,final String name,final String guid){
    //初始化用户信息
    OkHttpUtils
            .post()
            .url(apiURL+"/rest/employee/getuserlist")
            .addParams("token",token)
            .build()
            .execute(new StringCallback() {
              @Override
              public void onError(Call call, Exception e, int id) {

              }
              @Override
              public void onResponse(String response, int id) {
                //获取员工信息
                CustomUserProvider.partUsers.clear();
                JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                JsonArray array = object.get("data").getAsJsonArray();
                for (JsonElement jsonElement:array){
                  JsonObject jObject = jsonElement.getAsJsonObject();
                  String names=jObject.get("display_name").getAsString();
                  String guid=jObject.get("guid").getAsString();
                  String avator_url=jObject.get("avator_url").getAsString();
                  CustomUserProvider.partUsers.add(new LCChatKitUser(guid, names, avator_url));
                }
                Message msg=Message.obtain();
                msg.what=1;
                msg.arg1=0;
                msg.obj=guid;
                handler.sendMessage(msg);
              }
            });
  }
}
