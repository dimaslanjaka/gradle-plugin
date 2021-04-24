package com.dimaslanjaka.gradle.api

import com.dimaslanjaka.kotlin.File
import org.gradle.api.Project
import java.net.URI

open class Project(p: Project) {
    private val project = p
    var DEFAULT_BUILD_FILE = "build.gradle"
    var DEFAULT_SETTINGS_BUILD_FILE = "settings.gradle"

    init {
        listOf(
            p.repositories.google(), p.repositories.mavenCentral(), p.repositories.mavenLocal()
        ).forEach {
            project.repositories.add(it)
            project.buildscript.repositories.add(it)
        }
        DEFAULT_BUILD_FILE = File(p.projectDir.absolutePath, DEFAULT_BUILD_FILE).absolutePath
        DEFAULT_SETTINGS_BUILD_FILE = File(p.projectDir.absolutePath, DEFAULT_SETTINGS_BUILD_FILE).absolutePath
        //addDep("com.google.code.gson:gson:2.8.6")
    }

    fun getProject(): Project {
        return project
    }

    /**
     * add single dependency
     * <code>addDep("com.google.code.gson:gson:2.8.6")</code>
     */
    fun addDep(dep: String) {
        project.dependencies.add("implementation", dep)
    }

    /**
     * add single dependency with custom configuration
     * <code>addDep("compileOnly", "com.google.code.gson:gson:2.8.6")</code>
     */
    fun addDep(conf: String, dep: String) {
        project.dependencies.add(conf, dep)
    }

    fun addRepo(urlrepo: String) {
        project.repositories.add(project.repositories.maven {
            it.url = URI(urlrepo)
        })
    }

}