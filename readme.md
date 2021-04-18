# How to import
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
limit = 100
}
```