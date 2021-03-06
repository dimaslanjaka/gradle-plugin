package com.dimaslanjaka.gradle.offline

import org.gradle.api.artifacts.dsl.RepositoryHandler

class OfflineDependenciesExtension {

    // deps
    private RepositoryHandler repositoryHandler

    // config properties
    def configurations = [] as Set<String>
    def buildScriptConfigurations = [] as Set<String>

    def includeSources = true
    def includeJavadocs = true
    def includePoms = true
    def includeIvyXmls = true
    def includeBuildscriptDependencies = true

    // init
    OfflineDependenciesExtension(RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler
    }

    // expose 'repositories' closure to build script, just like the default 'repositories' closure
    void repositories(Closure repoConfigurator) {
        def configurator = repoConfigurator.clone()
        configurator.delegate = this.repositoryHandler
        configurator()
    }

    void configurations(String... configurationNames) {
        this.configurations.addAll(configurationNames)
    }

    void buildScriptConfigurations(String... configurationNames) {
        this.buildScriptConfigurations.addAll(configurationNames)
    }

    // used by task depending on the repository definitions
    protected RepositoryHandler getRepositoryHandler() {
        return this.repositoryHandler
    }
}
