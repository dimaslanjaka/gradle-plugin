package com.dimaslanjaka.gradle.plugin

import org.gradle.api.Project

class Android(project: Project) {
    val config_name = "androidResolver"

    init {
        project.configurations.create(config_name)
    }
}