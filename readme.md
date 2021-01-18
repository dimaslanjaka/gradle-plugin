# add this project as composite build on your root project
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
```