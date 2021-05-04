import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileReader
import java.io.FileWriter
import java.util.*

repositories {
    mavenLocal()
    mavenCentral()
    google()
    jcenter()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
    maven {
        url = uri("https://maven.springframework.org/release")
    }
    maven {
        url = uri("https://maven.restlet.com")
    }
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

plugins {
    id("groovy")
    id("java")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "0.12.0"
    kotlin("jvm") version "1.4.32"
}
//println("fix sync intellij idea not showing")
apply {
    from("build.test.gradle")
}

group = "com.dimaslanjaka"
version = getVersionPref(project)["version"]!!
description = "Transform gradle artifacts to maven local repository, for available offline with mavenLocal"
println("${project.name} using version ${project.version}")

sourceSets {
    getByName("main") {
        java.srcDir("${project.projectDir}/src/main/groovy")
        java.srcDir("${project.projectDir}/src/main/java")
        java.srcDir("${project.projectDir}/src/main/kotlin")
    }
    getByName("test") {
        java.srcDir("${project.projectDir}/src/test/java")
        java.srcDir("${project.projectDir}/src/test/kotlin")
    }
}

// cache
configurations.all {
    resolutionStrategy {
        cacheDynamicVersionsFor(4, "hours")
        cacheChangingModulesFor(4, "hours")
    }
}

val offlineLib = File(projectDir, "repo/components/build/libs")
@Suppress("GradleDependency")
dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
    implementation(fileTree(mapOf("dir" to offlineLib, "include" to listOf("*.jar"))))

    //Test
    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.13")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    //testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    //testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.7.1")
    //testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    //kotlin deps
    //implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.32")
    //implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.32")
    //implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.32")
    //implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.32")

    compileOnly("org.jetbrains:annotations:16.0.2")
    //compileOnly("com.android.tools.build:gradle:7.0.0-alpha03")
    implementation("com.google.code.gson:gson:2.8.5")
    //implementation("com.squareup:javapoet:1.10.0")
    //implementation("com.squareup:kotlinpoet:1.0.0-RC1")
    implementation("joda-time:joda-time:2.10.9")
    //implementation("org.reflections:reflections:0.9.12")
    //implementation("org.jboss:jdk-misc:2.Final")
    implementation("org.jsoup:jsoup:1.13.1")

    // package re-locator
    //implementation("com.googlecode.jarjar:jarjar:1.3")
    //implementation("org.apache.maven:maven-model-builder:3.6.3")

    // Spring
    implementation("org.springframework:spring-core:5.2.14.RELEASE")

    // jsonschema2pojo
    // Annotation
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    // Required if generating JSR-303 annotations
    implementation("javax.validation:validation-api:1.1.0.CR2")
    // Required if generating Jackson 2 annotations
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.1")
    // Required if generating JodaTime data types
    implementation("joda-time:joda-time:2.2")

    // Apache
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("commons-validator:commons-validator:1.7")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("commons-collections:commons-collections:3.2.2")
    /*
    implementation("commons-io:commons-io:2.8.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.apache.commons:commons-collections4:4.3")
    implementation("org.apache.commons:commons-compress:1.20")
    implementation("org.apache.commons:commons-exec:1.3")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("commons-codec:commons-codec:1.9")
    implementation("commons-net:commons-net:3.6")
    implementation("commons-cli:commons-cli:1.4")
    implementation("xerces:xercesImpl:2.12.0")
    implementation("org.apache.cxf:cxf-common-utilities:2.5.11")
     */
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<Jar>().configureEach {
    archiveBaseName.set("gradle-plugin")

    manifest {
        attributes["Implementation-Title"] = project.description as String
        attributes["Implementation-Version"] = project.version as String
        attributes["Implementation-Vendor"] = "Dimas Lanjaka"
        attributes["Created-By"] =
            "${System.getProperty("java.version")} (${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")})"
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(arrayOf("-parameters", "-Xdoclint:none", "-Xlint:all"))
    options.isIncremental = true
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        suppressWarnings = true
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        javaParameters = true
        allWarningsAsErrors = false
    }
    incremental = true
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

tasks.named<AbstractCompile>("compileGroovy") {
    // Groovy only needs the declared dependencies
    // (and not longer the output of compileJava)
    classpath = sourceSets.main.get().compileClasspath
}
tasks.named<AbstractCompile>("compileKotlin") {
    // Java also depends on the result of Groovy compilation
    // (which automatically makes it depend of compileGroovy)
    classpath += files(sourceSets.main.get().withConvention(GroovySourceSet::class) { groovy }.classesDirectory)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_9
    targetCompatibility = JavaVersion.VERSION_1_9
    withJavadocJar()
    withSourcesJar()
}

gradlePlugin {
    plugins {
        register("gradle-plugin") {
            id = "com.dimaslanjaka"
            implementationClass = "com.dimaslanjaka.gradle.plugin.Core"
        }
    }
}

// publishing
pluginBundle {
    website = "https://github.com/dimaslanjaka/gradle-plugin"
    vcsUrl = "https://github.com/dimaslanjaka/gradle-plugin"
    description = project.description
    (plugins) {
        "gradle-plugin" {
            displayName = project.name
            description = project.description
            tags = listOf("offline", "auto", "optimize", "performance")
            version = project.version as String
        }
    }

    mavenCoordinates {
        groupId = "com.dimaslanjaka"
        artifactId = "gradle-plugin"
        version = project.version as String
    }
}

