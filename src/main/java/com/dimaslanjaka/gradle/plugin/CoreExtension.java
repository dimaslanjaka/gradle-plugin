package com.dimaslanjaka.gradle.plugin;

import com.dimaslanjaka.gradle.offline_dependencies.Extension;
import groovy.lang.MetaClass;
import org.gradle.api.Project;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;

public class CoreExtension extends Extension implements CoreExtensionInterface {
    /**
     * User Home
     */
    private final String home = CoreExtensionInterface.home;
    /**
     * Limit artifacts to cached
     */
    public int limit = CoreExtensionInterface.limit;
    /**
     * Artifact cache extensions to be cached.
     * default {".module", ".jar", ".pom", ".aar", ".sha1", ".xml"}
     */
    public String[] extensions = {".module", ".jar", ".pom", ".aar", ".sha1", ".xml"};
    /**
     * Root offline repository (default mavenLocal)
     */
    public File localRepository = CoreExtensionInterface.localRepository;
    /**
     * Forget expire cache, force repeat every first task executed
     */
    public boolean force = CoreExtensionInterface.force;
    /**
     * Repeat cache when last caching is more than (n) minutes
     */
    public int expire = 60;
    public boolean offline3 = false;

    public CoreExtension(Project p) {
        super(p, p.getRepositories());
        setProject(p);
        repositoryHandler = p.getRepositories();
    }

    public File getRoot() {
        return localRepository;
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
        this.debug = debug;
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
        setRoot(file);
    }

    @Override
    public Object invokeMethod(String s, Object o) {
        return null;
    }

    @Nullable
    @Override
    public Object getProperty(String s) {
        try {
            return getClass().getMethod(s);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setProperty(String fieldName, Object fieldValue) {
        Class<?> clazz = getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(this, fieldValue);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Nullable
    @Override
    public MetaClass getMetaClass() {
        return null;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
    }
}