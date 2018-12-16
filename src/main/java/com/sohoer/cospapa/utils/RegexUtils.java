package com.sohoer.cospapa.utils;

import ch.qos.logback.core.util.DatePatternToRegexUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    private static String DATE = "((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))?";

    public static void getDate(String content) {
        Pattern p = Pattern.compile(RegexUtils.DATE);
        Matcher matcher = p.matcher(content);
        while (matcher.find()) {
            String group=matcher.group();
            System.out.println(group);
        }
    }

    public static void main(String[] args) {
        getDate("更新于：2018-11-19 <em>1.0</em>");
    }

}
