package com.dimaslanjaka.gradle


import com.dimaslanjaka.gradle.utils.Properties
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.tasks.TaskState
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Disabled

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class OfflineTest {
    private Project project = ProjectBuilder.builder()
            .withProjectDir(new File("build/JUnit").absoluteFile)
            .withName("project name")
            .build()
    private Project subproject = ProjectBuilder.builder()
            .withProjectDir(new File("build/JUnit/sub").absoluteFile)
            .withName("project name")
            .build()
    private static final PLUGIN_ID = 'com.dimaslanjaka.gradle-plugin'
    private Properties gradleprop

    @Before
    void setUp() {
        project.configurations.create("implementation")
        project.configurations.create("compile")
        project.configurations.create("testCompile")
        GrabVerTest.simulateProperties(1, 1, 1, 20, 3)
        gradleprop = new Properties("${project.rootDir}/gradle.properties" as File, true)
    }

    @Test
    @Disabled
    void log() {
        def log = new File(project.buildDir, "tmp/${this.class.name}.properties")
        Properties orderedProperties = new Properties(log, true)
        def allowToModify = false
        if (!orderedProperties.contains("TIMESTAMP")) {
            allowToModify = true
        } else {
            def value = orderedProperties.get("TIMESTAMP").toLong()
            Date created = new Date(value)
            def difference = new Date().time - created.time
            if (difference < TimeUnit.MINUTES.toMillis(60)) {
                // log writed less than 60 mins ago
            }
        }
        if (allowToModify) {
            orderedProperties.set("TIMESTAMP", new Date().getTime().toString())
            println("Allow to modify")
        }
    }

    @Test
    @Disabled
    void jvm() {
        //org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
        def jvmargs = gradleprop.get("org.gradle.jvmargs")
        println(jvmargs)
    }

    private static String modifyDateLayout(String inputDate) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").parse(inputDate)
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date)
    }

    @Test
    void testOffline() {
        List<String> tasks = new ArrayList<>()
        tasks.add("updateOfflineRepository")

        project.gradle.startParameter.setTaskNames(tasks)
        project.pluginManager.apply PLUGIN_ID

        //dependencies.add('implementation', 'com.google.code.gson:gson:2.8.6')
        //project.dependencies.add("compile", "com.google.code.gson:gson:2.8.6")
        /*
        project.allprojects {
            dependencies {
                compile 'com.google.code.gson:gson:2.8.6'
            }
        }
        project.dependencies {
            testCompile 'org.springframework:spring-core:4.0.0.RELEASE'
        }
         */
        project.dependencies.create('org.springframework:spring-core:4.0.0.RELEASE')

        def userhome = new File(System.properties['user.home'].toString())
        def mavenlocal = new File(userhome, '.m2/repository')
        project.ext['userHome'] = userhome.absolutePath
        project.ext['reposDir'] = mavenlocal.absolutePath
        project.ext['offlineRepositoryRoot'] = mavenlocal.absolutePath
        //./gradlew updateOfflineRepository
        project.repositories {
            maven {
                url "https://maven.pkg.github.com/dimaslanjaka/gradle-plugin"
                credentials {
                    username = "dimaslanjaka"
                    password = "d98acb59134fabab05145ddeb7abe4441db18b4a"
                }
            }
            maven { url 'https://repo1.maven.org/maven2' }
            maven {
                url mavenlocal.absolutePath
            }
        }
        project.offlineDependencies {
            repositories {
                // You'll have to add your buildscript repositories here too
                maven {
                    url 'https://plugins.gradle.org/m2/'
                }
                mavenCentral()
                jcenter()
            }

            includeSources = true
            includeJavadocs = true
            includePoms = true
            includeIvyXmls = true
            includeBuildscriptDependencies = true
        }

        project.versioning {
            major = 1
            minor = 1
            preRelease = "RC2"
        }

        project.gradle.taskGraph.afterTask {
            Task task, TaskState state ->
                if (state.failure) {
                    println("$task.name FAILED")
                } else {
                    println("$task.name SUCCESSFULL")
                }
        }
    }

    static Set getFamilyDependencies(ConfigurationContainer configurations) {
        def result = configurations.collect { configuration ->
            configuration.allDependencies.findAll { dependency ->
                dependency instanceof DefaultProjectDependency
            } collect { projectDependency ->
                projectDependency.dependencyProject.name
            }
        } flatten()

        result as Set
    }

    @Test
    void shouldGetFamilyDependencies() {
        project.configurations.create('configuration0')
        project.configurations.create('configuration1')
        project.configurations.configuration0 {
            subproject
        }

        project.configurations.each { configuration ->
            println "***************** ${configuration}"

            configuration.allDependencies.each {
                println "@@@@@@@@@@@@@@@@@ ${it}"
            }
        }
        final actual = getFamilyDependencies(project.configurations)

        println(actual.size())
    }

    @Test
    void testVersioning() throws Exception {
        List<String> tasks = new ArrayList<>()
        tasks.add("war")
        project.gradle.startParameter.setTaskNames(tasks)
        project.pluginManager.apply PLUGIN_ID
        project.versioning {
            major = 1
            minor = 1
            preRelease = "RC2"
        }
        Assert.assertNotSame("minor check", 0, project.versioning.minor)
        Assert.assertNotSame("patch check", 0, project.versioning.patch)
        Assert.assertNotSame("build check", 0, project.versioning.build)
        Assert.assertNotSame("code check", 0, project.versioning.code)
    }
}
