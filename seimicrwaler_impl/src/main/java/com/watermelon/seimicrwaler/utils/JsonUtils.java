package com.watermelon.seimicrwaler.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * Created by watermelon on 2019/04/06
 */
public class JsonUtils {
    private static final Gson gson = new GsonBuilder().create();

    public static String toJson(Object obj, Class cls) {
        return gson.toJson(obj, cls);
    }

    public static <T> T fromJson(String json, Class<T> beanClass) {
        return gson.fromJson(json, beanClass);
    }

    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

}
