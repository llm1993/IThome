package com.llm.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.llm.myapplication.R;
import com.llm.myapplication.utils.GetContent;
import com.llm.myapplication.utils.Utils;

public class WebViewActivity extends AppCompatActivity {
    WebView webView;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                webView.loadDataWithBaseURL(null, msg.obj.toString(), "text/html", "utf-8", null);
            }
            if (msg.what == -1) {
                Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.webview_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        final Intent intent = getIntent();
        if (Utils.isNetworkConnected(getApplicationContext())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    message.obj = GetContent.getHtml(intent.getStringExtra("title").toString(), Utils.map.get(intent.getStringExtra("title")).toString());
                    if (message.obj == null)
                        message.what = -1;
                    handler.sendMessage(message);
                }
            }).start();
        }else {
            // 网络不可用，返回false，不显示正在加载更多
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
        }
    }
}
