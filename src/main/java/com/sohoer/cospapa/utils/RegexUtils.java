package com.sohoer.cospapa.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    private static String DATE = "\\b[0-9]{4}-[0-1][0-2]-[0-3][0-9]\\b";
    private static String NAME = "\\/([a-z]*)\\/$";

    /**
     * 根据正则regex去字符串content中过滤
     * @param content
     * @param regex
     * @param groupIndex
     * @return
     */
    private static String filter(String content, String regex ,Integer groupIndex) {
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(content);
        while (matcher.find()) {
            return matcher.group(groupIndex);
        }
        return null;
    }

    private static String filter(String content,String regex)
    {
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(content);
        while (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * 获取更新时间
     * @param content
     * @return
     */
    public static String getDate(String content) {
        return filter(content, RegexUtils.DATE);
    }

    /**
     * 获取书的英文名称
     * @param content
     * @return
     */
    public static String getBookName(String content){
        return filter(content, RegexUtils.NAME,1);
    }



    public static void main(String[] args) {
        System.out.println(getDate("更新于：2018-11-19 <em>1.0</em>"));
        String str="http://www.gufengmh.com/manhua/aojiaowangyetainanzhui/";
        System.out.println(getBookName(str));;
    }

}
