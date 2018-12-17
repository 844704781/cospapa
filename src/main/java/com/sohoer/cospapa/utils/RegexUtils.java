package com.sohoer.cospapa.utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    private static String DATE ="\\b[0-9]{4}-[0-1][0-2]-[0-3][0-9]\\b";
    public static String getDate(String content) {
        Pattern p = Pattern.compile(RegexUtils.DATE);
        Matcher matcher = p.matcher(content);
        while (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static void main(String[] args) {
        getDate("更新于：2018-11-19 <em>1.0</em>");
    }

}
