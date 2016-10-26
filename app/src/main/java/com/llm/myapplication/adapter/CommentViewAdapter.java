package com.llm.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.llm.myapplication.R;
import com.llm.myapplication.beans.Comment;
import com.llm.myapplication.utils.DeCode;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by SAMSUNG on 2016/10/23.
 */

public class CommentViewAdapter extends BaseAdapter {
    private ArrayList<Comment> beans = new ArrayList<Comment>();
    private Context context;

    public CommentViewAdapter(ArrayList<Comment> beans, Context context) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            /*View复用*/
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.comment_item, null);
            holder = new CommentViewHolder();
            // 通过findViewById()方法实例R.layout.item_list内各组件
            holder.image = (ImageView) convertView.findViewById(R.id.userimg);
            holder.floor = (TextView) convertView.findViewById(R.id.floor);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.date = (TextView) convertView.findViewById(R.id.comment_date);
            holder.comment = (TextView) convertView.findViewById(R.id.comment_content);
            convertView.setTag(holder);
        }
        holder = (CommentViewHolder) convertView.getTag();
        // 给holder中的控件进行赋值
        Comment comment = beans.get(position);
        holder.username.setText(DeCode.docode(comment.getM().getN()));
        holder.floor.setText(comment.getM().getSF());

        holder.comment.setText(DeCode.docode(comment.getM().getC()));
        if(comment.getH().equals("true"))
            holder.comment.setTextColor(0xffff0000);
        else
            holder.comment.setTextColor(0xff000000);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
        String datestr = comment.getM().getT();
        Timestamp ts = new Timestamp(Long.parseLong(datestr.substring(datestr.indexOf("(") + 1, datestr.indexOf(")"))));
        holder.date.setText(DeCode.docode(comment.getM().getY())+" "+df.format(ts));
        String userid = comment.getM().getUi();
        String imgurl = null;
        if (userid.length() == 5)
            imgurl = "http://avatar.ithome.com/avatars/000/0" + userid.substring(0, 1) + "/" + userid.substring(1, 3) + "/" + userid.substring(3, 5);
        if (userid.length() == 6)
            imgurl = "http://avatar.ithome.com/avatars/000/" + userid.substring(0, 2) + "/" + userid.substring(2, 4) + "/" + userid.substring(4, 6);
        if (userid.length() == 7)
            imgurl = "http://avatar.ithome.com/avatars/00"+userid.substring(0, 1)+"/" + userid.substring(1, 3) + "/" + userid.substring(3, 5) + "/" + userid.substring(5, 7);
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1)
                .cornerRadiusDp(30)
                .oval(false)
                .build();

        Picasso.with(context)
                .load(imgurl+"_60.jpg")
                .fit()
                .transform(transformation)
                .into(holder.image);
        //holder.image.setImageResource(R.drawable.c);
        return convertView;
    }
}
