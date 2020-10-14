 [![No Maintenance Intended](unmaintained.svg)](unmaintained.svg)

- auto clear big log of gradle
- auto create version code based on current date
- auto cached offline dependencies without redownload them
- auto convert gradle cache artifacts into local maven repository `~/.m2/repository`

```gradle
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

### On Android Project Set Below (optional)
```
//android.defaultConfig.javaCompileOptions.annotationProcessorOptions.includeCompileClasspath = true
android {
    // other configurations
    packagingOptions {
        pickFirst '**/*.so'
        exclude '**/*.html' // <-- inline javadoc of plugin will be excluded
        exclude 'META-INF/services/org.codehaus.groovy.transform.ASTTransformation'
        exclude 'META-INF/services/org.codehaus.groovy.runtime.ExtensionModule'
        exclude 'META-INF/groovy-release-info.properties'
        exclude 'README.md'
        exclude "META-INF/DEPENDENCIES"
        exclude "META-INF/LICENSE"
        exclude "META-INF/INDEX.LIST"
        exclude "META-INF/LICENSE.txt"
        exclude "META-INF/license.txt"
        exclude "META-INF/NOTICE"
        exclude "META-INF/NOTICE.txt"
        exclude "META-INF/notice.txt"
        exclude "META-INF/ASL2.0"
        exclude("META-INF/*.kotlin_module")
    }
}
```

### If you want automated in all of your project, put these codes in `~/.gradle/init.d/offline.gradle` (Not recommended)
 ```gradle
 // init.d script for all project initializer
 initscript {
    ext {
        userHome = new File(System.properties['user.home'].toString())
        reposDir = new File(userHome, '.m2/repository')
        offlineRepositoryRoot = reposDir.absolutePath
    }
    userHome = new File(System.properties['user.home'].toString())
    reposDir = new File(userHome, '.m2/repository')
    offlineRepositoryRoot = reposDir.absolutePath

    repositories {
        jcenter()
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
        flatDir {
            dirs "$gradleHomeDir/libs"
            //dirs "$rootProject.projectDir/libs"
        }
        maven { url 'https://maven.google.com' }
        maven { url 'https://maven.mozilla.org/maven2/' }
        maven { url 'https://jitpack.io' }
        maven { url 'https://plugins.gradle.org/m2/' }
        maven { url 'http://maven.springframework.org/release' }
        maven { url 'http://maven.restlet.org' }
        maven { url "https://repo.spring.io/snapshot" }
        maven { url "https://repo.spring.io/milestone" }
        maven { url 'https://repo.gradle.org/gradle/libs-releases' }
        maven {
            url "https://maven.pkg.github.com/dimaslanjaka/gradle-plugin"
            credentials {
                username = "dimaslanjaka"
                password = "d98acb59134fabab05145ddeb7abe4441db18b4a"
            }
        }
        maven { url 'https://repo1.maven.org/maven2' }
        maven {
            url offlineRepositoryRoot
        }
    }
    dependencies {
        classpath 'com.dimaslanjaka:gradle-plugin:latest.release'
    }
}
```

### Auto version code
```gradle
// apply the plugin
apply plugin: "com.dimaslanjaka.gradle-plugin"

println("Current Version ${AutoVersionCode}") 
println("Current Version Without MinuteSeconds ${FixedVersionCode}")
```
