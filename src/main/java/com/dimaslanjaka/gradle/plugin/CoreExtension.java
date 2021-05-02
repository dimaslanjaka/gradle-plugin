package com.dimaslanjaka.gradle.plugin;

import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class CoreExtension implements CoreExtensionInterface /*, com.dimaslanjaka.gradle.offline_dependencies.ExtensionInterface */ {
    /**
     * Debug while processing
     */
    public static boolean debug = false;
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
    /**
     * Root oflline repository (default mavenLocal)
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
    RepositoryHandler repositoryHandler;
    /**
     * Dependency configuration names
     * implementation, compile, etc
     */
    Set<String> configurations = new HashSet<>();
    /**
     * project runtime configurations
     * classpath, etc
     */
    Set<String> buildScriptConfigurations = new HashSet<>();
    boolean includeSources = true;
    boolean includeJavadocs = true;
    boolean includePoms = true;
    boolean includeIvyXmls = true;
    boolean includeBuildscriptDependencies = true;
    @SuppressWarnings("FieldMayBeFinal")
    private Project project;

    public CoreExtension(Project p) {
        project = p;
        repositoryHandler = p.getRepositories();
    }

    public File getRoot() {
        return localRepository;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project p) {
        this.project = p;
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