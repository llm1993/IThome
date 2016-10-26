package com.llm.myapplication.view;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.llm.myapplication.R;
import com.llm.myapplication.adapter.ListViewAdapter;
import com.llm.myapplication.beans.NewsBean;
import com.llm.myapplication.utils.GetContent;
import com.llm.myapplication.utils.Utils;
import com.llm.myapplication.utils.XmlUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BGARefreshLayout.BGARefreshLayoutDelegate {
    private BGARefreshLayout mRefreshLayout;
    private ListView listView;
    private List<NewsBean> beans = new ArrayList<NewsBean>();
    private List<NewsBean> beansAdd = new ArrayList<NewsBean>();
    private ListViewAdapter listViewAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        GetContent.width = (int) (point.x / displayMetrics.density);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("首页");
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView);
        listViewAdapter = new ListViewAdapter((ArrayList<NewsBean>) beans, this);
        listView.setAdapter(listViewAdapter);
        ListsetOnItemClickListener();

        initRefreshLayout();
        beginRefreshing();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void initRefreshLayout() {
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.rl_modulename_refresh);
        // 为BGARefreshLayout设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);

        // 为了增加下拉刷新头部和加载更多的通用性，提供了以下可选配置选项  -------------START
        // 设置正在加载更多时的文本
        refreshViewHolder.setLoadingMoreText("loadingMoreText");
        // 设置整个加载更多控件的背景颜色资源id
        //refreshViewHolder.setLoadMoreBackgroundColorRes(loadMoreBackgroundColorRes);
        // 设置整个加载更多控件的背景drawable资源id
        //refreshViewHolder.setLoadMoreBackgroundDrawableRes(loadMoreBackgroundDrawableRes);
        // 设置下拉刷新控件的背景颜色资源id
        //refreshViewHolder.setRefreshViewBackgroundColorRes(refreshViewBackgroundColorRes);
        // 设置下拉刷新控件的背景drawable资源id
        //refreshViewHolder.setRefreshViewBackgroundDrawableRes(refreshViewBackgroundDrawableRes);
        // 设置自定义头部视图（也可以不用设置）     参数1：自定义头部视图（例如广告位）， 参数2：上拉加载更多是否可用
        //mRefreshLayout.setCustomHeaderView(mBanner, false);
        // 可选配置  -------------END
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        // 在这里加载最新数据
        if (Utils.isNetworkConnected(getApplicationContext())) {
            // 如果网络可用，则加载网络数据
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    String startId = null;
                    if (beans.size() != 0) {
                        startId = beans.get(0).getNewsID();
                    }
                    beansAdd = XmlUtils.getListBeans(startId, Utils.getNewsXmlUrl());
                    if (beansAdd == null) {
                        return -1;
                    }
                    if (beansAdd.size() == 0) {
                        return 1;
                    }
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    // 加载完毕后在UI线程结束下拉刷新
                    mRefreshLayout.endRefreshing();
                    if (result == -1) {
                        Toast.makeText(MainActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (result == 1) {
                        Toast.makeText(MainActivity.this, "暂无最新！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    beans.addAll(0, beansAdd);
                    LinkedHashSet<NewsBean> set = new LinkedHashSet();
                    set.addAll(beans);
                    beans.clear();
                    beans.addAll(set);
                    listViewAdapter.notifyDataSetChanged();
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
        // 在这里加载更多数据，或者更具产品需求实现上拉刷新也可以

        if (Utils.isNetworkConnected(getApplicationContext())) {
            // 如果网络可用，则异步加载网络数据，并返回true，显示正在加载更多
            new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... params) {
                    String id = beans.get(beans.size() - 1).getNewsID();
                    beansAdd = XmlUtils.getListBeans(id, Utils.getNewsXmlUrl(id));
                    if (beansAdd == null) {
                        return -1;
                    }
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if (result == -1) {
                        Toast.makeText(MainActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 加载完毕后在UI线程结束加载更多
                    mRefreshLayout.endLoadingMore();
                    beans.addAll(beansAdd);
                    listViewAdapter.notifyDataSetChanged();
                }
            }.execute();
            return true;
        } else {
            // 网络不可用，返回false，不显示正在加载更多
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            return false;
        }
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void ListsetOnItemClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("newsid", beans.get(position).getNewsID());
                beans.get(position).setColor("0xff888888");
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        listViewAdapter.notifyDataSetChanged();
                    }
                }.execute();
                intent.putExtra("bean", beans.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            beginRefreshing();
            return true;
        }
        if (id == R.id.action_about) {
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            if (!Utils.kind.equals("news")) {
                Utils.kind = "news";
                toolbar.setTitle("首页");
                beginRefreshing();
            }
        } else if (id == R.id.nav_android) {
            if (!Utils.kind.equals("android")) {
                Utils.kind = "android";
                beans.clear();
                toolbar.setTitle("安卓");
                beginRefreshing();
            }
        } else if (id == R.id.nav_iphone) {
            if (!Utils.kind.equals("ios")) {
                Utils.kind = "ios";
                beans.clear();
                toolbar.setTitle("iPhone");
                beginRefreshing();
            }
        } else if (id == R.id.nav_windows) {
            if (!Utils.kind.equals("windows")) {
                Utils.kind = "windows";
                beans.clear();
                toolbar.setTitle("Windows");
                beginRefreshing();
            }
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