val groovydoc: Groovydoc by tasks
val javadoc: Javadoc by tasks
val jar: Jar by tasks
jar.doLast {
    // TODO: create jar without version
    val jarnoversion = File(jar.archiveFile.get().asFile.parent, "gradle-plugin.jar")
    jar.archiveFile.get().asFile.copyTo(jarnoversion, true)
}

// compile jar with dependencies
offlineLib.listFiles()?.forEach { offlineJar ->
    offlineJar?.let { fileJar ->
        if (fileJar.name.contains("sources")) {
            // TODO: aggregating documentation groovy
            groovydoc.apply {
                source(zipTree(fileJar))
                include("**/*.groovy")
            }
            // TODO: aggregating documentation java
            javadoc.apply {
                source(zipTree(fileJar))
                include("**/*.java")
                (options as StandardJavadocDocletOptions).apply {
                    if (JavaVersion.current().isJava9Compatible) {
                        addBooleanOption("html5", true)
                    } else {
                        addBooleanOption("html4", true)
                    }
                    if (JavaVersion.current().isJava8Compatible) {
                        addStringOption("Xdoclint:none", "-quiet")
                    }
                }
            }
        } else if (!fileJar.name.contains("doc")) {
            // TODO: aggregating jar source, skip documentation and sources
            jar.from(zipTree(fileJar)) {
                include("**")
                exclude("META-INF", "META-INF/**")
            }
        }
    }
}

tasks.findByName("publishPlugins")?.doLast {
    updateVersionPref(project)
}

@Suppress("UNUSED_VARIABLE", "unused")
tasks {
    val fatJar by creating(Jar::class) {
        description = "create jar with dependencies"
        isZip64 = true
        group = "build"

        // set output file
        archiveFileName.set("gradle-plugin-wd.jar")

        //destinationDirectory.set(File(project.rootDir, "../javafx/libs").absoluteFile)

        // set manifest jar
        manifest.attributes.apply {
            put("Main-Class", "com.dimaslanjaka.gradle.plugin.Core")
            put("Implementation-Title", "Gradle Kotlin DSL (${project.name})")
            put("Implementation-Version", archiveVersion.get())
        }
        excludes.add("META-INF/**")

        from(configurations.compileClasspath.map { config ->
            config.map { mapit ->
                if (mapit.isDirectory) {
                    mapit
                } else {
                    zipTree(mapit)
                }
            }
        }) {
            exclude("META-INF", "META-INF/**")
        }
        with(jar.get())
        dependsOn(jar)
    }
    val updateVersion by creating(Task::class) {
        group = "project"
        description = "Increase Version Manual"
        doLast {
            updateVersionPref(project)
        }
    }
    val clearCacheTest by creating(Task::class) {
        group = "project"
        description = "clear all IDE configuration and Gradle Cache from tests project folders"
        doLast {
            listOf(
                File(project.rootDir.absolutePath, "Test"),
                File(project.rootDir.absolutePath, "repo/components/Test")
            ).forEach { testFolder ->
                testFolder.listFiles()?.forEach { file ->
                    if (file.isDirectory) {
                        file.listFiles()?.forEach { testUnit ->
                            if (testUnit.isDirectory) {
                                listOf(".gradle", ".idea").map {
                                    if (testUnit.name == it) {
                                        if (testUnit.deleteRecursively()) {
                                            println(testUnit.absolutePath + " Deleted Successfully")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
//jar.dependsOn("fatJar")

fun updateVersionPref(project: Project) {
    println("Updating version")

    // TODO: auto update version after publish plugin
    val v = getVersionPref(project)["version"].toString()
    val token = v.split(".").map { it.toInt() } as MutableList
    // increase minor
    token[2] = token[2] + 1
    // if minor has max value, increase major
    if (token[2] >= Int.MAX_VALUE) {
        token[1] = token[1] + 1
        token[2] = 0
    }
    // if major has max value, increase main
    if (token[1] >= Int.MAX_VALUE) {
        token[0] = token[0] + 1
        token[1] = 0
    }
    // apply version
    getVersionPref(project)["version"] = token.joinToString(".")
    project.version = token.joinToString(".")

    // save new version
    getVersionPref(project, token.joinToString("."))

    val map = mutableMapOf<String, Any>()
    map["main"] = token[0]
    map["major"] = token[1]
    map["minor"] = token[2]
    map["version"] = token
    //println(map)

    println("Updated version to ${map["version"]}")
}

fun getVersionPref(project: Project, newVersion: String? = null): Properties {
    // TODO: Set result properties
    val fileVersion = File(project.projectDir, "version.properties")
    if (!fileVersion.exists()) fileVersion.createNewFile()
    val reader = FileReader(fileVersion)
    val properties = Properties()
    properties.load(reader)

    if (!properties.containsKey("version") || (newVersion != null && newVersion.isNotEmpty())) {
        // TODO: if newVersion is not null or properties not have version property, set and save them
        properties["version"] = newVersion ?: "1.0.0"
        properties.store(FileWriter(fileVersion), "Gradle Plugin Version")
        println("Version Updated Successfully")
    }

    return properties
}

fun isSameFileSize(file1: File, file2: File): Boolean {
    return if (!file1.exists() || !file2.exists()) {
        false
    } else {
        file1.length().compareTo(file2.length()) == 0
    }
}
