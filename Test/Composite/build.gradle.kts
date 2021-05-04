@file:Suppress("UNUSED_VARIABLE", "LocalVariableName", "PropertyName")

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath(kotlin("gradle-plugin", version = "1.4.32"))
    }
}

plugins {
    // activate offline plugin
    id("com.dimaslanjaka")
}

// configure offline config
offlineConfig {
    limit = 100
    //force = true
    extensions = listOf(".jar", ".aar").toTypedArray()
    debug = false
    //offline3 = true
}

description = "Test plugin using composite build"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        jcenter()
        maven { url = uri("https://jitpack.io") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
    delete(File(rootProject.projectDir.absolutePath, ".idea"))
}
