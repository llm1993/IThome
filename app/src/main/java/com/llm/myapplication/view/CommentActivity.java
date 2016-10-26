package com.llm.myapplication.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.llm.myapplication.R;
import com.llm.myapplication.adapter.CommentViewAdapter;
import com.llm.myapplication.adapter.ListViewAdapter;
import com.llm.myapplication.beans.Comment;
import com.llm.myapplication.beans.NewsBean;
import com.llm.myapplication.utils.JsonUtils;
import com.llm.myapplication.utils.Utils;
import com.llm.myapplication.utils.XmlUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

public class CommentActivity extends AppCompatActivity implements BGARefreshLayout.BGARefreshLayoutDelegate{
    private BGARefreshLayout mRefreshLayout;
    private ListView listView;
    private List<Comment> beans = new ArrayList<Comment>();
    private List<Comment> beansAdd = new ArrayList<Comment>();
    private CommentViewAdapter commentViewAdapter;
    private String newsid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.comment_toolbar);
        toolbar.setTitle("评论");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        listView = (ListView) findViewById(R.id.comment_listView);
        commentViewAdapter = new CommentViewAdapter((ArrayList<Comment>) beans, this);
        listView.setAdapter(commentViewAdapter);
        Intent intent = getIntent();
        newsid = intent.getStringExtra("newsid");
        initRefreshLayout();
        beginRefreshing();
    }
    private void initRefreshLayout() {
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.comment_refresh);
        // 为BGARefreshLayout设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);

        // 为了增加下拉刷新头部和加载更多的通用性，提供了以下可选配置选项  -------------START
        // 设置正在加载更多时的文本
        refreshViewHolder.setLoadingMoreText("loadingMoreText");
    }
    // 通过代码方式控制进入正在刷新状态。应用场景：某些应用在activity的onStart方法中调用，自动进入正在刷新状态获取最新数据
    public void beginRefreshing() {
        mRefreshLayout.beginRefreshing();
    }

    // 通过代码方式控制进入加载更多状态
    public void beginLoadingMore() {
        mRefreshLayout.beginLoadingMore();
    }
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        // 在这里加载最新数据
        if (Utils.isNetworkConnected(getApplicationContext())) {
            // 如果网络可用，则加载网络数据
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    beansAdd = JsonUtils.getCommentList(Utils.getCommentJsonUrl(newsid));
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    // 加载完毕后在UI线程结束下拉刷新
                    mRefreshLayout.endRefreshing();

                    beans.addAll(0, beansAdd);
                    LinkedHashSet<Comment> set = new LinkedHashSet();
                    set.addAll(beans);
                    beans.clear();
                    beans.addAll(set);
                    commentViewAdapter.notifyDataSetChanged();
                }
            }.execute();
        } else {
            // 网络不可用，结束下拉刷新
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            mRefreshLayout.endRefreshing();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (Utils.isNetworkConnected(getApplicationContext())) {
            // 如果网络可用，则异步加载网络数据，并返回true，显示正在加载更多
            new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... params) {
                    String floorid = beans.get(beans.size() - 1).getM().getCi();
                    beansAdd = JsonUtils.getCommentList(Utils.getCommentJsonUrl(newsid,floorid));
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer result) {

                    // 加载完毕后在UI线程结束加载更多
                    mRefreshLayout.endLoadingMore();
                    beans.addAll(beansAdd);
                    commentViewAdapter.notifyDataSetChanged();
                }
            }.execute();
            return true;
        } else {
            // 网络不可用，返回false，不显示正在加载更多
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
