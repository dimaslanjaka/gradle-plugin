package com.dimaslanjaka.gradle.curl

import org.gradle.api.Project
import org.gradle.api.tasks.OutputDirectory
import org.gradle.testfixtures.ProjectBuilder
import java.io.File

class ProjectCore {
    companion object {
        var project: Project = ProjectBuilder.builder()
                .withProjectDir(File("build/JUnit/DownloadFileTest").absoluteFile)
                .withName("project name")
                .build()

        @OutputDirectory
        val outDir: File = project.buildDir

        fun createProject(name: String): Project {
            project = ProjectBuilder.builder()
                    .withProjectDir(File("build/JUnit/$name").absoluteFile)
                    .withName("project $name")
                    .build()
            return project
        }
    }
}