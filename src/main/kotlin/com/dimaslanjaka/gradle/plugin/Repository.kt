package com.dimaslanjaka.gradle.plugin

import com.dimaslanjaka.kotlin.File
import gson
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import java.io.InputStream
import java.util.*

class Repository(p: Project) {
    var project: Project = p
    var app = FileResourcesUtils()
    var `is`: InputStream = app.getFileFromResourceAsStream("repo.md")
    var listRepos: MutableList<String> = FileResourcesUtils.getInputStream(`is`)

    init {
        if (p.subprojects.isNotEmpty()) {
            for (sp in p.subprojects) {
                initialize(sp)
            }
        }
        initialize(project)
    }

    private fun initialize(project: Project) {
        println("initialize repository ${project.name}")

        // add default repositories
        addDefault(project)

        // add repositories from resource
        for (repo in listRepos) {
            if (repo.startsWith("http")) {
                addRepo(project, repo.trim())
            }
        }

        project.gradle.buildFinished {
            val repositories = LinkedHashMap<String, String>()
            project
                .repositories
                .stream()
                .filter { repository: ArtifactRepository? -> repository is MavenArtifactRepository }
                .forEach { repository: ArtifactRepository ->
                    val mavenRepository = repository as MavenArtifactRepository
                    val name = mavenRepository.name.toLowerCase(Locale.ROOT)
                    val url = mavenRepository.url.toString()
                    repositories[name] = url

                    //println("$name=$url")
                }
            val logfile = File(
                project.buildDir.absolutePath, "plugin/com.dimaslanjaka/repositories-${project.name}"
            )
            logfile.write(gson().toJson(repositories))
        }
    }

    private fun addRepo(project1: Project, repo: String) {
        addRepoUrl(project1, repo)
    }

    private fun addDefault(project: Project?) {
        if (project != null) {
            project.repositories.add(project.repositories.mavenLocal())
            project.repositories.add(project.repositories.google())
            project.repositories.add(project.repositories.jcenter())
            project.repositories.add(project.repositories.mavenCentral())
        }
    }

    companion object {
        @JvmStatic
        fun addRepositories(project: Project, repositories: LinkedHashMap<String, String>) {
            project
                .repositories
                .stream()
                .filter { repository: ArtifactRepository? -> repository is MavenArtifactRepository }
                .forEach { repository: ArtifactRepository ->
                    val mavenRepository = repository as MavenArtifactRepository
                    val name = mavenRepository.name.toLowerCase(Locale.ROOT)
                    val url = mavenRepository.url.toString()
                    repositories[name] = url
                }
        }

        @JvmStatic
        fun addMavenRepo(proj: Project, name: String, url: String): MavenArtifactRepository {
            return proj.repositories.maven { repo ->
                repo.name = name
                repo.setUrl(url)
            }
        }

        @JvmStatic
        fun addRepoUrl(p: Project, url: String) {
            p.repositories.maven { mavenArtifactRepository ->
                mavenArtifactRepository.name = MD5.get(url)
                mavenArtifactRepository.setUrl(url)
            }
        }
    }
}