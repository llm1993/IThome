package com.llm.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.llm.myapplication.R;
import com.llm.myapplication.beans.NewsBean;
import com.llm.myapplication.utils.GetContent;
import com.llm.myapplication.utils.Utils;
import com.llm.myapplication.utils.XmlUtils;

import java.util.ArrayList;
import java.util.List;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private List list = new ArrayList();
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                list.add(msg.obj.toString());
                webView.loadDataWithBaseURL(null, msg.obj.toString(), "text/html", "utf-8", null);
            }
            if (msg.what == 2) {
                webView.loadDataWithBaseURL(null, msg.obj.toString(), "text/html", "utf-8", null);
            }
            if (msg.what == -1) {
                Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.out.println(list.size());
            if (list.size() > 1) {
                Message message = new Message();
                message.what = 2;
                list.remove(list.size() - 1);
                message.obj = list.get(list.size() - 1);
                if (message.obj == null)
                    message.what = -1;
                handler.sendMessage(message);
            } else
                WebViewActivity.this.finish();
        }
        return true;
    }

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
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {

                if (url.contains("www.ithome.com")) {
                    url.substring(url.lastIndexOf("/"), url.lastIndexOf("."));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 1;
                            message.obj = GetContent.getHtml("标题", url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".")));
                            if (message.obj == null)
                                message.what = -1;
                            handler.sendMessage(message);
                        }
                    }).start();
                } else {
                    view.loadUrl(url);
                }
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
                    NewsBean newsBean = (NewsBean) intent.getSerializableExtra("bean");
                    XmlUtils.setContent(newsBean);
                    message.obj = GetContent.get(newsBean);
                    if (message.obj == null)
                        message.what = -1;
                    handler.sendMessage(message);
                }
            }).start();
        } else {
            // 网络不可用，返回false，不显示正在加载更多
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
        }
    }
}
