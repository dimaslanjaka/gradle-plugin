package com.dimaslanjaka.gradle.plugin

import com.dimaslanjaka.kotlin.File
import fixPath
import getHtml
import gson
import org.gradle.api.Project
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.net.URL
import java.util.regex.Pattern
import java.io.File as javaFile


class OfflineRemote {
    val remotes = mutableListOf(
        "http://archiva.temasys.com.sg/repository/internal/",
        "https://nexus.xebialabs.com/nexus/content/groups/public/com/android/",
        "https://xjrccb.club:9444/nexus/content/groups/public/",
        "https://www.aht-group.com/nexus/content/groups/public/",
        "https://repo.sovrin.org/service/rest/repository/browse/maven-public/",
        "https://nexus.neomind.com.br/nexus/service/rest/repository/browse/neomind-jboss/"
    )
    lateinit var project: Project

    constructor() {}
    constructor(p: Project) {
        this.project = p
        p.allprojects.forEach { ap ->
            ap.afterEvaluate { apAE ->
                apAE.repositories.forEach { repo ->
                }
            }
        }
    }

    fun checkLocal() {
    }

    val walkResult = WalkResult()
    fun walk(path: String): WalkResult {
        val root = File(path)
        val list = root.listFiles()
        for (f in list) {
            if (f.isDirectory) {
                walk(f.absolutePath)
                //println("Dir:" + f.absoluteFile)
                walkResult.dirs.add(f)
            } else {
                //println("File:" + f.absoluteFile)
                walkResult.files.add(f)
            }
        }
        return walkResult
    }

    fun convertDomain(string: String): String {
        val buildDomain = mutableListOf<String>()
        val split = string.split(javaFile.separator).toMutableList()
        split.forEach {
            val regex = "^\\d"
            val pattern = Pattern.compile(regex, Pattern.MULTILINE);
            val matcher = pattern.matcher(it);
            if (!matcher.find() && !it.contains(".")) {
                buildDomain.add(it)
            }
        }

        var domain = buildDomain.joinToString(".")
        if (domain.startsWith(".")) {
            domain = domain.substring(1)
        }
        return domain
    }

    class WalkResult {
        val dirs = mutableListOf<File>()
        val files = mutableListOf<File>()
    }

    fun fetch(): List<Off1> {
        val res = mutableListOf<Off1>()
        walk(Core.extension.localRepository.absolutePath).files.forEach { artifact ->
            if (artifact.name.endsWith(".pom")) {
                var allowToDownload = true
                // if in directory contains jar file, skip re-download from remote
                artifact.parentFile.listFiles()?.map { files ->
                    files?.let {
                        if (it.name.endsWith(".jar")) {
                            allowToDownload = false
                        }
                    }
                }

                if (allowToDownload) {
                    val off1 = Off1()
                    val removeMaven = artifact.parentFile.absolutePath
                        .replace(Core.extension.localRepository.absolutePath, "")
                    var maven = removeMaven.replace(javaFile.separator, "/")
                    if (maven.startsWith("/")) {
                        maven = maven.substring(1)
                    }
                    off1.path = maven
                    off1.domain = convertDomain(removeMaven)

                    remotes.shuffled().forEach { remote ->
                        val url = URL("${remote}/${maven}")
                        off1.url = url
                        off1.repository = URL(remote)
                    }
                    res.add(off1)
                }
            }
        }
        return res.distinct()
    }

    class Off1 {
        lateinit var urljars: List<URL>
        lateinit var url: URL
        lateinit var domain: String
        lateinit var path: String
        lateinit var repository: URL

        override fun toString(): String {
            return gson().toJson(this)
        }
    }

    fun downloadFetch() {
        val downloadedArtifacts = mutableListOf<Any>()
        fetch().forEach { off1 ->
            val keyDownloadedArtifact = off1.domain
            if (!downloadedArtifacts.contains(keyDownloadedArtifact)) {
                downloadedArtifacts.add(keyDownloadedArtifact)
                println("Fetch ${off1.url.fixPath()}")
                val fetchUrls = fetchURLJars(off1)
                off1.urljars = fetchUrls
            }
        }
    }

    fun fetchURLJars(off1: Off1): List<URL> {
        val result = mutableListOf<URL>()
        try {
            val get = off1.url.fixPath().getHtml()
            if (get.code == 200) {
                val doc = Jsoup.parse(get.html.toString())
                val hyprelinks: Elements? = doc.select("a")
                hyprelinks?.let { hyperlink ->
                    hyperlink.forEach { a ->
                        val href = a.attr("href").toString()
                        if (href.endsWith(".aar") || href.endsWith(".jar")) {
                            var urljar = URL(href)
                            if (!href.startsWith("http")) {
                                urljar = URL("${off1}/${href}").fixPath()
                            }
                            result.add(urljar)
                        }
                    }
                }
            } else {
                println("\t${get.code}")
            }
        } catch (e: Exception) {
            //e.printStackTrace()
        }
        return result.distinct()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Core.extension = CoreExtension(File("build/test-results/${javaClass.name}").createGradleProject())
            val off = OfflineRemote()
            val off1 = off.downloadFetch()
            println(off1)
            /*
            listOf(
                URL("http://archiva.temasys.com.sg/repository/internal//androidx/collection/collection/1.1.0/"),
                URL("https://repo.sovrin.org/service/rest/repository/browse/maven-public/com/android/tools/build/transform-api/2.0.0-deprecated-use-gradle-api/"),
                URL("https://nexus.neomind.com.br/nexus/service/rest/repository/browse/neomind-jboss/com/android/tools/lint/lint-checks/22.2.1/")
            ).map { off.downloadURL(it) }
             */
        }
    }
}