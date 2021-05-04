package com.dimaslanjaka.gradle.plugin

import com.dimaslanjaka.gradle.api.Extension
import com.dimaslanjaka.kotlin.ConsoleColors
import com.dimaslanjaka.kotlin.File
import isAndroid
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.internal.artifacts.DefaultModuleIdentifier
import org.gradle.internal.component.external.model.DefaultModuleComponentIdentifier
import org.gradle.util.GFileUtils
import println
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.io.File as javaFile


/**
 * Cache artifact based on project configurations
 */
open class Offline3(p: Project) {
    private val configuration = Extension.get<CoreExtension>(
        p, Core.CONFIG_NAME
    ) as CoreExtension
    var debug: Boolean = configuration.debug
    val logger = Logger(p)
    private val allArtifacts = mutableListOf<javaFile>()
    private val cachedArtifacts = mutableListOf<javaFile>()
    private val nonCachedArtifacts = mutableListOf<javaFile>()

    init {
        p.allprojects.forEach { subproject ->
            subproject?.let { sp ->
                //sp.pluginManager.apply(OfflineDependenciesPlugin::class.java)
                val configurationNames = getConfigurations(sp)
                val collectRepository = collectRepositoryFiles(sp, configurationNames) { repositoryFiles ->
                    repositoryFiles.forEach { (id, files) ->
                        val directory = moduleDirectory(id)
                        GFileUtils.mkdirs(directory)
                        //logger.d(directory)
                        files.forEach { file ->
                            //logger.i("-> $file")
                            val destination = com.dimaslanjaka.gradle.plugin.File(directory, file.name)
                            GFileUtils.copyFile(file, destination)
                        }
                    }
                }
            }
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getConfigurations(project: Project): List<Configuration> {
        val configurations = mutableSetOf<Configuration>()
        setOf("compile", "testCompile", "implementation", "testImplementation").forEach { confName ->
            val find = project.configurations.findByName(confName)
            if (find != null) {
                configurations.add(find)
            } else {
                if (Core.extension.debug) {
                    println("Project with configuration name $confName not found")
                }
            }
        }

        setOf("classpath", "runtime").forEach { confName ->
            val find = project.buildscript.configurations.findByName(confName)
            if (find != null) {
                configurations.add(find)
            } else {
                if (Core.extension.debug) {
                    println("Project buildscript with configuration name $confName not found")
                }
            }
        }

        configurations.addAll(project.buildscript.configurations)
        configurations.addAll(project.configurations)
        return configurations.distinct()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun collectRepositoryFiles(
        project: Project,
        configurations: List<Configuration>,
        callback: (MutableMap<ModuleComponentIdentifier, MutableSet<java.io.File>>) -> Unit
    ) {
        val componentIds = mutableSetOf<ModuleComponentIdentifier>()
        val repositoryFiles = mutableMapOf<ModuleComponentIdentifier, MutableSet<java.io.File>>()
        configurations.forEach { configuration ->
            configuration.allDependencies.forEach { dependency ->
                if (dependency != null) {
                    val cfg = project.configurations.detachedConfiguration(dependency)
                    cfg.resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
                        val targetLocal =
                            Paths.get(this.configuration.localRepository.absolutePath).normalize().toString()
                        val artifactLocation = Paths.get(artifact.file.absolutePath).normalize().toString()
                        if (!artifactLocation.startsWith(targetLocal)) {
                            // TODO: add artifact even not from local
                            val componentId = DefaultModuleComponentIdentifier.newId(
                                DefaultModuleIdentifier.newId(
                                    artifact.moduleVersion.id.group,
                                    artifact.moduleVersion.id.name
                                ), artifact.moduleVersion.id.version
                            )

                            componentIds.add(componentId)
                            if (repositoryFiles.containsKey(componentId)) {
                                if (!repositoryFiles[componentId]?.contains(artifact.file)!!) {
                                    repositoryFiles[componentId]?.add(artifact.file)
                                }
                            } else {
                                repositoryFiles.putIfAbsent(componentId, mutableSetOf(artifact.file))
                            }

                            if (this.configuration.debug) println("Adding artifact for id'{${componentId}}' (location '{${artifact.file}}')")
                        }
                    }
                }
            }
        }

        callback(repositoryFiles)
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

    private fun convert2MavenLocal(artifactStr: String): Path {
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

    private fun isDomain(string: String): Boolean {
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

    private fun isVersion(string: String): Boolean {
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

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun moduleDirectory(ci: ModuleComponentIdentifier): java.io.File {
        return java.io.File(
            configuration.localRepository.absolutePath,
            "${ci.group.split(".").joinToString("/")}/${ci.module}/${ci.version}"
        );
    }
}