package com.llm.myapplication.utils;

import com.llm.myapplication.beans.ContentBean;
import com.llm.myapplication.beans.NewsBean;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class XmlUtils {

    private static NewsBean beanthis;

    public static List<NewsBean> getListBeans(String startId, String newsXmlUrl) {
        List<NewsBean> list = new ArrayList<>();
        String maxId = Utils.getMaxId();
        if (maxId.equals(startId)) {
            return list;
        }
        if (newsXmlUrl == null || newsXmlUrl.equals("")) {
            return null;
        } else {
            list = parser(newsXmlUrl);
        }
        return list;
    }

    public static void setContent(NewsBean bean) {
        beanthis = bean;
        String url = "http://api.ithome.com/xml/newscontent/" + bean.getNewsID().substring(0, 3) + "/"
                + bean.getNewsID().substring(3) + ".xml?r=" + System.currentTimeMillis();
        parser(url);
    }

    private static List parser(String newsXmlUrl) {
        List list = new ArrayList<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(newsXmlUrl);
            NodeList root = document.getChildNodes();
            Node channel = root.item(0).getChildNodes().item(1);
            NodeList items = channel.getChildNodes();
            for (int j = 0; j < items.getLength(); j++) {
                Node item = items.item(j);
                if (item.hasChildNodes()) {
                    NodeList nodebeans = item.getChildNodes();
                    NewsBean newsbean = new NewsBean();
                    ContentBean contentBean = new ContentBean();
                    for (int index = 0; index < nodebeans.getLength(); index++) {
                        Node nodebean = nodebeans.item(index);
                        if (nodebean.getNodeType() == 1) {
                            String content = nodebean.getTextContent();
                            switch (nodebean.getNodeName()) {
                                case "newsid":
                                    newsbean.setNewsID(content);
                                    break;
                                case "title":
                                    newsbean.setTitle(content);
                                    break;
                                case "c":
                                    if (!content.equals(""))
                                        newsbean.setColor(content);
                                    break;
                                case "url":
                                    newsbean.setUrl(content);
                                    break;
                                case "postdate":
                                    newsbean.setPostDate(content);
                                    break;
                                case "image":
                                    newsbean.setImgUrl(content);
                                    break;
                                case "description":
                                    newsbean.setDescription(content);
                                    break;
                                case "hitcount":
                                    newsbean.setHitCount(content);
                                    break;
                                case "commentcount":
                                    newsbean.setCommentcount(content);
                                    break;
                                case "forbidcomment":
                                    newsbean.setForbidcomment(content.equals("true"));
                                    break;
                                case "cid":
                                    newsbean.setCid(content);
                                    break;
                                case "newssource":
                                    contentBean.setNewssource(content);
                                    break;
                                case "newsauthor":
                                    contentBean.setNewsauthor(content);
                                    break;
                                case "detail":
                                    contentBean.setDetail(content);
                                    break;
                                case "z":
                                    contentBean.setZ(content);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    if (newsbean.getNewsID() != null && !AdCheck.checkHasAd(newsbean)) {
                        list.add(newsbean);
                    }
                    if (contentBean.getDetail() != null) {
                        beanthis.setContent(contentBean);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }
}
