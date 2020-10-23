package com.dimaslanjaka.gradle

import com.dimaslanjaka.gradle.core.Plugin
import com.dimaslanjaka.gradle.testing.ProjectBuilder
import org.gradle.api.Project
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.junit.runner.Runner
import org.junit.runners.MethodSorters
import java.io.File
import java.io.InputStream
import java.util.*

@RunWith(Runner::class)
@FixMethodOrder(MethodSorters.DEFAULT)
class TestNoProperties {
    lateinit var project: Project
    private lateinit var plugin: Plugin
    private fun InputStream.toFile(file: File) {
        use { input ->
            file.outputStream().use { input.copyTo(it) }
        }
    }

    @get:Rule
    val pb = ProjectBuilder(File("build/test/${Date()}"))

    @Test
    fun `project builds`() {
        //pb.`build gradle from url`()
        //pb.instance.pluginManager.apply(com.dimaslanjaka.gradle.core.Plugin::class.java)
        plugin = Plugin()
        plugin.apply(pb.instance)
        pb.run("build")
    }
}