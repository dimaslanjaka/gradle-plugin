package com.dimaslanjaka.gradle.offline

import com.dimaslanjaka.gradle.Utils.Properties
import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler
import org.gradle.internal.reflect.Instantiator

import static com.dimaslanjaka.gradle.Utils.ConsoleColors.CYAN_BOLD_BRIGHT
import static com.dimaslanjaka.gradle.Utils.ConsoleColors.styler

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
        boolean isAndroidProject = project.plugins.hasPlugin('com.android.application') || project.plugins.hasPlugin('com.android.library')
        boolean isKotlinMultiplatformProject = project.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")
        File propertiesFile = new File("${project.rootDir.absolutePath}/gradle.properties")
        Properties gradleprop = new Properties(propertiesFile, true)

        if (isAndroidProject) {
            if (!gradleprop.hasProperty("android.useAndroidX")) {
                gradleprop.set("android.useAndroidX", "true")
            }
            if (!gradleprop.hasProperty("android.enableJetifier")) {
                gradleprop.set("android.enableJetifier", "true")
            }
        }
        if (isKotlinMultiplatformProject) {
            gradleprop.hasOrSet("kotlin.code.style", "official")
            gradleprop.hasOrSet("kotlin.mpp.enableGranularSourceSetsMetadata", "true")
            gradleprop.hasOrSet("kotlin.native.enableDependencyPropagation", "true")
            if (new File(project.rootDir, "iosApp").exists()) {
                gradleprop.hasOrSet("xcodeproj", "./iosApp")
            }
        }
        gradleprop.hasOrSet("kotlin_version", "1.4.10")
        gradleprop.hasOrSet("groovy_version", "2.5.10")
        gradleprop.hasOrSet("org.gradle.caching", "true")
        gradleprop.hasOrSet("org.gradle.caching.debug", "true")
        gradleprop.hasOrSet("org.gradle.daemon.idletimeout", "1800000")
        gradleprop.hasOrSet("org.gradle.jvmargs", "-Xms256m -Xmx1048m -Xss27m -XX:MaxPermSize=512m -XX:ReservedCodeCacheSize=225m -XX:+UseCompressedOops -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8")
        gradleprop.hasOrSet("systemProp.file.encoding", "utf-8")
        gradleprop.hasOrSet("org.gradle.parallel", "true")
        gradleprop.hasOrSet("org.gradle.configureondemand", "true")
    }
}