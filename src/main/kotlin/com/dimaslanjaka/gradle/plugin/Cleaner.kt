package com.dimaslanjaka.gradle.plugin

import com.dimaslanjaka.kotlin.File
import org.gradle.api.Project
import java.util.*

class Cleaner {
    lateinit var project: Project
    lateinit var logfile: File

    constructor(project: Project) {
        this.project = project
        logfile = File(project.buildDir.absolutePath, "plugin/com.dimaslanjaka/clean-${project.name}")
        logfile.resolveParent()
        logfile.write("Cleaner Start ${Date()}\n\n")
    }

    constructor() {}

    fun start() {
        val local = Core.extension.localRepository.absolutePath
        File(local).walk()?.files?.forEach { artifact ->
            if (artifact.name.endsWith(".pom")) {
                var dontRemove = false
                // if in directory contains jar file, skip
                artifact.parentFile.listFiles()?.map { files ->
                    files?.let {
                        if (it.name.endsWith(".jar")) {
                            dontRemove = true
                        }
                    }
                }
                //println("(${dontRemove}) $artifact")
                if (!dontRemove) {
                    val deleted = if (artifact.parentFile.delete()) "deleted" else false
                    if (this::logfile.isInitialized) {
                        this.logfile.appendText("$artifact $deleted\n")
                    }
                }
            }
        }
    }
}

fun main() {
    Core.extension = CoreExtension(File("build").createGradleProject())
    Cleaner().start()
}