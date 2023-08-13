//file:noinspection unused
//file:noinspection GrUnnecessaryPublicModifier
package com.dimaslanjaka.gradle.offline_dependencies

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

public class Extension implements ExtensionInterface {
    public File root
    private Project project

    // deps
    public RepositoryHandler repositoryHandler;

    // config properties
    public Set<String> configurations = [] as Set<String>
    public Set<String> buildScriptConfigurations = [] as Set<String>

    /**
     * Cache -sources.jar?
     */
    public boolean includeSources = true
    /**
     * Cache -javadoc.jar?
     */
    public boolean includeJavadocs = true
    /**
     * Cache -pom.xml?
     */
    public boolean includePoms = true
    /**
     * Cache ivy xmls?
     */
    public boolean includeIvyXmls = true
    /**
     * Cache build script dependencies?
     */
    public boolean includeBuildscriptDependencies = true
    /**
     * Show debug while processing
     */
    public boolean debug = false

    // init
    Extension(Project project, RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler
        this.project = project
        this.root = new File(project.projectDir, "build/offline-repository").absoluteFile
    }

    public void setProject(Project project1) {
        this.project = project1
    }

    public Project getProject() { return this.project }

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

    private Map<String, Object> mapPrint = new TreeMap<>();

    public Map<String, Object> dump() {
        mapPrint.with {
            put("root-dir", this.root.canonicalPath);
            put("project-name", this.project.name)
            put("project-dir", this.project.projectDir.canonicalPath)
            put("configurations", this.configurations);
            put("configurations-build", this.buildScriptConfigurations);
            put("i-Pom", this.includePoms);
            put("i-Ivy", this.includeIvyXmls);
            put("i-Javadoc", this.includeJavadocs);
            put("i-Sources", this.includeSources);
            put("i-BuildDeps", this.includeBuildscriptDependencies);
        }
        return mapPrint
    }

    @Override
    public String toString() {
        dump()
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
        return gson.toJson(mapPrint)
    }
}

