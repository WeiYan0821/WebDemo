package com.yule.show.webdemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private static final String TAG = "MainActivity";

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint({"SetJavaScriptEnabled", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 手機瀏海區
        WindowManager.LayoutParams lpp = getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lpp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        }
        getWindow().setAttributes(lpp);

        // 隱藏虛擬按鍵, 並且全屏
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) == 0) {
                // 虛擬按鍵出現要做的事情
                hideBottomUIMenu();
            }   // 虛擬按鍵消失要做的事情
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        webView = new WebView(this);

        FrameLayout rootView = findViewById(R.id.xxx);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.addView(webView, layoutParams);

        webView.setWebViewClient( new  Browser_home());
        webView.setWebChromeClient( new MyChrome());
        // 聲明WebSettings子類
        WebSettings webSettings = webView.getSettings();
        // 如果訪問的頁面中要與Javascript交互, 則webview必須設置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        // 若載入的 html 裡有JS 在執行動畫等操作，會造成資源浪費（CPU、電量）
        // 在 onStop 和 onResume 裡分別把 setJavaScriptEnabled() 給設定成 false 和 true 即可

        //設定自適應螢幕，兩者合用
        webSettings.setUseWideViewPort(true); //將圖片調整到適合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 縮放至螢幕的大小

        //縮放操作
        webSettings.setSupportZoom(false); // 支持縮放, 默認為true. 是下面那個的前提.
        webSettings.setBuiltInZoomControls(false); // 設置內置的縮放控件. 若為false, 則該WebView不可縮放
        webSettings.setDisplayZoomControls(false); // 隱藏原生的縮放控件

        // 關閉密碼保存提醒(false)  開啟密碼保存功能(true)
        webSettings.setSavePassword(false);

        // 是否支持多窗口，默認值false
        webSettings.setSupportMultipleWindows(false);
        // 是否可用Javascript(window.open)打開窗口，默認值 false
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setAllowContentAccess(true); // 是否可訪問Content Provider的資源，默認值 true
        // 設定可以訪問檔案
        webSettings.setAllowFileAccess(true);
        // 是否允許通過file url加載的Javascript讀取本地文件，默認值 false
        webSettings.setAllowFileAccessFromFileURLs(false);
        // 是否允許通過file url加載的Javascript讀取全部資源(包括文件,http,https)，默認值 false
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        // 支援通過JS開啟新視窗
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webView.requestFocus(View.FOCUS_DOWN);

        //自動播放影片
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        loadWebsite();

//        clearCookies(this);

    }

    @SuppressLint("ObsoleteSdkInt")
    protected void hideBottomUIMenu() {
        // 隱藏虛擬按鍵, 並且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            // for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void loadWebsite() {
        webView.loadUrl("https://hero-wars.com/");
    }

    private class Browser_home extends WebViewClient {
        Browser_home() {

        }

        // 开始载入页面时调用此方法，在这里我们可以设定一个loading的页面，告诉用户程序正在等待网络响应。

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d("view", "開始載入");
            Log.i(TAG, "onPageStarted:　");

            super.onPageStarted(view, url, favicon);

        }

        // 在页面加载结束时调用。我们可以关闭loading 条，切换程序动作。
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("view", "載入結束");
            Log.i(TAG, "onPageFinished: ");

            super.onPageFinished(view, url);

        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        // 連結跳轉都會走這個方法
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(TAG, "shouldOverrideUrlLoading : " + url);

            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    private class MyChrome extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {

        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
            b.setMessage(message);
            b.setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm());
            b.setCancelable(false);
            b.create().show();
            return true;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i(TAG , "title : " + title);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
            Log.i(TAG , "icon : " + icon);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        Log.i("VVVVVVV", "onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((ViewGroup) webView.getParent()).removeView(webView);
        webView.setTag(null);

        //清除当前webview访问的历史记录
        //只会webview访问历史记录里的所有记录除了当前访问记录
        webView.clearHistory();
        //这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
        webView.clearFormData();
        //清除网页访问留下的缓存
        //由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
        webView.clearCache(true);
        clearCookies(this);
        webView.destroy();
        webView = null;
    }

    public void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.startSync();
            cookieSyncMngr.sync();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
