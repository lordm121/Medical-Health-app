package com.rigoma.medicalhealth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private String webUrl = "https://medicalhealth.info";
    private ValueCallback<Uri[]> mUploadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View splashScreen = findViewById(R.id.splash_screen);
        View loadingScreen = findViewById(R.id.loading_screen);
        webView = findViewById(R.id.web_view);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setHorizontalScrollBarEnabled(false);
        webView.loadUrl(webUrl);

        //open other links that are pressed in the same webview instead of sending it to another app/activity.
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());

                return true;
            }

            //when a new link starts loading, this method will run.
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadingScreen.setVisibility(View.VISIBLE);
            }

            //when the new link that started loading finishes and is fully ready, this method will run.
            @Override
            public void onPageFinished(WebView view, String url) {
                splashScreen.setVisibility(View.GONE);
                loadingScreen.setVisibility(View.GONE);
            }

            //when an error occurs while loading a link, this method will run.
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                //insert failed to load screen.
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mUploadMessage = filePathCallback;
                openImageChooser();
                return true;
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(Intent.createChooser(intent, "Choose File"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (mUploadMessage == null)
                return;

            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri[] result = new Uri[]{data.getData()};
                    mUploadMessage.onReceiveValue(result);
                } else {
                    mUploadMessage.onReceiveValue(null);
                }
            } else {
                mUploadMessage.onReceiveValue(null);
            }

            mUploadMessage = null;
        }
    }

    //go back to the previous state in the webview if it exists. if not, then leave the activity.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}