# copy gradle dependencies based on project configuration - [Gradle Portal](https://plugins.gradle.org/plugin/com.dimaslanjaka.gradle.offline_dependencies)

## Configuration

```groovy
// groovy
offlineDependencies {
    setRepositoryHandler(project.repositories)
    //noinspection GroovyAssignabilityCheck
    root = new File(project.buildDir.absolutePath, "offline") // will save to project_path/build/offline
    includeSources = true
    includeJavadocs = true
    includePoms = true
    includeIvyXmls = true
    includeBuildscriptDependencies = false

    configurations = ["compile", "implementation"] // dependencies to copy by configuration name
    buildScriptConfigurations = ["classpath"] // dependencies from classpath, runtime, etc
}
```

```kotlin
// kotlin
offlineDependencies {
    setRepositoryHandler(project.repositories)
    root = File(project.buildDir.absolutePath, "offline") // will save to project_path/build/offline
    includeSources = true
    includeJavadocs = true
    includePoms = true
    includeIvyXmls = true
    includeBuildscriptDependencies = false

    configurations = setOf("compile", "implementation") // dependencies to copy by configuration name
    buildScriptConfigurations = setOf("classpath") // dependencies from classpath, runtime, etc
}
```
### Full Example
```kotlin 
repositories {
    mavenLocal()
    google()
    mavenCentral()
    maven(url = "https://repo.gradle.org/gradle/libs")
    jcenter()
}

offlineDependencies {
    setRepositoryHandler(project.repositories)
    root = File(project.buildDir.absolutePath, "offline") // will save to project_path/build/offline
    includeSources = true
    includeJavadocs = true
    includePoms = true
    includeIvyXmls = true
    includeBuildscriptDependencies = false

    configurations = setOf("compile", "implementation") // dependencies to copy by configuration name
    buildScriptConfigurations = setOf("classpath") // dependencies from classpath, runtime, etc
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(gradleTestKit())
    implementation("org.gradle:sample-check:0.7.0")
    implementation("junit:junit:4.12")
    implementation(kotlin("stdlib", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION))
    implementation("org.xmlunit:xmlunit-matchers:2.5.1")
}
```
 