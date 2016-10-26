package com.llm.myapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.llm.myapplication.beans.NewsBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by SAMSUNG on 2016/9/25.
 * GET http://api.ithome.com/xml/newslist/maxnewsid.xml 获取最新的文章id
 * <p>
 * GET http://api.ithome.com/xml/newslist/news.xml?r=1474978735185 最新列表
 * <p>
 * GET http://api.ithome.com/xml/newslist/news_75b9e75b220e9192.xml?r=1474979005762 根据id取出50条
 * <p>
 * GET http://api.ithome.com/xml/newscontent/260/899.xml?r=1474978227754  获取文章内容
 * <p>
 * GET http://api.ithome.com/json/tags/0260/260899.json 相关文章
 * <p>
 * <p>
 * GET http://api.ithome.com/json/hotcommentlist/265/1f850e0010530a8f.json?r=1474978227757 获取热门评论
 * GET http://www.ithome.com/json/commentlist/265/1f850e0010530a8f.json?r=1476919608657 获取最新评论
 * GET http://www.ithome.com/json/commentlist/1f850e0010530a8f_19654999.json?r=1476919819261 根据上面评论最后id获取评论
 */

public class Utils {
    // 评论所需key
    private final static byte[] commentKey = {-14, -7, -77, -102, -94, -16, -74, -1};
    // 最新列表所需key
    private final static byte[] newslistKey = {-86, -7, -69, -102, -83, -124, -87, -14};
    public static Map map = new HashMap();
    public static int type = 0;
    public static String kind = "news";

    public static String getNewsXmlUrl() {
        return getNewsXmlUrl(null);
    }

    public static String getNewsXmlUrl(String id) {
        String urlStr = null;
        if (id == null) {
            urlStr = "http://api.ithome.com/xml/newslist/"+kind+".xml" + "?r=" + System.currentTimeMillis();
        } else {
            urlStr = "http://api.ithome.com/xml/newslist/"+kind+"_" + getKeyByID(id, 1) + ".xml?r="
                    + System.currentTimeMillis();
        }
        return urlStr;
    }

    public static String getCommentJsonUrl(String id) {
        return getCommentJsonUrl(id, null);
    }

    public static String getCommentJsonUrl(String id, String floorID) {
        String urlStr = null;
        if (floorID == null) {
            urlStr = "http://www.ithome.com/json/commentlist/" + id.substring(0, 3) + "/" + getKeyByID(id, 2)
                    + ".json?r=" + System.currentTimeMillis();
        } else {
            urlStr = "http://www.ithome.com/json/commentlist/" + getKeyByID(id, 2) + "_"
                    + floorID + ".json?r=" + System.currentTimeMillis();
        }
        return urlStr;
    }

    public static String getCommentJsonList(String id) {
        String urlStr = null;
        if (id == null) {
            urlStr = "http://api.ithome.com/xml/newslist/news.xml" + "?r=" + System.currentTimeMillis();
        } else {
            urlStr = "http://api.ithome.com/xml/newslist/news" + getKeyByID(id, 2) + ".xml?r=" + System.currentTimeMillis();
        }
        return urlStr;
    }

    public static String getKeyByID(String id, int keyType) {
        String newsID = id + "\u0000\u0000";
        byte[] key = null;
        if (keyType == 1)
            key = newslistKey;
        if (keyType == 2)
            key = commentKey;
        byte[] arrayOfByte = new byte[key.length];
        int i = 0;
        while (i < key.length) {
            arrayOfByte[i] = (byte) (-38 ^ key[i]);
            i += 1;
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(arrayOfByte, "DES");
        StringBuffer sb = null;
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            cipher.init(1, secretKeySpec);
            byte[] paramArrayOfByte = cipher.doFinal(newsID.getBytes());
            sb = new StringBuffer();
            i = 0;
            while (i < paramArrayOfByte.length) {

                String str = Integer.toHexString(paramArrayOfByte[i] & 0xFF);
                if (str.length() == 1)
                    sb.append("0" + str);
                else
                    sb.append(str);
                i += 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static String getMaxId() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("http://api.ithome.com/xml/newslist/maxnewsid.xml");
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url
                    .openConnection();
            httpUrlConnection.setRequestMethod("GET");
            BufferedReader bfr = new BufferedReader(new InputStreamReader(
                    httpUrlConnection.getInputStream(), "utf-8"));
            String line;
            while ((line = bfr.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString().trim();
    }
}
