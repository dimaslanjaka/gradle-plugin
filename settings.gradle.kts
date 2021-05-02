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
        maven("https://repo.spring.io/simple/libs-release-local/")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    }
}

rootProject.name = "Powerful Gradle Plugin"

//includeBuild("repo/components")