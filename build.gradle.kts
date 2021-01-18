plugins {
    id("com.gradle.plugin-publish") version "0.12.0"
    groovy
    `kotlin-dsl`
    `java-gradle-plugin`
    //id("com.android.library") apply false
    //id("org.jetbrains.kotlin.jvm").version("1.3.72")
    //"java"
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
    jcenter()
}

dependencies {
    //implementation localGroovy()
    //implementation gradleApi()
    //compile("org.gradle:gradle-core:2.14.1")
    //implementation("org.codehaus.groovy:groovy-all:3.0.7")
    compileOnly("org.jetbrains:annotations:20.1.0")
    //compileOnly("com.android.tools.build:gradle:7.0.0-alpha03")
    //implementation("com.squareup:javapoet:1.10.0")
    //implementation("com.squareup:kotlinpoet:1.0.0-RC1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    //testImplementation("org.jetbrains.kotlin:kotlin-test")
    //testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    testImplementation(gradleTestKit())
    implementation(kotlin("gradle-plugin"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

gradlePlugin {
    plugins {
        register("gradle-plugin") {
            id = "com.dimaslanjaka"
            implementationClass = "com.dimaslanjaka.gradle.plugin.Core"
        }
    }
}

configure<KotlinDslPluginOptions> {
    experimentalWarning.set(false)
}

// publishing
pluginBundle {
    // These settings are set for the whole plugin bundle
    website = "https://webmanajemen.com/"
    vcsUrl = "https://github.com/dimaslanjaka/gradle-plugin"

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
        "gradle-plugin" {
            // id is captured from java-gradle-plugin configuration
            displayName = "Gradle2Maven plugin"
            description = "Transform gradle artifacts to maven local repository, for available offline with `mavenLocal()`"
            tags = listOf("offline", "auto", "optimize", "performance")
            version = "1.0.0"
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
        artifactId = "gradle-plugin"
        version = "1.0.0"
    }
}