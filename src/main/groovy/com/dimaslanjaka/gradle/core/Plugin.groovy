package com.dimaslanjaka.gradle.core

import com.dimaslanjaka.gradle.offline.OfflineDependenciesPlugin
import com.dimaslanjaka.gradle.offline.OfflineMavenRepository
import com.dimaslanjaka.gradle.repository.Resolver
import com.dimaslanjaka.gradle.resourceProvider.ResourceProviderPlugin
import com.dimaslanjaka.gradle.testing.Reporter
import com.dimaslanjaka.gradle.utils.Properties
import com.dimaslanjaka.gradle.utils.file.FileHelper
import com.dimaslanjaka.gradle.versioning.GrabVer
import com.dimaslanjaka.init.AggregateDocPlugin
import com.dimaslanjaka.init.ProjectInfo
import com.dimaslanjaka.webserver.httpserver.HTTPServerTask
import org.gradle.BuildResult
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.*
import org.gradle.api.tasks.TaskState
import org.json.JSONObject

import static com.dimaslanjaka.gradle.utils.ConsoleColors.*

class Plugin implements org.gradle.api.Plugin<Project> {
    public static File projectBuildDir
    public static File projectDir
    public static File projectRootDir
    public static File tmpDir
    public static Project project
    public static boolean isAndroidProject
    public static List<String> configurationList = new ArrayList<>()
    public static List<Dependency> dependenciesList = new ArrayList<>()

    @Override
    void apply(Project project) {
        // set default color
        System.setProperty("org.gradle.color.failure", "RED")
        System.setProperty("org.gradle.color.progressstatus", "YELLOW")
        System.setProperty("org.gradle.color.success", "GREEN")

        Plugin.project = project
        // TODO: Resolve repositories
        Resolver.resolve(project)
        // TODO: Aggregating multiproject javadoc
        AggregateDocPlugin.apply(project)
        // TODO: Print project informations
        ProjectInfo.apply(project)
        // TODO: Publish repositories url to server
        Reporter.apply(project)

        this.project = project
        projectBuildDir = project.buildDir
        projectDir = project.projectDir
        projectRootDir = project.rootDir
        tmpDir = new File(project.buildDir, this.getClass().package.name)
        if (!tmpDir.exists()) tmpDir.mkdirs()
        isAndroidProject = project.plugins.hasPlugin('com.android.application') || project.plugins.hasPlugin('com.android.library')

        project.getGradle().addListener(new DependencyResolutionListener() {
            @Override
            void beforeResolve(ResolvableDependencies resolvableDependencies) {

            }

            @Override
            void afterResolve(ResolvableDependencies resolvableDependencies) {
                //println("Resolved Dependency: ${resolvableDependencies.name}")
                configurationList.add(resolvableDependencies.name)
                resolvableDependencies.getDependencies().findAll { dep ->
                    dependenciesList.add(dep)
                }
            }
        })

        // add repositories
        def userHome = new File(System.properties['user.home'].toString())
        def reposDir = new File(userHome, '.m2/repository')
        def offlineRepositoryRoot = reposDir.absolutePath
        project.ext["offlineRepositoryRoot"] = offlineRepositoryRoot
        assert project.offlineRepositoryRoot == offlineRepositoryRoot
        assert project.ext["offlineRepositoryRoot"] == offlineRepositoryRoot
        project.repositories.add(project.repositories.jcenter())
        project.repositories.add(project.repositories.mavenCentral())
        project.repositories.add(project.repositories.mavenLocal())
        /*project.repositories.add(project.repositories.maven {
            url "https://maven.pkg.github.com/dimaslanjaka/gradle-plugin"
            credentials {
                username = "dimaslanjaka"
                password = "d98acb59134fabab05145ddeb7abe4441db18b4a"
            }
        })*/
        project.repositories.add(project.repositories.maven {
            url "https://plugins.gradle.org/m2/"
        })
        project.repositories.add(project.repositories.maven { url offlineRepositoryRoot })
        //project.dependencies.add('compile', 'io.realm:realm-android:0.80.3')

        // add local dependcies repository
        project.buildscript.repositories {
            def repos = new ArrayList()
            reposDir.eachDir { repos.add(it) }
            def androidstudio = new File(System.properties['user.home'] as File, ".android/manual-offline-m2")
            if (androidstudio.exists()) {
                androidstudio.eachDir {
                    repos.add(it)
                }
            }
            repos.sort()

            for (repo in repos) {
                // android studio offline support
                maven {
                    name = "injected_offline_${repo.name}"
                    url = repo.toURI().toURL()
                }
                // default
                maven {
                    name = repo.name
                    url = repo.toURI().toURL()
                }
            }
        }

        project.afterEvaluate {
            // start offline dependencies
            OfflineDependenciesPlugin offline = new OfflineDependenciesPlugin()
            offline.apply(project)
            GrabVer version = new GrabVer()
            version.apply(project)
            printTaskList()

            if (!dependenciesList.toString().contains("com.google.inject")) {
                if (configurationList.hasProperty('implementation')) {
                    project.dependencies.add("implementation", "com.google.inject:guice:4.0-beta5")
                } else if (configurationList.hasProperty('compile')) {
                    project.dependencies.add("compile", "com.google.inject:guice:4.0-beta5")
                }
            }

            project.offlineDependencies {
                repositories {
                    maven { url 'https://maven.google.com' }
                    jcenter()
                    mavenCentral()
                    mavenLocal()
                    maven { url 'https://maven.mozilla.org/maven2/' }
                    maven { url 'https://jitpack.io' }
                    maven { url 'https://plugins.gradle.org/m2/' }
                    maven { url 'http://maven.springframework.org/release' }
                    maven { url 'http://maven.restlet.org' }
                    gradlePluginPortal()
                    maven { url "https://repo.spring.io/snapshot" }
                    maven { url "https://repo.spring.io/milestone" }
                    maven { url 'https://repo.gradle.org/gradle/libs-releases' }
                }

                includeSources = true
                includeJavadocs = true
                includePoms = true
                includeIvyXmls = true
                includeBuildscriptDependencies = true
            }

            // TODO: Start Class If Project Is Android Project
            if (isAndroidProject) {
                // TODO: Start Resource Provider
                def resourceProvider = new ResourceProviderPlugin(project)
                // TODO: Start Leaking Memories
                if (!dependenciesList.toString().contains("com.squareup.leakcanary")) {
                    println(styler(GREEN_BOLD, "Adding memory leaks detector on debug mode"))
                    if (configurationList.hasProperty('implementation')) {
                        project.dependencies.add("debugImplementation", "com.squareup.leakcanary:leakcanary-android:2.5")
                    } else if (configurationList.hasProperty('compile')) {
                        project.dependencies.add("debugCompile", "com.squareup.leakcanary:leakcanary-android:2.5")
                    }
                }
            }
        }

        // TODO: declare process killer
        project.task('killJava', type: KillJavaProcesses) {
        }

        // TODO: declare copy jar task
        project.task('copyJar', type: OfflineMavenRepository) {
        }

        // TODO: Create Webserver Task
        project.task("StartWebServerMavenRepository", type: HTTPServerTask) {}

        project.gradle.buildFinished() { BuildResult result ->
            // TODO: Log last build
            logLastBuildTime()

            // TODO: clean log after build
            cleanLog()
        }

        project.gradle.taskGraph.afterTask {
            Task task, TaskState state ->
                if (state.failure) {
                    println("${project.name}:${task.name} ${styler(RED, 'FAILED')}")
                } else {
                    println("${project.name}:${task.name} ${styler(GREEN, 'SUCCESS')}")
                }
        }
    }

