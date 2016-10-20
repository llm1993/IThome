package com.llm.beans;

import java.util.List;

public class Comment {

	private CommentData M;
	private String H;
	private String SF;
	private String F;
	private String Hfc;
	private List<CommentData> R;
	public CommentData getM() {
		return M;
	}
	public void setM(CommentData m) {
		M = m;
	}
	public String isH() {
		return H;
	}
	public void setH(String h) {
		H = h;
	}
	public String getSF() {
		return SF;
	}
	public void setSF(String sF) {
		SF = sF;
	}
	public String getF() {
		return F;
	}
	public void setF(String f) {
		F = f;
	}
	public String getHfc() {
		return Hfc;
	}
	public void setHfc(String hfc) {
		Hfc = hfc;
	}
	public List<CommentData> getR() {
		return R;
	}
	public void setR(List<CommentData> r) {
		R = r;
	}

}
