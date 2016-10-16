package com.llm.myapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.llm.myapplication.beans.Bean;

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

/**
 * Created by SAMSUNG on 2016/9/25.
 */

public class Utils {
    public static Map map = new HashMap();
    public static int type = 0;

    public static ArrayList getData(int page) {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = null;
        try {
            String urlStr = "http://wap.ithome.com/ithome/getajaxdata.aspx?page=" + page + "&type=wapcategorypage";
            switch (type) {
                case 0:
                    break;
                case 10:
                    urlStr += "&categoryid=10";
                    break;
                case 5:
                    urlStr += "&categoryid=5";
                    break;
                case 18:
                    urlStr += "&categoryid=10";
                    break;
                default:
                    break;
            }
            URL url = new URL(urlStr);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url
                    .openConnection();
            httpUrlConnection.setRequestMethod("GET");
            BufferedReader bfr = new BufferedReader(new InputStreamReader(
                    httpUrlConnection.getInputStream(), "utf-8"));
            String line = null;
            sb = new StringBuilder();
            while ((line = bfr.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Pattern pattern = Pattern.compile("title\">.{10,120}</span></a>");
        Pattern patternhtml = Pattern.compile("<a href=\"/.{15,20}\">");
        Matcher matcher = pattern.matcher(sb);
        Matcher matcherhtml = patternhtml.matcher(sb);
        String linetitle = null;
        String linehtml = null;
        List<String> titleList = new ArrayList<String>();
        List<String> contentList = new ArrayList<String>();
        int[] adArr = new int[30];
        int adIndex = 0;
        while (matcher.find()) {
            adIndex++;
            linetitle = matcher.group();
            if (linetitle.contains("font"))
                linetitle = linetitle.substring(linetitle.indexOf("'>") + 2,
                        linetitle.indexOf("</"));
            else
                linetitle = linetitle.substring(linetitle.indexOf(">") + 1,
                        linetitle.indexOf("</"));
            if (linetitle.trim().endsWith("元") || linetitle.contains("科技宅福利") || linetitle.contains("白菜价")) {
                adArr[adIndex] = 1;
                System.out.println(linetitle);
                continue;
            }
            titleList.add(linetitle);
        }
        adIndex = 0;
        while (matcherhtml.find()) {
            adIndex++;
            linehtml = matcherhtml.group();
            linehtml = linehtml.substring(linehtml.indexOf("\"") + 1,
                    linehtml.indexOf("\">"));
            if (adArr[adIndex] == 1) {
                System.out.println(adIndex);
                System.out.println(linehtml.substring(linehtml.indexOf("/") + 6, linehtml.indexOf(".")));
                continue;
            }
            contentList.add(linehtml.substring(linehtml.indexOf("/") + 6, linehtml.indexOf(".")));
        }
        Date date = new Date();
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy/MM");
        for (int index = 0; index < titleList.size(); index++) {
            String title = titleList.get(index);
            Bean bean = new Bean();
            bean.setTitle(title);
            String url = "http://img.ithome.com/newsuploadfiles/thumbnail/" + sFormat.format(date) + "/" + contentList.get(index) + ".jpg";
            System.out.println(url);
            bean.setImgUrl(url);
            arrayList.add(bean);
            map.put(title, contentList.get(index));
        }
        return arrayList;
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
}