    static def 'print all sourceSets'(Project project){
        // TODO: print all sourceset
        if (project.hasProperty('sourceSets')) {
            JSONObject jsonObject = new JSONObject()
            project.sourceSets.each {
                //println(it)
                List<Object> objects = new LinkedList<>()
                it.allSource.eachWithIndex { Object entry, int i ->
                    //println entry
                    objects.add(entry)
                }
                jsonObject.put("$it", objects)
            }
            new File("${project.buildDir}/sourceSets").with {
                if (!it.exists()) it.mkdirs()
            }
            FileHelper.createNew(new File("${project.buildDir}/sourceSets/index.json"), jsonObject.toJSONString())
        }
    }

    void showProjectDependencies(Project project, int nesting) {
        ConfigurationContainer configurations = project.configurations
        Configuration configuration = configurations.compile
        println " " * (3 * nesting) + project.name
        DomainObjectSet<ProjectDependency> projectDependencies = configuration.dependencies.withType ProjectDependency
        projectDependencies.forEach {
            showProjectDependencies(it.dependencyProject, nesting + 1)
        }
    }

    void depListener(Project project) {
        def compileDeps = project.getConfigurations().getByName("compile").getDependencies()
        project.getGradle().addListener(new DependencyResolutionListener() {
            @Override
            void beforeResolve(ResolvableDependencies resolvableDependencies) {
                //compileDeps.add(project.getDependencies().create("org.foo:bar:$version"))
                project.getGradle().removeListener(this)
            }

            @Override
            void afterResolve(ResolvableDependencies resolvableDependencies) {}
        })
    }

    void logLastBuildTime() {
        def orderedProperties = new Properties(new File("${projectBuildDir}/tmp/${this.getClass().name}.properties"), true)
        Date date = new Date()
        orderedProperties.set("TIME", date.getTime().toString())
        orderedProperties.set("DATE", date.toString())
    }

    static void printTaskList() {
        project.tasks.collect { task ->
            def dir = new File(project.buildDir, "tasks/${task.name}.properties")
            if (!dir.parentFile.exists()) dir.parentFile.mkdirs()
            dir.text = "task=" + task.name + " dependsOn=" + task.dependsOn.toArray().toString()
        }
    }

    static File readFile(String path) {
        File test = new File(path)
        if (!test.parentFile.exists()) test.parentFile.mkdirs()
        if (!test.exists()) test.createNewFile()
        return test
    }

    static void cleanLog() {
        if (project != null) {
            cleanLog(project)
        }
    }

    static void cleanLog(Project project) {
        //delete gradle log
        def gradle = project.getGradle()
        def currentGradleHomeDir = gradle.getGradleUserHomeDir().getAbsolutePath()
        def currentGradleVersion = gradle.getGradleVersion()
        new File("${currentGradleHomeDir}/daemon/${currentGradleVersion}").listFiles().each {
            if (it.getName().endsWith('.out.log')) {
                println("Deleting gradle log file: $it") // Optional debug output
                it.delete()
            }
        }
    }
}


