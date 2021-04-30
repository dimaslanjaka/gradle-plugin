package com.dimaslanjaka.gradle.plugin;

import com.dimaslanjaka.kotlin.File;
import org.gradle.api.Project;
import org.gradle.api.invocation.Gradle;

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

    public static void main(String[] args) {
        cleanGradleDaemonLog(new File("build/test-results/Utils").createGradleProject());
    }

    public static void cleanGradleDaemonLog(Project target) {
        // TODO: clear gradle big log files
        Gradle gradle = target.getGradle();
        String gradleVersion = gradle.getGradleVersion();
        String gradleHome = gradle.getGradleUserHomeDir().getAbsolutePath();
        File daemon = new File(gradleHome, "/daemon");
        java.io.File[] daemonFolders = daemon.listFiles();
        for (java.io.File cf : daemonFolders) {
            java.io.File[] cacheFiles = cf.listFiles();
            assert cacheFiles != null;
            for (java.io.File cx : cacheFiles) {
                if (cx.getName().endsWith(".log")) { // .out.log
                    if (cx.delete()) {
                        if (gradleVersion != cf.getName()) {
                            println(cx + " deleted");
                        } else {
                            println("[current_project] " + cx + " deleted");
                        }
                    }
                }
            }
        }
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