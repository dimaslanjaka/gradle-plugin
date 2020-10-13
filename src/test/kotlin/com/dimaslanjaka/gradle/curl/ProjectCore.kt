package com.dimaslanjaka.gradle.curl

import org.gradle.api.Project
import org.gradle.api.tasks.OutputDirectory
import org.gradle.testfixtures.ProjectBuilder
import java.io.File

class ProjectCore {
    companion object {
        var parent: Project = ProjectBuilder.builder()
                .withProjectDir(File("build/JUnit/ParentProject").absoluteFile)
                .withName("project parent")
                .build()
        var project: Project = ProjectBuilder.builder()
                .withProjectDir(File("build/JUnit/DownloadFileTest").absoluteFile)
                .withName("project name")
                .build()

        @OutputDirectory
        val outDir: File = project.buildDir

        fun createProject(name: String, withParent: Boolean = false): Project {
            val create = ProjectBuilder.builder()
                    .withName("Project $name")
            if (withParent) {
                parent = ProjectBuilder.builder()
                        .withProjectDir(File("build/JUnit/ParentProject${name.toUpperCase()}").absoluteFile)
                        .withName("project parent $name")
                        .build()
                create.withParent(parent)
                create.withProjectDir(File("build/JUnit/${parent.rootDir.absolutePath}/$name").absoluteFile)
            } else {
                create.withProjectDir(File("build/JUnit/$name").absoluteFile)
            }
            project = create.build()
            return project
        }
    }
}