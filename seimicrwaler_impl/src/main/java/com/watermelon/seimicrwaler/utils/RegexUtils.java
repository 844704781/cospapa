package com.watermelon.seimicrwaler.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by watermelon on 2019/04/05
 */
public class RegexUtils {

    public static String filter(String content, String regex, Integer groupIndex) {
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(content);
        while (matcher.find()) {
            return matcher.group(groupIndex);
        }
        return null;
    }

    public static List<String> getArraysFilter(String content, String regex , Integer index) {
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(content);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group(index));
        }
        return list;
    }


}
