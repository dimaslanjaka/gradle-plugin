package com.dimaslanjaka.gradle.repository

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.FlatDirectoryArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository
import org.gradle.api.internal.artifacts.repositories.DefaultMavenLocalArtifactRepository
import java.net.URI

class Resolver {
    companion object {
        lateinit var project: Project

        @JvmStatic
        fun resolve(rootProject: Project) {
            project = rootProject
            project.allprojects.forEach {
                addNew(it)
            }
        }

        @JvmStatic
        fun addNew(project: Project) {
            add(project.repositories.gradlePluginPortal())
            add(project.repositories.jcenter())
            add(project.repositories.mavenLocal())
            add(project.repositories.mavenCentral())
            add(project.repositories.maven {
                it.url = uri("https://maven.pkg.github.com/dimaslanjaka/gradle-plugin")
                it.credentials { c ->
                    c.username = "dimaslanjaka"
                    c.password = "d98acb59134fabab05145ddeb7abe4441db18b4a"
                }
            })

            add("http://backend.webmanajemen.com/artifact/")
            add("http://devrepo.kakao.com:8088/nexus/content/repositories/central/")
            add("http://esd4j.org/jabylon/repository/")
            add("http://maven2.javacpp.googlecode.com/git/")
            add("http://maven2.javacv.googlecode.com/git/")
            add("http://maven.51haoyayi.com:18081/nexus/content/repositories/jcenter/")
            add("http://nexus.openolat.org/nexus/content/repositories/public/")
            add("http://www.jabylon.org/maven/")
            add("https://archiva.interlocsolutions.com/archiva/repository/internal/")
            add("https://dl.bintray.com/android/android-tools")
            add("https://dl.bintray.com/jetbrains/kotlin-native-dependencies")
            add("https://dl.bintray.com/kodein-framework/Kodein-DI")
            add("https://dl.bintray.com/kotlin/kotlin-eap")
            add("https://dl.bintray.com/kotlin/kotlinx")
            add("https://dl.bintray.com/kotlin/ktor")
            add("https://dl.bintray.com/touchlabpublic/kotlin")
            add("https://dl.google.com/dl/android/maven2/")
            add("https://jcenter.bintray.com/")
            add("https://jetbrains.bintray.com/intellij-plugin-service")
            add("https://jitpack.io")
            add("https://kotlin.bintray.com/kotlinx")
            add("https://maven.atlassian.com/content/repositories/atlassian-public/")
            add("https://maven.fabric.io/public")
            add("https://maven.google.com")
            add("https://maven.google.com/")
            add("https://maven.mozilla.org/maven2/")
            add("https://maven.repository.redhat.com/ga/")
            add("https://maven.restlet.org")
            add("https://maven.springframework.org/release")
            add("https://nexus.xebialabs.com/nexus/content/groups/public/")
            add("https://oss.sonatype.org/content/repositories/releases/")
            add("https://packages.atlassian.com/maven-external/")
            add("https://plugins.gradle.org/m2/")
            add("https://repo1.maven.org/maven2")
            add("https://repo1.maven.org/maven2/")
            add("https://repo.gradle.org/gradle/libs-releases")
            add("https://repo.hortonworks.com/content/repositories/releases/")
            add("https://repo.maven.apache.org/maven2")
            add("https://repo.spring.io/libs-milestone/")
            add("https://repo.spring.io/libs-release/")
            add("https://repo.spring.io/milestone")
            add("https://repo.spring.io/plugins-release/")
            add("https://repo.spring.io/snapshot")
            add("https://repository.axelor.com/nexus/service/rest/repository/browse/maven-public/")
            add("https://repository.jboss.org/nexus/content/repositories/ea/")
            add("https://repository.jboss.org/nexus/content/repositories/releases/")
        }

        @JvmStatic
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

        @JvmStatic
        fun uri(url: String): URI {
            return URI(url)
        }

        @JvmStatic
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
}
