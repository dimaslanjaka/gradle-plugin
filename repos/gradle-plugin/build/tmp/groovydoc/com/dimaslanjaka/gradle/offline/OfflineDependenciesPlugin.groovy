package com.dimaslanjaka.gradle.offline

import com.dimaslanjaka.gradle.utils.Properties
import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler
import org.gradle.api.tasks.Copy
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.GradleVersion

import static com.dimaslanjaka.gradle.utils.ConsoleColors.CYAN_BOLD_BRIGHT
import static com.dimaslanjaka.gradle.utils.ConsoleColors.styler

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

        println("Offline dependencies root configured at ${styler(CYAN_BOLD_BRIGHT, project.ext.offlineRepositoryRoot as String)}")

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

        /*
        if (!project.extensions.hasProperty("thirdPartyDependencies")) {
            //project.extensions.add("thirdPartyDependencies", new ThirdPartyDependenciesExtension(project: project))
            project.extensions.create("thirdPartyDependencies", ThirdPartyDependenciesExtension, project)
        }
         */

        project.gradle.buildFinished() { BuildResult result ->
            // TODO: Copy artifact after build finished
            new GradleCacheToMavenRepo(project) {
                @Override
                void onFinish() {
                    new Thread(new Runnable() {
                        @Override
                        void run() {
                            CacheCleaner.apply(project)
                        }
                    })
                }
            }
        }

        project.afterEvaluate {
            gradlePropertiesFix()

            /**
             * @link "https://stackoverflow.com/questions/26697999/gradle-equivalent-to-mavens-copy-dependencies"
             */
            project.task("copyArtifacts", type: Copy) {
                if (GradleVersion.current() < GradleVersion.version("4.0")) {
                    if (project.configurations.hasProperty("compile")) {
                        from project.configurations.compile
                        into project.ext.offlineRepositoryRoot
                    }
                } else {
                    if (project.configurations.hasProperty("default")) {
                        from project.configurations.default
                        into project.ext.offlineRepositoryRoot
                    }
                }
            }
        }
    }

    void addDependencies(String deps) {
        project.dependencies {
            if (GradleVersion.current() < GradleVersion.version("4.0")) {
                delegate."compile"("$deps")
            } else {
                delegate."implementation"("$deps")
            }
        }
    }

    /**
     * Add dependencies
     * @param scope compile, testCompile, etc
     * @param deps dependencies artifact group:artifact:version
     */
    void addDependencies(String scope, String deps) {
        project.dependencies {
            delegate."$scope"("$deps")
        }
    }

    /**
     * Enabling Caching Ability Through Gradle Properties
     */
    void gradlePropertiesFix() {
        // TODO: add caching ability to gradle.properties
        boolean isAndroidProject = project.plugins.hasPlugin('com.android.application') || project.plugins.hasPlugin('com.android.library')
        boolean isKotlinMultiplatformProject = project.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")
        File propertiesFile = new File("${project.rootDir.absolutePath}/gradle.properties")
        Properties gradleprop = new Properties(propertiesFile, true)

        if (isAndroidProject) {
            gradleprop.hasOrSet("android.useAndroidX", "true")
            gradleprop.hasOrSet("android.enableJetifier", "true")
            gradleprop.hasOrSet("android.enableBuildCache", "false")
        }
        if (isKotlinMultiplatformProject) {
            gradleprop.hasOrSet("kotlin.code.style", "official")
            gradleprop.hasOrSet("kotlin.mpp.enableGranularSourceSetsMetadata", "true")
            gradleprop.hasOrSet("kotlin.native.enableDependencyPropagation", "true")
            if (new File(project.rootDir, "iosApp").exists()) {
                gradleprop.hasOrSet("xcodeproj", "./iosApp")
            }
        }
        gradleprop.hasOrSet("startParameter.offline", "true")
        gradleprop.hasOrSet("kotlin_version", "1.4.10")
        gradleprop.hasOrSet("groovy_version", "2.5.10")
        gradleprop.hasOrSet("org.gradle.caching", "true")
        gradleprop.hasOrSet("org.gradle.caching.debug", "false")
        gradleprop.hasOrSet("org.gradle.daemon.idletimeout", "1800000")
        gradleprop.hasOrSet("org.gradle.jvmargs", "-Xms256m -Xmx1048m -Xss27m -XX:MaxPermSize=512m -XX:ReservedCodeCacheSize=225m -XX:+UseCompressedOops -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8")
        gradleprop.hasOrSet("systemProp.file.encoding", "utf-8")
        gradleprop.hasOrSet("org.gradle.parallel", "true")
        gradleprop.hasOrSet("org.gradle.configureondemand", "true")
        gradleprop.hasOrSet("org.gradle.cache.cleanup", "false")
        //gradleprop.hasOrSet("org.gradle.unsafe.configuration-cache", "ON");
        // Use this flag sparingly, in case some of the plugins are not fully compatible
        //gradleprop.hasOrSet("org.gradle.unsafe.configuration-cache-problems", "warn");
    }
}