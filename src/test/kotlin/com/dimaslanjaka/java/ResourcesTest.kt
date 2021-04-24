package com.dimaslanjaka.java

import com.dimaslanjaka.kotlin.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ResourcesTest {
    @Test
    fun read() {
        val settings = Resources.getResourceFileAsString("settings.gradle")
        Assertions.assertTrue(settings.length > 100)
    }

    @Test
    fun resolve() {
        val dest = File("build/tmp/resources/settings.gradle")
        Resources.copy("settings.gradle", dest.toDimaslanjakaGradlePluginFile())
        Assertions.assertTrue(dest.exists())
    }
}