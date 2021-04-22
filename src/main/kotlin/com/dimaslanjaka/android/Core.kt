package com.dimaslanjaka.android

import com.dimaslanjaka.android.resourceprovider.ResourceProviderPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class Core : Plugin<Project> {
    override fun apply(project: Project) {
        if (project.subprojects.size > 0) {
            project.subprojects.forEach { sp ->
                if (sp is Project) {
                    val android =
                        sp.plugins.hasPlugin("com.android.application") || sp.plugins.hasPlugin("com.android.library")
                    if (android) {
                        val extension = sp.extensions.create("androidConfig", CoreExtension::class.java, project)
                        sp.afterEvaluate {
                            if (extension.contentprovider) {
                                sp.pluginManager.apply(ResourceProviderPlugin::class.java)
                            }
                        }
                    }
                }
            }
        }
    }
}

open class CoreExtension(project: Project) {
    var contentprovider = true
}