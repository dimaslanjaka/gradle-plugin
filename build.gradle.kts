import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.12.0"
    kotlin("jvm") version "1.4.10"
}

apply {
    //plugin(org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin::class)
    from("publish.gradle")
}

group = "com.dimaslanjaka.android"
version = "1.0.0"

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

// cache
configurations.all {
    resolutionStrategy {
        cacheDynamicVersionsFor(4, "hours")
        cacheChangingModulesFor(4, "hours")
    }
}

dependencies {
    implementation(gradleApi());
    testImplementation(gradleTestKit())
    implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))

    //kotlin deps
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.10")

    // Additional
    compileOnly("org.jetbrains:annotations:20.1.0")
    compileOnly("com.android.tools.build:gradle:3.6.3")

    // JSON
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("joda-time:joda-time:2.10.9")
    implementation("org.reflections:reflections:0.9.12")
    implementation("org.jboss:jdk-misc:2.Final")

    // resource provider
    implementation("com.squareup:javapoet:1.10.0")
    implementation("com.squareup:kotlinpoet:1.6.0")

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
}

val jar: Jar by tasks
