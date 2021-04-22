package com.dimaslanjaka.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class Offline3(p: Project) {
    init {
        p.afterEvaluate { project ->
            //it.configurations.collectionSchema.
            val configurations = project.configurations
            configurations.collectionSchema.elements.forEach { schema ->
                println(schema.name)
            }
        }
    }
}