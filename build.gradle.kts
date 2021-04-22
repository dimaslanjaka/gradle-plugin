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
    //`kotlin-dsl`
    //`java-gradle-plugin`
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "0.12.0"
    kotlin("jvm") version "1.4.32"
}

apply {
    from("build.test.gradle")
}

group = "com.dimaslanjaka"
version = getVersionPref(project)["version"]!!
description = "Transform gradle artifacts to maven local repository, for available offline with mavenLocal"

allprojects {
    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(arrayOf("-parameters", "-Xdoclint:none", "-Xlint:all"))
        options.isIncremental = true
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }

    tasks.withType<KotlinCompile>().all {
        //println("Configuring $name in project ${project.name}...")
        kotlinOptions {
            suppressWarnings = true
            jvmTarget = JavaVersion.VERSION_1_8.toString()
            javaParameters = true
            allWarningsAsErrors = false
        }
        incremental = true
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
}

sourceSets {
    getByName("main") {
        java.srcDir("src/main/groovy")
        java.srcDir("src/main/java")
        java.srcDir("src/main/kotlin")
    }
    getByName("test") {
        java.srcDir("src/test/java")
        java.srcDir("src/test/kotlin")
    }
}

// cache
configurations.all {
    resolutionStrategy {
        cacheDynamicVersionsFor(4, "hours")
        cacheChangingModulesFor(4, "hours")
    }
}

@Suppress("GradleDependency") dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    val offlineLib = File(projectDir, "repo/components/build/libs")
    implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
    //implementation(fileTree(mapOf("dir" to offlineLib, "include" to listOf("*.jar"))))

    //Test
    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.13")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.7.1")
    //testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    //kotlin deps
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.32")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.32")
    //implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.32")
    //implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.32")

    compileOnly("org.jetbrains:annotations:16.0.2")
    compileOnly("com.android.tools.build:gradle:7.0.0-alpha03")
    implementation("com.google.code.gson:gson:2.8.5")
    //implementation("com.squareup:javapoet:1.10.0")
    //implementation("com.squareup:kotlinpoet:1.0.0-RC1")
    implementation("joda-time:joda-time:2.10.9")
    //implementation("org.reflections:reflections:0.9.12")
    //implementation("org.jboss:jdk-misc:2.Final")

    // package relocator
    //implementation("com.googlecode.jarjar:jarjar:1.3")
    //implementation("org.apache.maven:maven-model-builder:3.6.3")

    // Apache
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.apache.commons:commons-collections4:4.3")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("org.apache.commons:commons-compress:1.20")
    implementation("org.apache.commons:commons-exec:1.3")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("commons-codec:commons-codec:1.9")
    implementation("commons-net:commons-net:3.6")
    implementation("commons-validator:commons-validator:1.7")
    implementation("commons-io:commons-io:2.8.0")
    implementation("commons-cli:commons-cli:1.4")
    implementation("xerces:xercesImpl:2.12.0")
    implementation("org.apache.cxf:cxf-common-utilities:2.5.11")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val jar: Jar by tasks
// compile jar with dependencies
jar.from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
jar.isZip64 = true

val rootLibs = File("${project.rootProject.rootDir}/../lib/").absolutePath
val libtarget = File(rootLibs, "gradle-plugin.jar")
jar.doLast {
    copy {
        val dari = jar.archivePath
        // copy if source size is different from target
        val compares = libtarget.exists() && dari.length().compareTo(libtarget.length()) != 0
        if (compares || !libtarget.exists()) {
            from(dari)
            into(rootLibs)
            rename { fileName ->
                fileName.replace("gradle-plugin-${project.version}", "gradle-plugin")
            }
            println("Copy\n\tf: $dari \n\tt: $rootLibs")
        }
    }
}

tasks.findByName("publishPlugins")?.doLast {
    updateVersionPref(project)
}

tasks {
    val fatJar by creating(Jar::class) {
        val jarname = "gradle-plugin-with-dependencies.jar"
        description = "create jar with dependencies"
        isZip64 = true
        group = "build"

        // set output file
        archiveFileName.set(jarname)
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
    }
    val updateVersion by creating {
        group = "build"
        description = "Increase Version Manual"
        doLast {
            updateVersionPref(project)
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
    project.version = getVersionPref(project)["version"]!!

    // save new version
    getVersionPref(project, project.version.toString())

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
    }

    return properties
}

