package com.dimaslanjaka.gradle.plugin;

import java.io.*;
import java.*;

class Utils {
    @SuppresWarnings("unused")
    public static boolean isURL(String url, boolean cached) {
        try {
            (new java.net.URL(url)).openStream().close();
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    @SuppresWarnings("unused")
    public static boolean isURL(String url) {
        return isURL(uri, false);
    }
}