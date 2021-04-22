buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.1")
        classpath(kotlin("gradle-plugin", version = "1.4.32"))
    }
}

plugins {
    // activate offline plugin
    id("com.dimaslanjaka")
}
// configure offline config
if (project.hasProperty("offlineConfig")) {
    offlineConfig {
        limit = Integer.MAX_VALUE
    }
}

description = "Test plugin using composite build"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
