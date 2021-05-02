pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
        google()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
        maven("https://maven.springframework.org/release")
        maven("https://maven.restlet.com")
        maven("https://repo.gradle.org/gradle/libs-releases")
        maven("https://repo.gradle.org/gradle/enterprise-libs-release-candidates-local")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://plugins.gradle.org/m2/")
    }
}
rootProject.name = "Composite Build Test"

// Relative composite build
val plugin: String = File(rootProject.projectDir, "/../../").absolutePath
includeBuild(plugin)
include(":java-groovy-kotlin")