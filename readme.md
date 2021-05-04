# Plugin Portal [https://plugins.gradle.org/plugin/com.dimaslanjaka](https://plugins.gradle.org/plugin/com.dimaslanjaka)
## How to manual import
- copy this project inside your project
- then import like bellow

## settings.gradle
```gradle
includeBuild("plugin") //name folder of this project
```

# to apply this plugin to your root project
## build.gradle root project
```gradle
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