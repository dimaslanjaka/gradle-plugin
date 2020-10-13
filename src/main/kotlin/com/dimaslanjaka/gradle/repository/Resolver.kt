package com.dimaslanjaka.gradle.repository

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.FlatDirectoryArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository
import org.gradle.api.internal.artifacts.repositories.DefaultMavenLocalArtifactRepository
import java.net.URI

class Resolver(rootProject: Project) {
    val project = rootProject

    init {
        add(project.repositories.gradlePluginPortal())
        add(project.repositories.jcenter())
        add(project.repositories.mavenLocal())
        add(project.repositories.mavenCentral())
        add("https://dl.google.com/dl/android/maven2/")
        add("https://dl.bintray.com/kotlin/kotlinx")
        add("https://dl.bintray.com/kotlin/ktor")
        add("https://dl.bintray.com/jetbrains/kotlin-native-dependencies")
        add("https://dl.bintray.com/kotlin/kotlin-eap")
        add("https://dl.bintray.com/kodein-framework/Kodein-DI")
        add("https://dl.bintray.com/touchlabpublic/kotlin")
        add("https://maven.mozilla.org/maven2/")
        add("https://repository.jboss.org/nexus/content/repositories/ea/")
        add("https://plugins.gradle.org/m2/")
        add("https://maven.springframework.org/release")
        add("https://maven.restlet.org")
        add("https://repo.spring.io/snapshot")
        add("https://repo.spring.io/milestone")
        add("https://jitpack.io")
        add("https://repo1.maven.org/maven2/")
        add("https://jcenter.bintray.com/")
        add("https://maven.google.com")
        add("https://repo.gradle.org/gradle/libs-releases")
        add("https://dl.bintray.com/android/android-tools")
        add("https://repository.jboss.org/nexus/content/repositories/releases/")
        add("https://maven.fabric.io/public")
        add("https://oss.sonatype.org/content/repositories/releases/")
        add("https://repo.spring.io/plugins-release/")
        add("https://maven.atlassian.com/content/repositories/atlassian-public/")
        add("https://repo.spring.io/libs-milestone/")
        add("https://repo.hortonworks.com/content/repositories/releases/")
        add("https://repo.spring.io/libs-release/")
        add(project.repositories.maven {
            it.url = uri("https://maven.pkg.github.com/dimaslanjaka/gradle-plugin")
            it.credentials { c ->
                c.username = "dimaslanjaka"
                c.password = "d98acb59134fabab05145ddeb7abe4441db18b4a"
            }
        })
        add("http://backend.webmanajemen.com/artifact/")
    }

    fun add(repo: Any) {
        when (repo) {
            is MavenArtifactRepository -> {
                project.repositories.add(repo)
                Core.repositories.add(repo.url.toString())
            }
            is ArtifactRepository -> {
                project.repositories.add(repo)
            }
            is FlatDirectoryArtifactRepository -> {
                project.repositories.add(repo)
            }
            is String -> {
                project.repositories.add(project.repositories.maven {
                    it.url = uri(repo)
                    Core.repositories.add(repo)
                })
            }
        }
    }

    fun uri(url: String): URI {
        return URI(url)
    }

    fun copyScriptRepositories(project: Project) {
        for (repo in project.rootProject.buildscript.repositories) {
            if (repo is DefaultMavenLocalArtifactRepository) {
                project.logger.info("[CubaPlugin] using repository mavenLocal()")
                project.repositories.mavenLocal()
            } else if (repo is DefaultMavenArtifactRepository) {
                // we copy only http / https repositories using standard way:
                if (repo.url.scheme == "http" || repo.url.scheme == "https") {
                    project.logger.info("[CubaPlugin] using repository $repo.name at $repo.url with credentials")
                    val mavenCredentials = repo.credentials
                    project.repositories.maven {
                        it.url = repo.url
                        it.credentials { cred ->
                            cred.username = (mavenCredentials.username ?: "")
                            cred.password = (mavenCredentials.password ?: "")
                        }
                    }
                } else if (repo.url.scheme == "file") {
                    project.logger.info("[CubaPlugin] using repository $repo.name at $repo.url")

                    // copy without authentication
                    project.repositories.maven {
                        it.url = repo.url
                    }
                } else {
                    project.logger.info("[CubaPlugin] using repository $repo.name at $repo.url as fallback")
                    // fallback
                    project.repositories.add(repo)
                }
            } else {
                project.logger.info("[CubaPlugin] using repository ${repo.name}")
                //project.logger.info("[CubaPlugin] using repository $repo.name" + (repo.hasProperty("url") ? " at $repo.url" : ""))
                // fallback
                project.repositories.add(repo)
            }
        }
    }
}
