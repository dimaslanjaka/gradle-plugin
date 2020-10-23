package com.dimaslanjaka.gradle.curl

import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DownloadFileTest {
    private lateinit var project: Project
    private lateinit var outputDirectory: File
    private val pluginId = "com.dimaslanjaka.gradle-plugin"

    @OutputDirectory
    val outDir = File("build/artifactory")

    @Input
    val url = "https://maven.springframework.org/release/com/cloudfoundry/identity/cloudfoundry-login-server/1.2.10/cloudfoundry-login-server-1.2.10-sources.jar"

    @Before
    fun initialize() {
        project = ProjectBuilder.builder()
                .withProjectDir(File("build/JUnit/DownloadFileTest").absoluteFile)
                .withName("project name")
                .build()
        outputDirectory = project.rootDir
    }

    @Test
    fun test() {
        val tasks = arrayListOf<String>("build")
        project.gradle.startParameter.setTaskNames(tasks)
        project.pluginManager.apply(pluginId)
        DownloadFile(url, outputDirectory)
    }
}