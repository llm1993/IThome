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

 GET http://api.ithome.com/xml/newslist/news.xml?r=1474978735185 最新列表

 GET http://api.ithome.com/xml/newslist/news_75b9e75b220e9192.xml?r=1474979005762 根据id取出50条

 GET http://api.ithome.com/xml/newscontent/260/899.xml?r=1474978227754  获取文章内容

 GET http://api.ithome.com/json/tags/0260/260899.json 相关文章


 GET http://api.ithome.com/json/hotcommentlist/265/1f850e0010530a8f.json?r=1474978227757 获取热门评论
 GET http://www.ithome.com/json/commentlist/265/1f850e0010530a8f.json?r=1476919608657 获取最新评论
 GET http://www.ithome.com/json/commentlist/1f850e0010530a8f_19654999.json?r=1476919819261 根据上面评论最后id获取评论

 */

public class Utils {
    // 评论所需key
    private final static byte[] commentKey = {-14, -7, -77, -102, -94, -16, -74, -1};
    // 最新列表所需key
    private final static byte[] newslistKey = {-86, -7, -69, -102, -83, -124, -87, -14};
    public static Map map = new HashMap();
    public static int type = 0;

	public static String getNewsXmlUrl() {
		return getNewsXmlUrl(null);
	}

	public static String getNewsXmlUrl(String id) {
		String urlStr = null;
		if (id == null) {
			urlStr = "http://api.ithome.com/xml/newslist/news.xml" + "?r=" + System.currentTimeMillis();
		} else {
			urlStr = "http://api.ithome.com/xml/newslist/news_" + getKeyByID(id, 1) + ".xml?r="
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
			urlStr = "http://www.ithome.com/json/commentlist/" + id.substring(0, 3) + "/" + getKeyByID(id, 2) + "_"
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
            String line;
            sb = new StringBuilder();
            while ((line = bfr.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Pattern pattern = Pattern.compile("title\">.+?<");
        Pattern patternhtml = Pattern.compile("/html/.+?\">");
        Pattern patterndate = Pattern.compile("date\">.+?<");
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
        boolean adflag = false;
        int count = 0;
        while (true) {
            adflag = false;
            adIndex++;
            int counttemp = count;
            if (matcher.find()) {
                count++;
                linetitle = matcher.group();
                if (linetitle.contains("font"))
                    linetitle = linetitle.substring(
                            linetitle.indexOf("'>") + 2,
                            linetitle.lastIndexOf("<"));
                else
                    linetitle = linetitle.substring(linetitle.indexOf(">") + 1,
                            linetitle.indexOf("<"));
                if (linetitle.trim().endsWith("元")
                        || linetitle.contains("科技宅福利") || linetitle.contains("白菜价")) {
                    System.out.println(adIndex + "  " + linetitle);
                    adflag = true;
                } else
                    titleList.add(linetitle);
            }
            if (matcherhtml.find()) {
                count++;
                linehtml = matcherhtml.group();
                linehtml = linehtml.substring(
                        linehtml.lastIndexOf("/") + 1, linehtml.indexOf("."));
                if (!adflag)
                    contentList.add(linehtml);
            }

            if (matcherdate.find()) {
                count++;
                datehtml = matcherdate.group();
                datehtml = datehtml.substring(datehtml.indexOf(">") + 1,
                        datehtml.indexOf("<"));
                if (!adflag)
                    dateList.add(datehtml);
            }
            if (counttemp == count) {
                break;
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
            NewsBean bean = new NewsBean();
            bean.setTitle(title);
            bean.setPostDate(dateList.get(index));
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
