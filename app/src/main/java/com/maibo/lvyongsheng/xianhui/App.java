package com.maibo.lvyongsheng.xianhui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.karumi.dexter.Dexter;
import com.maibo.lvyongsheng.xianhui.serviceholdermessage.MyselfMessageHandler;
import com.maibo.lvyongsheng.xianhui.utils.UserAgentInterceptor;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.zhy.autolayout.config.AutoLayoutConifg;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import cn.leancloud.chatkit.handler.LCIMClientEventHandler;
import cn.leancloud.chatkit.handler.LCIMConversationHandler;
import okhttp3.OkHttpClient;

/**
 * Created by wli on 16/2/24.
 */
public class App extends Application {

    // 专用ID和KEY
    private final String APP_ID = "wciU4iW0lEVmc9EJ9WzmhyGw-gzGzoHsz";
    private final String APP_KEY = "eXUtSMYSxVCJhE4IHOiGWabv";
    String TAG = "Application:";
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private  static  App myapp=null;

    public static App getMyapp() {
        return myapp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myapp=this;
        ZXingLibrary.initDisplayOpinion(this);
        SharedPreferences sp = getSharedPreferences("baseDate", MODE_PRIVATE);
        String guid = sp.getString("guid", null);

        AVOSCloud.setDebugLogEnabled(false);
        AVIMClient.setMessageQueryCacheEnable(true);
//    LCChatKit.getInstance().init(getApplicationContext(), APP_ID, APP_KEY);
        init(getApplicationContext(), APP_ID, APP_KEY);
        // AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, new LCIMMessageHandler(context));
        // 自定义实现的 AVIMClientEventHandler 需要注册到 SDK 后，SDK 才会通过回调 onClientOffline 来通知开发者
//        AVIMClient.setClientEventHandler(new AVImClientManager(getApplicationContext()));

        //OKhttp初始化配置
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new UserAgentInterceptor(getUserAgent()))
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
        //针对Android 6.0获取相机权限
        Dexter.initialize(getApplicationContext());
        AutoLayoutConifg.getInstance().init(this);
        initvolley();
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    private void initvolley() {
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        imageLoader=new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            int maxMery= (int) Runtime.getRuntime().maxMemory();
            LruCache<String ,Bitmap> lruCache=new LruCache<String ,Bitmap>(maxMery/8){
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes()*value.getHeight();
                }
            };
            @Override
            public Bitmap getBitmap(String url) {
                return lruCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                lruCache.put(url,bitmap);
            }
        });
    }


    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 该方法原本是在LCChatKit类中进行初始化的
     * 为了能够发送系统消息只能在此处进行初始化并重写发送通知的类
     *
     * @param context
     * @param appId
     * @param appKey
     */
    public void init(Context context, String appId, String appKey) {
        if (TextUtils.isEmpty(appId)) {
            throw new IllegalArgumentException("appId can not be empty!");
        } else if (TextUtils.isEmpty(appKey)) {
            throw new IllegalArgumentException("appKey can not be empty!");
        } else {
            AVOSCloud.initialize(context.getApplicationContext(), appId, appKey);
            AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, new MyselfMessageHandler(context));
            AVIMClient.setClientEventHandler(LCIMClientEventHandler.getInstance());
            AVIMMessageManager.setConversationEventHandler(LCIMConversationHandler.getInstance());
            AVIMClient.setOfflineMessagePush(true);

        }
    }

    /**
     * 获取手机相关信息
     *
     * @return
     */
    private String getUserAgent() {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(getApplicationContext());
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
