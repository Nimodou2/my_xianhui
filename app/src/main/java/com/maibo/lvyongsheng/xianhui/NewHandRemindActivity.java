package com.maibo.lvyongsheng.xianhui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by LYS on 2016/12/16.
 */

public class NewHandRemindActivity extends BaseActivity {

    @Bind(R.id.webview) WebView webView;
    @Bind(R.id.pro_bar) ProgressBar pro_bar;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_hand_remind);
        CloseAllActivity.getScreenManager().pushActivity(this);
        initView();
    }

    private void initView() {
        adapterLitterBar(ll_head);
//        initWebView();

    }

    /**
     * 初始化webview
     */
    private void initWebView() {
        //为了支持JavaScript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        //优先使用缓存
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//LOAD_NO_CACHE:不使用缓存

        webView.loadUrl("http://baidu.com");
        //控制网页打开位置1、返回值为true时在当前应用中打开 2、为false时在浏览器中打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        //监听webview中内容加载进度
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    pro_bar.setVisibility(View.GONE);

                } else {
                    // 加载中
                    pro_bar.setVisibility(View.VISIBLE);
                    pro_bar.setProgress(newProgress);
                }

            }
        });
    }

    @OnClick(R.id.back)
    public void back(View view){
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
