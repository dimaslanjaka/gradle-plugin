package com.dimaslanjaka.gradle.plugin;

import org.gradle.api.Project;

import java.io.File;

public class CoreExtension implements CoreExtensionInterface {
    /**
     * Debug while processing
     */
    public static boolean debug = false;
    private final Project project;
    /**
     * User Home
     */
    public String home = CoreExtensionInterface.home;
    /**
     * Limit artifacts to cached
     */
    public int limit = CoreExtensionInterface.limit;
    /**
     * Artifact cache extensions to be cached.
     * default {".module", ".jar", ".pom", ".aar", ".sha1", ".xml"}
     */
    public String[] extensions = {".module", ".jar", ".pom", ".aar", ".sha1", ".xml"};
    public File localRepository = CoreExtensionInterface.localRepository;
    /**
     * Forget expire cache, force repeat every first task executed
     */
    public boolean force = CoreExtensionInterface.force;
    /**
     * Repeat cache when last caching is more than (n) minutes
     */
    public int expire = 60;

    public CoreExtension(Project p) {
        project = p;
    }

    public Project getProject() {
        return project;
    }

    @Override
    public boolean validExtension(String s) {
        for (String entry : extensions) {
            if (s.endsWith(entry)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getHome() {
        return home;
    }

    @Override
    public boolean getDebug() {
        return debug;
    }

    @Override
    public void setDebug(boolean debug) {
        CoreExtension.debug = debug;
    }

    @Override
    public int getLimit() {
        return limit;
    }

    @Override
    public void setLimit(int i) {
        limit = i;
    }

    @Override
    public File getLocalRepository() {
        return localRepository;
    }

    @Override
    public void setLocalRepository(File file) {
        localRepository = file;
    }
}