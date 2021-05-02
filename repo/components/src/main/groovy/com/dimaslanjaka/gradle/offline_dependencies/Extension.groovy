//file:noinspection unused
//file:noinspection GrUnnecessaryPublicModifier
package com.dimaslanjaka.gradle.offline_dependencies


import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

class Extension implements ExtensionInterface {
    File root
    private Project project

    // deps
    RepositoryHandler repositoryHandler;

    // config properties
    Set<String> configurations = [] as Set<String>
    Set<String> buildScriptConfigurations = [] as Set<String>

    /**
     * Cache -sources.jar?
     */
    boolean includeSources = true
    /**
     * Cache -javadoc.jar?
     */
    boolean includeJavadocs = true
    /**
     * Cache -pom.xml?
     */
    boolean includePoms = true
    /**
     * Cache ivy xmls?
     */
    boolean includeIvyXmls = true
    /**
     * Cache build script dependencies?
     */
    boolean includeBuildscriptDependencies = true
    /**
     * Show debug while processing
     */
    boolean debug = false

    // init
    Extension(Project project, RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler
        this.project = project
        this.root = new File(project.projectDir, "build/offline-repository").absoluteFile
    }

    void setProject(Project project1) {
        this.project = project1
    }

    public Project getProject() { return this.project }

    /**
     * Set root from string
     * @param newRoot
     */
    void setRoot(String newRoot) {
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

    public void addConfigurations(String string) {
        this.configurations.add(string)
    }

    public void buildScriptConfigurations(String... configurationNames) {
        this.buildScriptConfigurations.addAll(configurationNames)
    }

    // used by task depending on the repository definitions
    protected RepositoryHandler getRepositoryHandler() {
        return this.repositoryHandler
    }

    public void setRepositoryHandler(RepositoryHandler RH) {
        this.repositoryHandler = RH
    }

    /**
     * View configured extension as map
     * @return
     */
    @SuppressWarnings('unused')
    public Map asMap() {
        this.class.declaredFields.findAll { !it.synthetic }.collectEntries {
            [(it.name): this."$it.name"]
        }
    }
}

