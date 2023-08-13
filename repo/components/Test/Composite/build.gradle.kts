plugins {
    id("com.dimaslanjaka.gradle.offline_dependencies")
    kotlin("jvm") version ("1.4.32")
    id("java")
}

repositories {
    mavenLocal()
    google()
    mavenCentral()
    maven(url = "https://repo.gradle.org/gradle/libs")
    
}

offlineDependencies {
    setRepositoryHandler(project.repositories)
    root = File(project.buildDir.absolutePath, "offline") // will save to project_path/build/offline
    includeSources = true
    includeJavadocs = true
    includePoms = true
    includeIvyXmls = true
    includeBuildscriptDependencies = false
    debug = false
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