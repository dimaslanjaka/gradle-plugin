//file:noinspection unused
package com.dimaslanjaka.gradle.offline_dependencies

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

interface ExtensionInterface {
    Project project = null;
    File root = null;
    RepositoryHandler repositoryHandler = null;
    Set<String> configurations = [] as Set<String>;
    Set<String> buildScriptConfigurations = [] as Set<String>;
    boolean includeSources = true;
    boolean includeJavadocs = true;
    boolean includePoms = true;
    boolean includeIvyXmls = true;
    boolean includeBuildscriptDependencies = true;
}