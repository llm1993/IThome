package com.llm.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.llm.myapplication.R;
import com.llm.myapplication.beans.NewsBean;
import com.llm.myapplication.view.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by SAMSUNG on 2016/10/16.
 */

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<NewsBean> beans = new ArrayList<NewsBean>();
    private Context context;

    public ListViewAdapter(ArrayList<NewsBean> beans, Context context) {
        this.beans = beans;
        this.context = context;
    }

    public int getCount() {
        return beans.size();
    }

    public Object getItem(int position) {
        return beans.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            /*View复用*/
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list, null);
            holder = new ViewHolder();
            // 通过findViewById()方法实例R.layout.item_list内各组件
            holder.text = (TextView) convertView.findViewById(R.id.title);
            holder.image = (ImageView) convertView.findViewById(R.id.listimg);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.comment = (TextView) convertView.findViewById(R.id.comment);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        // 给holder中的控件进行赋值
        NewsBean bean = beans.get(position);
        holder.text.setText(bean.getTitle());
        holder.date.setText(bean.getPostDate());
        holder.comment.setText("回复: "+bean.getCommentcount());
        if (bean.getColor().equals("")) {
            holder.text.setTextColor(Integer.parseInt(bean.getColor().replace("#", "")));
        }
        holder.image.setImageResource(R.drawable.c);
        Picasso.with(context)
                .load(bean.getImgUrl())
                .into(holder.image);
        return convertView;
    }
}
