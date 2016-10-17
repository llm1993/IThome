package com.llm.myapplication.beans;

/**
 * Created by SAMSUNG on 2016/9/25.
 */

public class Bean {
    private String title;
    private String imgUrl;
    private String date;
    private int color = 0xff000000;
    public Bean() {
    }
    public Bean(String title, String imgUrl) {
        this.title = title;
        this.imgUrl = imgUrl;
    }

    @Override
    public boolean equals(Object obj) {
        return this.title.equals(((Bean) obj).title);
    }

    @Override
    public int hashCode() {
        return this.title.hashCode();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
