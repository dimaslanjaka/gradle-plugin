package com.dimaslanjaka.gradle.offline


import com.dimaslanjaka.gradle.Utils.Properties
import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler
import org.gradle.internal.reflect.Instantiator

import static com.dimaslanjaka.gradle.Utils.ConsoleColors.*

class OfflineDependenciesPlugin {

    static final String EXTENSION_NAME = 'offlineDependencies'
    protected Project project
    protected boolean firstRun = false
    protected boolean debug = false
    private static String[] RELEASE_TASKS = ["assembleRelease", "bundleRelease", "grabverRelease"]
    private static String[] SAVE_TASKS = ["build", "assembleDebug", "assembleRelease", "bundleDebug", "bundleRelease", "grabverRelease", "jar", "war", "explodedWar"]
    private List<String> runTasks
    def homePath = new File(System.properties['user.home'].toString())
    def reposDir = new File(homePath, '.m2/repository')

    void apply(Project project) {
        this.project = project
        this.runTasks = project.gradle.startParameter.taskNames

        def userHome = new File(System.properties['user.home'].toString())
        def mavenDir = new File(userHome, '.m2/repository')
        if (!mavenDir.exists()) mavenDir.mkdirs()

        if (!project.hasProperty("offlineRepositoryRoot")) {
            project.ext.offlineRepositoryRoot = "${reposDir.getAbsolutePath()}/.m2/repository"
        }

        RepositoryHandler repositoryHandler = new DefaultRepositoryHandler(
                project.services.get(BaseRepositoryFactory.class) as BaseRepositoryFactory,
                project.services.get(Instantiator.class) as Instantiator,
                project.services.get(CollectionCallbackActionDecorator.class) as CollectionCallbackActionDecorator
        )

        def extension = project.extensions.create(EXTENSION_NAME, OfflineDependenciesExtension, repositoryHandler)

        println("Offline dependencies root configured at '${styler(CYAN_BOLD_BRIGHT, project.ext.offlineRepositoryRoot as String)}'")

        project.task('updateOfflineRepository', type: UpdateOfflineRepositoryTask) {
            conventionMapping.root = { "${project.offlineRepositoryRoot}" }
            conventionMapping.configurationNames = { extension.configurations }
            conventionMapping.buildscriptConfigurationNames = { extension.buildScriptConfigurations }
            conventionMapping.includeSources = { extension.includeSources }
            conventionMapping.includeJavadocs = { extension.includeJavadocs }
            conventionMapping.includePoms = { extension.includePoms }
            conventionMapping.includeIvyXmls = { extension.includeIvyXmls }
            conventionMapping.includeBuildscriptDependencies = { extension.includeBuildscriptDependencies }
        }

        project.gradle.buildFinished() { BuildResult result ->
            def CopyJarStart = new GradleCacheToMavenRepo(project)
            gradlePropertiesFix()
        }
    }

    void gradlePropertiesFix() {
        // TODO: add caching ability to gradle.properties
        def writeAbilityTest = new File("${project.buildDir.absolutePath}/tmp/writePropertiesAbility.properties")
        boolean writePropertiesAbility = writeAbilityTest.exists()
        if (!writePropertiesAbility) {
            writeAbilityTest.createNewFile()

            File propertiesFile = new File("${project.rootDir.absolutePath}/gradle.properties")
            if (!propertiesFile.exists()) {
                propertiesFile.createNewFile()
            }
            def gradleprop = new Properties(propertiesFile, true)
            def allowWrite = false
            if (!gradleprop.hasProperty("org.gradle.caching")) {
                gradleprop.setProperty("org.gradle.caching", "true")
                allowWrite = true
            }
            if (!gradleprop.hasProperty("org.gradle.caching.debug")) {
                gradleprop.setProperty("org.gradle.caching.debug", "true")
                allowWrite = true
            }
            if (!gradleprop.hasProperty("org.gradle.daemon.idletimeout")) {
                gradleprop.setProperty("org.gradle.daemon.idletimeout", "1800000")
                allowWrite = true
            }
            if (!gradleprop.hasProperty("org.gradle.jvmargs")) {
                allowWrite = true
                gradleprop.set("org.gradle.jvmargs", "-Xms256m -Xmx1048m -Xss27m -XX:MaxPermSize=512m -XX:ReservedCodeCacheSize=225m -XX:+UseCompressedOops -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8")
            }
            if (!gradleprop.hasProperty("systemProp.file.encoding")) {
                allowWrite = true
                gradleprop.set("systemProp.file.encoding", "utf-8")
            }
            if (!gradleprop.hasProperty("org.gradle.parallel")) {
                allowWrite = true
                gradleprop.set("org.gradle.parallel", "true")
            }
            if (!gradleprop.hasProperty("org.gradle.configureondemand")) {
                allowWrite = true
                gradleprop.set("org.gradle.configureondemand", "true")
            }
            if (allowWrite) {
                Writer writer = propertiesFile.newWriter()
                gradleprop.store(writer, null)
                writer.close()
                println("Saved properties: ${styler(GREEN, propertiesFile.toString())}")
            }
        }
    }
}