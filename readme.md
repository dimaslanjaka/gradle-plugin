<!--- [![No Maintenance Intended](unmaintained.svg)](unmaintained.svg) --->

- auto clear big log of gradle
- auto create version code based on current date
- auto cached offline dependencies without redownload them
- auto convert gradle cache artifacts into local maven repository `~/.m2/repository`
- auto clean gradle cache 3 days old `~/.gradle/caches/**/*`

```groovy
// add to classpath (build.gradle)
dependencies {
  classpath "com.dimaslanjaka:gradle-plugin:latest.release"
}
// or (build.gradle.kts)
dependencies {
  classpath("com.dimaslanjaka:gradle-plugin:latest.release")
}

// apply the plugin (build.gradle)
apply plugin: "com.dimaslanjaka.gradle-plugin"
// or (build.gradle.kts)
apply(plugin = "com.dimaslanjaka.gradle-plugin")

// apply for subproject
subprojects {
    apply(plugin = "com.dimaslanjaka.gradle-plugin") // Version should be inherited from parent
}

// apply for all project
allprojects {
    apply(plugin = "com.dimaslanjaka.gradle-plugin") // Version should be inherited from parent
}
```
### Full build.gradle example
```groovy
buildscript {
    repositories {
        mavenCentral()
        google()
        jcenter()
        mavenLocal()
        gradlePluginPortal()
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath "com.dimaslanjaka:gradle-plugin:latest.release"
    }
}
apply plugin: "com.dimaslanjaka"
// this also work
//apply plugin: "com.dimaslanjaka.gradle-plugin"
```

### Auto version code
```groovy
// apply the plugin
apply plugin: "com.dimaslanjaka.gradle-plugin"

// print generated version
println("Current Version ${AutoVersionCode}") 
println("Current Version Without MinuteSeconds ${FixedVersionCode}")
```
