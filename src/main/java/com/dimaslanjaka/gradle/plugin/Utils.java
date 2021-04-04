package com.dimaslanjaka.gradle.plugin;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.IOException;

@SuppressWarnings("unused")
public class Utils {
    private final static String temp = System.getProperty("java.io.tmpdir");
    private final static File tempDir = new File(temp, "gradle");
    static UrlValidator urlValidator = new UrlValidator();

    static {
        if (!tempDir.exists())
            if (!tempDir.mkdirs())
                println("cannot create temporarily folder");
    }

    public static boolean webUrlValid(String url) {
        urlValidator = new UrlValidator(new String[]{"http", "https"});
        return urlValidator.isValid(url);
    }

    public static boolean ftpUrlValid(String url) {
        urlValidator = new UrlValidator(new String[]{"ftp"});
        return urlValidator.isValid(url);
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
        File directory = getTempDir();
        return directory.file_in_dir(filename, autocreate);
    }

    private static File getTempDir() {
        return tempDir;
    }

    @SuppressWarnings("all")
    public static void println(Object str) {
        com.dimaslanjaka.kotlin.Helper.println(str);
    }

    public static void main(String[] args) {
        System.out.println(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    private static String getMethodName() {
        return getMethodName(0);
    }

    @SuppressWarnings("unused")
    public static boolean isURL(String url, boolean cached) {
        if (cached) {
            String name = MD5.get(getMethodName());
            com.dimaslanjaka.gradle.plugin.File tmp = new com.dimaslanjaka.gradle.plugin.File(getTempDir(name, true), "url-cached.properties");
            if (!tmp.getParentFile().exists()) {
                if (!tmp.getParentFile().mkdirs()) {
                    println("cannot create parent file of " + tmp);
                }
            } else if (tmp.getParentFile().isFile()) {
                if (tmp.getParentFile().delete()) {
                    if (!tmp.getParentFile().mkdirs()) {
                        println("cannot create parent file of " + tmp);
                    }
                }
            }
            if (!tmp.exists()) {
                try {
                    if (!tmp.createNewFile()) {
                        println("cannot create file " + tmp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            final de.poiu.apron.PropertyFile propertyFile = de.poiu.apron.PropertyFile.from(tmp);
            final String cachedUrl = propertyFile.get(url);
            if (cachedUrl != null) {
                if (cachedUrl.equals("true")) {
                    return true;
                }
            }
            boolean verify = verifyUrl(url);
            if (verify) {
                propertyFile.set(url, "true");
            } else {
                propertyFile.set(url, "false");
            }
            propertyFile.update(tmp);
            return verify;
        }
        return verifyUrl(url);
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

    public static void println(Object... o) {
        com.dimaslanjaka.kotlin.Helper.println(o);
    }
}