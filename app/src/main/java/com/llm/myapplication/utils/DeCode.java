package com.llm.myapplication.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SAMSUNG on 2016/10/23.
 */

public class DeCode {
    public static String docode(String src) {
        Matcher m = Pattern.compile("%u([0-9a-fA-F]{4})").matcher(src);
        while (m.find()) {
            String temp = m.group();
            src = src.replace(temp, "" + (char) Integer.parseInt(temp.substring(2), 16));
        }
        m = Pattern.compile("%([0-9a-fA-F]{2})").matcher(src);
        while (m.find()) {
            String temp = m.group();
            src = src.replace(temp, "" + (char) Integer.parseInt(temp.substring(2), 16));
        }
        return src;
    }
}
