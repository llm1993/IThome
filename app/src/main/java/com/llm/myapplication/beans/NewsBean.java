package com.llm.myapplication.beans;

/**
 * Created by SAMSUNG on 2016/9/25.
 */

import com.llm.myapplication.utils.XmlUtils;

public class NewsBean {

	private String newsID;
	private String title;
	private String url;
	private String postDate;
	private String imgUrl;
	private String description;
	private String hitCount;
	private String commentcount;
	private boolean forbidcomment;
	private String cid;
	private String color = "0xff000000";
	private ContentBean content;

	public NewsBean() {
	}

	public NewsBean(String title, String imgUrl) {
		this.title = title;
		this.imgUrl = imgUrl;
	}

	@Override
	public boolean equals(Object obj) {
		return this.newsID.equals(((NewsBean) obj).newsID);
	}

	@Override
	public int hashCode() {
		return this.newsID.hashCode();
	}

	public String getNewsID() {
		return newsID;
	}

	public void setNewsID(String newsID) {
		this.newsID = newsID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPostDate() {
		return postDate;
	}

	public void setPostDate(String postDate) {
		this.postDate = postDate;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHitCount() {
		return hitCount;
	}

	public void setHitCount(String hitCount) {
		this.hitCount = hitCount;
	}

	public String getCommentcount() {
		return commentcount;
	}

	public void setCommentcount(String commentcount) {
		this.commentcount = commentcount;
	}

	public boolean isForbidcomment() {
		return forbidcomment;
	}

	public void setForbidcomment(boolean forbidcomment) {
		this.forbidcomment = forbidcomment;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public ContentBean getContent() {
		return content;
	}

	public void setContent(ContentBean content) {
		this.content = content;
	}

	public void setContent() {
		if(this.newsID==null||this.getNewsID().length() != 6){
			return;
		}else{
			XmlUtils.setContent(this);
		}
	}

}
