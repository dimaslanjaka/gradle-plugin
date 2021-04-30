package com.dimaslanjaka.gradle.offline_dependencies


import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

class OfflineDependenciesExtension {
    /**
     * Root offline repository
     */
    File root
    private Project project
    private GString rootGString

    // deps
    RepositoryHandler repositoryHandler;

    // config properties
    Set<String> configurations = [] as Set<String>
    Set<String> buildScriptConfigurations = [] as Set<String>

    boolean includeSources = true
    boolean includeJavadocs = true
    boolean includePoms = true
    boolean includeIvyXmls = true
    boolean includeBuildscriptDependencies = true

    // init
    OfflineDependenciesExtension(Project project, RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler
        this.project = project
        this.root = new File(project.projectDir, "build/offline-repository").absoluteFile
        this.rootGString = "${this.root}"
    }

    /**
     * Set root from string
     * @param newRoot
     */
    public void setRoot(String newRoot) {
        this.root = new File(newRoot)
    }

    /**
     * Set root from java.io.File
     * @param newRoot
     */
    public void setRoot(File newRoot) {
        this.root = newRoot
    }

    // expose 'repositories' closure to build script, just like the default 'repositories' closure
    public void repositories(Closure repoConfigurator) {
        def configurator = repoConfigurator.clone()
        configurator.delegate = this.repositoryHandler
        configurator()
    }

    public void configurations(String... configurationNames) {
        this.configurations.addAll(configurationNames)
    }

    public void buildScriptConfigurations(String... configurationNames) {
        this.buildScriptConfigurations.addAll(configurationNames)
    }

    // used by task depending on the repository definitions
    protected RepositoryHandler getRepositoryHandler() {
        return this.repositoryHandler
    }

    @SuppressWarnings('unused')
    public Map asMap() {
        this.class.declaredFields.findAll { !it.synthetic }.collectEntries {
            [(it.name): this."$it.name"]
        }
    }
}

