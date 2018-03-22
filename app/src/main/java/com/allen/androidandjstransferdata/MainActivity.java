package com.allen.androidandjstransferdata;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 实现JS调用Android代码
 */
public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private WebSettings mWebViewSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        initView();
        initWebView();
        mWebView.loadUrl("file:///android_asset/demo.html");
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                Toast.makeText(MainActivity.this, "url = " + url + "---message = " + message + "----result =" + result.toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    /**
     * 设置WebView支持
     */
    private void initWebView() {
        mWebViewSettings = mWebView.getSettings();
        //设置WebView允许执行JavaScript
        mWebViewSettings.setJavaScriptEnabled(true);
        mWebViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.addJavascriptInterface(this, "android");
    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
    }

    @android.webkit.JavascriptInterface
    public void onLogin(String userName, String passWord) {
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passWord)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        showAlert(userName, passWord);
    }

    private void showAlert(String userName, String pwd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);
        builder.setView(view);
        TextView txtName = view.findViewById(R.id.dialog_userName);
        TextView txtPwd = view.findViewById(R.id.dialog_pwd);
        TextView txtOk = view.findViewById(R.id.btn_ok);
        txtName.setText(userName);
        txtPwd.setText(pwd);
        final AlertDialog dialog = builder.create();
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                doCallJS();
            }
        });
        dialog.show();
    }

    /**
     * 调用JS代码
     */
    private void doCallJS() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //会重新加载Url
                mWebView.loadUrl("javascript:loginResult()");
                mWebView.evaluateJavascript("javascript:loginResult()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
