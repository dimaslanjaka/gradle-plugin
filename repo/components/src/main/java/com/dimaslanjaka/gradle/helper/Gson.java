package com.dimaslanjaka.gradle.helper;

import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class Gson {
    public static String toJson(Object obj) {
        return new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(obj);
    }

    public static <T> Object fromJson(String json, Type type) {
        return new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().fromJson(json, type);
    }

    public static <T> Object fromJson(String json, Class<T> typeClass) {
        return new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().fromJson(json, typeClass);
    }
}
