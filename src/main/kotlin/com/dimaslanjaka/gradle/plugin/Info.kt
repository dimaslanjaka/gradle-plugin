package com.dimaslanjaka.gradle.plugin

import com.dimaslanjaka.kotlin.ConsoleColors.Companion.println
import org.gradle.api.Project

@Suppress("MemberVisibilityCanBePrivate")
class Info {
    constructor() {}
    constructor(project: Project) {
        getExtensions(project)
        getConfigurations(project)
    }

    private fun getExtensions(project: Project) {
        project.tasks.create("Print All Extension") {
            it.group = "info"
            it.doLast {
                println("Configured Extension:")
                project.extensions.extensionsSchema.forEach { schema ->
                    println("\t-> ${schema.name}\t: Class<${schema.publicType}>")
                }
            }
        }
    }

    private fun getConfigurations(project: Project) {
        project.tasks.create("Print All Configurations") {
            it.group = "info"
            it.doLast {
                println("Configuration List:")
                project.configurations.forEach { conf ->
                    kotlin.io.println(conf.all)
                }
            }
        }
    }
}