package com.watermelon.seimicrwaler.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static List<Map<String, Object>> getArraysFilter(String content, String regex, Integer index) {
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(content);
        List<Map<String, Object>> list = new ArrayList<>();
        int i = 0;
        while (matcher.find()) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("image", matcher.group(index));
            map.put("index", i);
            list.add(map);
            i++;
        }
        return list;
    }

}
