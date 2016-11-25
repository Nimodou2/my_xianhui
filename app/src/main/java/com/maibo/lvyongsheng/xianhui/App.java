package com.maibo.lvyongsheng.xianhui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.karumi.dexter.Dexter;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import cn.leancloud.chatkit.LCChatKit;
import okhttp3.OkHttpClient;

/**
 * Created by wli on 16/2/24.
 */
public class App extends Application {

  // 专用ID和KEY
  private final String APP_ID  = "wciU4iW0lEVmc9EJ9WzmhyGw-gzGzoHsz";
  private final String APP_KEY = "eXUtSMYSxVCJhE4IHOiGWabv";
  String TAG="Application:";
  @Override
  public void onCreate() {
    super.onCreate();
    ZXingLibrary.initDisplayOpinion(this);
    SharedPreferences sp = getSharedPreferences("baseDate",MODE_PRIVATE);
    String guid=sp.getString("guid",null);
    //leanCloud即时通讯初始化配置
    if(guid!=null){
      //初始化用户体系

      //此处不可缺少
      AVOSCloud.setDebugLogEnabled(true);
      AVIMClient.setMessageQueryCacheEnable(true);
      LCChatKit.getInstance().init(getApplicationContext(), APP_ID, APP_KEY);

      LCChatKit.getInstance().open(guid, new AVIMClientCallback() {
        @Override
        public void done(AVIMClient avimClient, AVIMException e) {}});
    }
    //OKhttp初始化配置
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
            .readTimeout(10000L, TimeUnit.MILLISECONDS)
            //其他配置
            .build();

    OkHttpUtils.initClient(okHttpClient);

    //针对Android 6.0获取相机权限
    Dexter.initialize(getApplicationContext());


  }
  public static void showToast(Context context,String text){
    Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
  }


}
