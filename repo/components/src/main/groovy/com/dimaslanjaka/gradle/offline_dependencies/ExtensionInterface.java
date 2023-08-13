//file:noinspection unused
package com.dimaslanjaka.gradle.offline_dependencies;

import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public interface ExtensionInterface {
    public Project project = null;
    public File root = null;
    public RepositoryHandler repositoryHandler = null;
    public Set<String> configurations = new HashSet<String>();
    public Set<String> buildScriptConfigurations = new HashSet<String>();
    public boolean includeSources = true;
    public boolean includeJavadocs = true;
    public boolean includePoms = true;
    public boolean includeIvyXmls = true;
    public boolean includeBuildscriptDependencies = true;
}