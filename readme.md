# Offline gradle plugin

Plugin Portal [https://plugins.gradle.org/plugin/com.dimaslanjaka](https://plugins.gradle.org/plugin/com.dimaslanjaka)

## How to manual import
- copy this project inside your project
- then import like bellow

### settings.gradle
```gradle
includeBuild("plugin") //name folder of this project
```

## Usage Sample
how to apply this plugin to your root project
### build.gradle root project
```gradle
// repository
repositories {
    mavenLocal() // make sure local maven included
    mavenCentral()
    google()
    jcenter()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

// activate plugin
plugins {
    id("com.dimaslanjaka")
}

// configurations
offlineConfig {
    // Unlimited Cached Artifacts
    limit = Integer.MAX_VALUE
    // local folder location
    localRepository = new File("build/repository")
    // debug while processing (dump)
    debug = false
    // forget expire time, do copy each project sync
    force = false
    // extension of artifacts went to save locally
    extensions = {".module", ".jar", ".pom", ".aar", ".sha1", ".xml"}
}
```

### Optional in settings.gradle
```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal() // make sure local maven included
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

rootProject.name = "your project name"
```