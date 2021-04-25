package com.dimaslanjaka.gradle.api

import org.gradle.api.Project

object Utils {
    @JvmStatic
    fun isAndroid(project: Project): Boolean {
        return project.plugins.hasPlugin("com.android.library") || project.plugins.hasPlugin("com.android.application")
    }
}