package com.dimaslanjaka.gradle.plugin

import com.dimaslanjaka.gradle.api.Extension
import com.dimaslanjaka.kotlin.ConsoleColors
import com.dimaslanjaka.kotlin.File
import com.google.gson.GsonBuilder
import isAndroid
import org.gradle.api.Project
import println
import java.nio.file.Path
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import com.dimaslanjaka.gradle.offline_dependencies.Extension as OfflineDependenciesExtension
import com.dimaslanjaka.gradle.offline_dependencies.Plugin as OfflineDependenciesPlugin
import java.io.File as javaFile

/**
 * Cache artifact based on project configurations
 */
class Offline3(p: Project) {
    private val configuration = Extension.get<CoreExtension>(
        p, Core.CONFIG_NAME
    ) as CoreExtension
    var debug: Boolean = configuration.debug
    private val allArtifacts = mutableListOf<javaFile>()
    private val cachedArtifacts = mutableListOf<javaFile>()
    private val nonCachedArtifacts = mutableListOf<javaFile>()

    init {
        if (configuration.offline3) {
            p.allprojects.forEach { subproject ->
                subproject?.let { sp ->
                    activate(sp)
                }
            }
        }
    }

    private fun activate(sp: Project) {
        val off = OfflineDependenciesPlugin()
        val handler = off.createRepositoryHandler(sp)
        val ext = OfflineDependenciesExtension(sp, handler)
        ext.root = configuration.root
        ext.buildScriptConfigurations = configuration.buildScriptConfigurations
        ext.configurations = configuration.configurations
        ext.debug = configuration.debug
        ext.project = sp

        ext.includeBuildscriptDependencies = configuration.includeBuildscriptDependencies
        ext.includeIvyXmls = configuration.includeIvyXmls
        ext.includeJavadocs = configuration.includeJavadocs
        ext.includePoms = configuration.includePoms
        ext.includeSources = configuration.includeSources

        off.apply(sp, ext)
    }

    @Suppress("FunctionName")
    fun startCache_old(project: Project) {
        val logfile = File(
            project.buildDir.absolutePath,
            "plugin/com.dimaslanjaka/offline3-${project.name}"
        )
        logfile.resolveParent()
        logfile.write("(Project=${project.name}) (Android=${project.isAndroid()})\n")
        logfile.appendText("Cache start on ${Date()}\n\n")

        project.afterEvaluate { projectAE ->
            val configurations = projectAE.configurations
            configurations.collectionSchema.elements.forEach { schema ->
                val cname = configurations.getByName(schema.name)
                if (cname.isCanBeResolved) { // only process resolved configuration
                    if (debug) {
                        println("Resolved Configuration Schema Of ${schema.name}")
                    }
                    logfile.appendText("\n+Resolved Configuration Schema Of ${schema.name}\n")
                    cname.map { configurationSchema ->
                        configurationSchema?.let { artifact ->
                            val logtxt = StringBuilder("\t")
                            if (artifact.exists()) {
                                allArtifacts.add(javaFile(artifact.absolutePath))
                                if (!artifact.absolutePath.contains(".m2")) {
                                    logtxt.append("Non-Cached -> $artifact")
                                    nonCachedArtifacts.add(javaFile(artifact.absolutePath))
                                } else {
                                    logtxt.append("Cached -> $artifact")
                                    cachedArtifacts.add(javaFile(artifact.absolutePath))
                                }
                            }
                            logtxt.append("\n")
                            logfile.appendText(logtxt.toString())
                        }
                    }
                } else {
                    logfile.appendText("\n-Couldn't Resolve Configuration Schema Of ${schema.name}\n")
                }
            }

            logfile.appendText("\n")

            nonCachedArtifacts.forEach { artifact ->
                val maven = convert2MavenLocal(artifact.absolutePath)
                if (debug) {
                    println("${artifact.normalize()} \n\t-> $maven")
                }

                try {
                    artifact.copyTo(maven.toFile(), true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                logfile.appendText("Copy:\n\t${artifact.normalize()} \n\t-> ${maven}\n")
            }

            println(ConsoleColors.randomColor("Cache Offline3 Log Saved On $logfile"))
        }
    }

    fun convert2MavenLocal(artifactStr: String): Path {
        val home = System.getProperty("user.home")
        val localRepository = Core.extension.getLocalRepository().absolutePath
        val gradleCache = File(home, ".gradle/caches/modules-2/files-2.1")
        val replace = File(artifactStr.replace(gradleCache.absolutePath, ""))
        val builder = StringBuilder(localRepository)
        replace.normalizeAsString().split(javaFile.separator.toString()).forEach { string ->
            if (string.length < 35) {
                if (isDomain(string) && !isVersion(string) && !Core.extension.validExtension(string)) {
                    // if domain format and not version format and not filename, split dot to slash path separator
                    builder.append(javaFile.separator)
                        .append(string.replace(".", javaFile.separator))
                } else {
                    builder.append(javaFile.separator).append(string)
                }
            }
        }

        if (debug) {
            listOf<Any>(
                ConsoleColors.red(artifactStr),
                ConsoleColors.green(replace.normalize().toString()),
                ConsoleColors.underline(javaFile.separator.toString())
            ).println()
        }

        return File(builder.toString()).normalize()
    }

    fun isDomain(string: String): Boolean {
        val regex =
            "^(((?!-))(xn--|_{1,1})?[a-z0-9-]{0,61}[a-z0-9]{1,1}\\.)*(xn--)?([a-z0-9][a-z0-9\\-]{0,60}|[a-z0-9-]{1,30}\\.[a-z]{2,})$"
        val pattern: Pattern = Pattern.compile(regex, Pattern.MULTILINE)
        val matcher: Matcher = pattern.matcher(string)
        val match = matcher.find()
        if (match && debug) {
            println("Domain: " + matcher.group(0))
        }
        return match
    }

    fun isVersion(string: String): Boolean {
        val regex =
            "^(\\d+\\.)?(\\d+\\.)?(\\*|\\d+)\$|^(\\d+\\.)?(\\d+\\.)?([a-zA-Z\\d-]+)\$|^(\\d+\\.)?(\\d+\\.)?(\\d+\\.)?([a-zA-Z]+)\$"
        val pattern: Pattern = Pattern.compile(regex, Pattern.MULTILINE)
        val matcher: Matcher = pattern.matcher(string)
        val match = matcher.find()
        if (match && debug) {
            println("Version: " + matcher.group(0))
        }
        return match
    }

    class ArtifactCache {
        lateinit var from: File
        lateinit var to: File

        constructor()
        constructor(gradleCache: File, mavenCache: File) {
            from = gradleCache
            to = mavenCache
        }

        override fun toString(): String {
            return GsonBuilder().setPrettyPrinting()
                .disableHtmlEscaping().create().toJson(this)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

        }
    }
}