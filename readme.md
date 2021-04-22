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
}
```