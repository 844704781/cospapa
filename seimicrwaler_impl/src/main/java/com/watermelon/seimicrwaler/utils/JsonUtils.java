package com.watermelon.seimicrwaler.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by watermelon on 2019/04/06
 */
public class JsonUtils {
    private static final Gson gson = new GsonBuilder().create();

    public static String toJson(Object obj, Class cls) {
        return gson.toJson(obj, cls);
    }

    public static ZimgUtils.Response fromJson(String result, Class<ZimgUtils.Response> responseClass) {
        return gson.fromJson(result, responseClass);
    }
}
