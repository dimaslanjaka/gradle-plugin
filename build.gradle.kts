import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.prefs.Preferences

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
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

plugins {
    id("groovy")
    id("maven-publish")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "0.12.0"
    kotlin("jvm") version "1.4.10"
}

val preferences = Preferences.userRoot().node("build/android-plugin")
if (preferences.get("version", "").isEmpty()) {
    println("Set first version: 1.0.0")
    preferences.put("version", "1.0.0")
}

group = "com.dimaslanjaka"
version = preferences.get("version", "1.0.0")

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
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        suppressWarnings = true
    }
}

sourceSets {
    getByName("main") {
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

// compile jarjar in kotlin dsl
val jarjar by configurations.creating
val thirdparty by configurations.creating

dependencies {
    implementation(gradleApi());
    testImplementation(gradleTestKit())
    implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))

    //kotlin deps
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.10")

    compileOnly("org.jetbrains:annotations:20.1.0")
    compileOnly("com.android.tools.build:gradle:7.0.0-alpha03")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.squareup:javapoet:1.10.0")
    implementation("com.squareup:kotlinpoet:1.0.0-RC1")
    implementation("joda-time:joda-time:2.10.9")
    implementation("org.reflections:reflections:0.9.12")
    implementation("org.jboss:jdk-misc:2.Final")

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


java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    //val files: Set<File> = sourceSets["main"].java.srcDirs
    //println(files)
}

gradlePlugin {
    plugins {
        register("android-plugin") {
            id = "com.dimaslanjaka.android"
            implementationClass = "com.dimaslanjaka.gradle.plugin.Core"
        }
    }
}

// publishing
pluginBundle {
    // These settings are set for the whole plugin bundle
    website = "https://webmanajemen.com/"
    vcsUrl = "https://github.com/dimaslanjaka/android-plugin"

    // tags and description can be set for the whole bundle here, but can also
    // be set / overridden in the config for specific plugins
    description = "Transform gradle artifacts to maven local repository, for available offline with mavenLocal()!"

    // The plugins block can contain multiple plugin entries.
    //
    // The name for each plugin block below (greetingsPlugin, goodbyePlugin)
    // does not affect the plugin configuration, but they need to be unique
    // for each plugin.

    // Plugin config blocks can set the id, displayName, version, description
    // and tags for each plugin.

    // id and displayName are mandatory.
    // If no version is set, the project version will be used.
    // If no tags or description are set, the tags or description from the
    // pluginBundle block will be used, but they must be set in one of the
    // two places.

    (plugins) {
        "android-plugin" {
            // id is captured from java-android-plugin configuration
            displayName = "Gradle Android Plugin"
            description =
                "Tools and helpers for android projects"
            tags = listOf("android", "contentprovider", "string", "tools", "helper", "auto")
            version = project.version as String
        }
    }

    // Optional overrides for Maven coordinates.
    // If you have an existing plugin deployed to Bintray and would like to keep
    // your existing group ID and artifact ID for continuity, you can specify
    // them here.
    //
    // As publishing to a custom group requires manual approval by the Gradle
    // team for security reasons, we recommend not overriding the group ID unless
    // you have an existing group ID that you wish to keep. If not overridden,
    // plugins will be published automatically without a manual approval process.
    //
    // You can also override the version of the deployed artifact here, though it
    // defaults to the project version, which would normally be sufficient.

    mavenCoordinates {
        groupId = "com.dimaslanjaka"
        artifactId = "android-plugin"
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

tasks.findByName("publishPlugins")?.doLast {
    // TODO: auto update version
    val v = preferences.get("version", "1.0.0").toString()
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
    preferences.put("version", token.joinToString("."))
    project.version = preferences.get("version", project.version.toString())

    val map = mutableMapOf<String, Any>()
    map["main"] = token[0]
    map["major"] = token[1]
    map["minor"] = token[2]
    map["version"] = token
    println(map)
}
