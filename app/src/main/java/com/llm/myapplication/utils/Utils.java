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
        Pattern pattern = Pattern.compile("title\">.{0,100}</span></a>");
        Pattern patternhtml = Pattern.compile("/html/.{1,25}\">");
        Pattern patterndate = Pattern.compile("date\">.{0,20}</span><span");
        Matcher matcher = pattern.matcher(sb);
        Matcher matcherhtml = patternhtml.matcher(sb);
        Matcher matcherdate = patterndate.matcher(sb);
        String linetitle;
        String linehtml;
        String datehtml;
        List<String> titleList = new ArrayList<>();
        List<String> contentList = new ArrayList<>();
        List<String> dateList = new ArrayList<>();
        int adIndex = 0;
        boolean isMatchAll = false;
        boolean adflag = false;
        int count = 0;
        while (!isMatchAll) {
            adflag = false;
            adIndex++;
            int counttemp = count;
            if (matcher.find()) {
                count++;
                linetitle = matcher.group();
                if (linetitle.contains("font"))
                    linetitle = linetitle.substring(
                            linetitle.indexOf("'>") + 2,
                            linetitle.indexOf("</"));
                else
                    linetitle = linetitle.substring(linetitle.indexOf(">") + 1,
                            linetitle.indexOf("</"));
                if (linetitle.trim().endsWith("元")
                        || linetitle.contains("科技宅福利")) {
                    System.out.print(adIndex + "  " + linetitle);
                    adflag = true;
                } else
                    titleList.add(linetitle);
            }
            if (matcherhtml.find()) {
                count++;
                linehtml = matcherhtml.group();
//                System.out.println(linehtml);
                linehtml = linehtml.substring(
                        linehtml.lastIndexOf("/") + 1, linehtml.indexOf("."));
                if (adflag) {
                    System.out.print(linehtml);
                } else
                    contentList.add(linehtml);
            }

            if (matcherdate.find()) {
                count++;
                datehtml = matcherdate.group();
                datehtml = datehtml.substring(datehtml.indexOf(">") + 1,
                        datehtml.indexOf("<"));
                if (adflag) {
                    System.out.println("  " + datehtml);
                } else
                    dateList.add(datehtml);
            }
            if (counttemp == count) {
                isMatchAll = true;
            }
        }
        if (titleList.size() != contentList.size()
                || titleList.size() != dateList.size()
                || contentList.size() != dateList.size()) {
            System.out.println(page);
            System.out.println(sb);
            System.out.println(titleList.size() + "!=" + contentList.size()
                    + "   " + dateList.size());
        }
        Date date = new Date();
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy/MM");
        for (int index = 0; index < titleList.size(); index++) {
            String title = titleList.get(index);
            Bean bean = new Bean();
            bean.setTitle(title);
            bean.setDate(dateList.get(index));
            String url = "http://img.ithome.com/newsuploadfiles/thumbnail/" + sFormat.format(date) + "/" + contentList.get(index) + ".jpg";
//            System.out.println(url);
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
