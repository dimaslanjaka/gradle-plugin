package com.dimaslanjaka.gradle.plugin;

import com.dimaslanjaka.kotlin.File;

import java.util.HashMap;
import java.util.Map;

import static com.dimaslanjaka.kotlin.ConsoleColors.println;

@SuppressWarnings("unused")
public class Utils {
    private final static String temp = System.getProperty("java.io.tmpdir");
    private final static File tempDir = new File(temp, "gradle");

    static {
        if (!tempDir.exists())
            if (!tempDir.mkdirs())
                println("cannot create temporarily folder");
    }

    @SuppressWarnings("all")
    public static boolean createTempDir(String name) {
        if (getTempDir(name).exists()) {
            return getTempDir(name).mkdirs();
        }
        return false;
    }

    public static File getTempDir(String name) {
        return getTempDir(name, false);
    }

    @SuppressWarnings("unused")
    public static File getTempDir(String name, boolean autocreate) {
        if (autocreate) createTempDir(name);
        return new File(tempDir, name);
    }

    public static File getTempFile(String filename, boolean autocreate) {
        com.dimaslanjaka.kotlin.File directory = getTempDir();
        return directory.file_in_dir(filename, autocreate);
    }

    private static File getTempDir() {
        return tempDir;
    }

    public static void main(String[] args) {
        System.out.println(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    private static String getMethodName() {
        return getMethodName(0);
    }

    static Map<String, Boolean> isURLs = new HashMap<>();

    @SuppressWarnings("unused")
    public static boolean isURL(String url, boolean cached) {
        if (cached) {
            if (isURLs.containsKey(url)) {
                return isURLs.get(url);
            }
        }

        boolean isURL = verifyUrl(url);
        isURLs.put(url, isURL);
        return isURL;
    }

    /**
     * Get the method name for a depth in call stack. <br>
     * Utility function
     *
     * @param depth depth in the call stack (0 means current method, 1 means call method, ...)
     * @return method name
     */
    public static String getMethodName(final int depth) {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

        //System. out.println(ste[ste.length-depth].getClassName()+"#"+ste[ste.length-depth].getMethodName());
        // return ste[ste.length - depth].getMethodName();  //Wrong, fails for depth = 0
        return ste[ste.length - 1 - depth].getMethodName(); //Thank you Tom Tresansky
    }

    @SuppressWarnings("unused")
    public static boolean isURL(String uri) {
        return isURL(uri, false);
    }

    private static boolean verifyUrl(String url) {
        try {
            (new java.net.URL(url)).openStream().close();
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }
}