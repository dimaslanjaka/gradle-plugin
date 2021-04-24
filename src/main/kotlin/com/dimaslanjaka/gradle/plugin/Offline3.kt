package com.dimaslanjaka.gradle.plugin

import org.gradle.api.Project
import java.io.File

class Offline3(p: Project) {
    companion object {
        var debug = false
    }

    init {
        if (debug) {
            val map = mutableMapOf<Any, Any>()
            map["Project name"] = p.name
            map["Project Dir"] = p.projectDir
            println(map)
        }
        p.afterEvaluate { project ->
            val allArtifacts = mutableListOf<File>()
            val cachedArtifacts = mutableListOf<File>()
            val nonCachedArtifacts = mutableListOf<File>()
            val configurations = project.configurations
            configurations.collectionSchema.elements.forEach { schema ->
                val cname = configurations.getByName(schema.name)
                if (cname.isCanBeResolved) {
                    if (debug) {
                        println("Resolved Configuration Schema Of ${schema.name}")
                    }
                    cname.map { configurationSchema ->
                        configurationSchema?.let { artifact ->
                            allArtifacts.add(File(artifact.absolutePath))
                            if (!artifact.absolutePath.contains(".m2")) {
                                nonCachedArtifacts.add(File(artifact.absolutePath))
                            } else {
                                cachedArtifacts.add(File(artifact.absolutePath))
                            }
                        }
                    }
                }
            }

            nonCachedArtifacts.forEach { artifact ->
                val maven = convert2Maven(artifact.absolutePath)
            }
        }
    }

    fun convert2Maven(artifactStr: String) {
        val home = Core.extension.getHome()
        val localRepository = Core.extension.getLocalRepository()
        val gradleCache = File(home, ".gradle/caches/modules-2/files-2.1")
        val replace = artifactStr.replace(gradleCache.absolutePath, "")
        val split = replace.split("[\\/]")
        println(split)
    }
}