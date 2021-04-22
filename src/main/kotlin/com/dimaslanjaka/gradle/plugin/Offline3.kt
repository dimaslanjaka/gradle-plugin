package com.dimaslanjaka.gradle.plugin

import org.gradle.api.Project

class Offline3(project: Project) {
    init {
        project.afterEvaluate {
            //it.configurations.collectionSchema.
            val classpath = it.configurations.getByName("compileClasspath")
        }
    }
}