package com.llm.myapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.llm.beans.Comment;

public class JsonUtils {
	public static List<Comment> getCommentList(String commentJsonUrl){
		StringBuilder sb = new StringBuilder();
		List<Comment> list = null;
		try {
			URL url = new URL(commentJsonUrl);
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setRequestMethod("GET");
			BufferedReader bfr = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream(), "utf-8"));
			String line;
			sb = new StringBuilder();
			while ((line = bfr.readLine()) != null) {
				sb.append(line);
			}
			System.out.println(sb);
			sb.delete(0, sb.indexOf("["));
			sb.delete(sb.length()-1,sb.length());
			Gson gson = new Gson();
//			list = gson.fromJson(sb.toString(), new TypeToken<List<Comment>>() {}.getType());
			list = JSON.parseArray(sb.toString(), Comment.class);
			System.out.println(list.size());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	public static void main(String[] args) throws Exception{
		StringBuilder sb = new StringBuilder();
		BufferedReader bfr = new BufferedReader(new InputStreamReader(JsonUtils.class.getResourceAsStream("1.txt")));
		String line;
		sb = new StringBuilder();
		while ((line = bfr.readLine()) != null) {
			sb.append(line);
		}
		System.out.println(sb);
//		List<Comment> json = JSON.parseArray(sb.toString(), Comment.class);
		Gson gson = new Gson();
		List<Comment> json = gson.fromJson(sb.toString(), new TypeToken<List<Comment>>() {}.getType());
		System.out.println(json.size());
		System.out.println(json.get(0).getM().getCi());
	}
}
