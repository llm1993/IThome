package com.llm.myapplication.utils;

import com.llm.myapplication.beans.ContentBean;
import com.llm.myapplication.beans.NewsBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SAMSUNG on 2016/9/27.
 */

public class GetContent {
    public static int width = 320;

    public static String getHtml(String title, String strUrl) {
        String str = new StringBuilder(strUrl).insert(3, "/").append(".xml").toString();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("http://api.ithome.com/xml/newscontent/" + str);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url
                    .openConnection();
            httpUrlConnection.setRequestMethod("GET");
            BufferedReader bfr = new BufferedReader(new InputStreamReader(
                    httpUrlConnection.getInputStream(), "utf-8"));
            String line = null;
            while ((line = bfr.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String newssource = sb.substring(sb.indexOf("<newssource>") + 12, sb.indexOf("</newssource>"));
        String newsauthor = sb.substring(sb.indexOf("<newsauthor>") + 12, sb.indexOf("</newsauthor>"));
        String z = sb.substring(sb.indexOf("<z>") + 3, sb.indexOf("</z>"));
        String string = sb.substring(sb.indexOf("<detail>") + 8, sb.indexOf("</detail>")).replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("amp;", "");
        return "<html><head><style>body{font-size:13pt;margin:13px;line-height:25px;letter-spacing:1px;}img{clear: both;\n" +
                " display: block;\n" +
                " margin:auto;max-width:" + (width - 26) + "px !important;}</style></head><body><div><h3>" + title + "</h3><span style=\"font-size:8pt;\">来源：" + newssource + "&nbsp;&nbsp;&nbsp;作者：" + newsauthor + "&nbsp;&nbsp;&nbsp;责编：" + z + "</span><br><HR style=\"border:1 dashed #987cb9\" width=\"100%\"></div>" + string + "</body></html>";
    }

    public static String get(NewsBean newsBean) {
        ContentBean contentBean = newsBean.getContent();
        String newssource = contentBean.getNewssource();
        String newsauthor = contentBean.getNewsauthor();
        String z = contentBean.getZ();
        String string = contentBean.getDetail();
        String title = newsBean.getTitle();
        return "<html><head><style>body{font-size:13pt;margin:13px;line-height:25px;letter-spacing:1px;}img{clear: both;\n" +
                " display: block;\n" +
                " margin:auto;max-width:" + (width - 26) + "px !important;}</style></head><body><div><h3>" + title + "</h3><span style=\"font-size:8pt;\">来源：" + newssource + "&nbsp;&nbsp;&nbsp;作者：" + newsauthor + "&nbsp;&nbsp;&nbsp;责编：" + z + "</span><br><HR style=\"border:1 dashed #987cb9\" width=\"100%\"></div>" + string + "</body></html>";
    }
}
