package com.dimaslanjaka.gradle.plugin

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.gradle.api.Project
import java.io.File

class Offline2 {
    var home = System.getProperty("user.home")
    var from = File(File(home), ".gradle/caches/modules-2/files-2.1")
    var to = File(File(home), ".m2/repository")
    val localMaven = StringBuilder(Offline.fixPath(to.absolutePath))
    lateinit var project: Project

    init {
        if (!to.exists()) if (!to.mkdirs()) println("fail create local repository")
    }

    constructor() {}
    constructor(project: Project, limit: Int = 5) {
        this.project = project
        startCache(limit)
    }

    /**
     * Start cache unlimited
     */
    constructor(project: Project) {
        this.project = project
        startCache(Integer.MAX_VALUE)
    }

    /**
     * Start cache limited
     */
    constructor(limit: Int = 5) {
        startCache(limit)
    }

    /**
     * Start cache limited
     */
    fun startCache(limit: Int) {
        fetchCaches()
        val res = result2.values.toMutableList()
        res.shuffle()
        res.forEachIndexed { index, resultOffline2 ->
            if (index <= limit) {
                val log = Offline.copy(resultOffline2.from, resultOffline2.to, false)
                println("$index. $log")
            }
        }
    }

    fun fetchCaches() {
        var resultCount = 0
        listJarModules().forEach { list1 ->
            list1.files.forEach { jarModules ->
                // copy maven path to module directory builder variable
                val modulePath: java.lang.StringBuilder = java.lang.StringBuilder(list1.mavenPath.toString())
                modulePath.append("/").append(Offline.getFileName(jarModules)).append("/")

                jarModules.listFiles()?.forEach { jarVersions ->
                    if (jarVersions.isDirectory) {
                        // copy jar directory for versioning variable
                        val jVersion = java.lang.StringBuilder(modulePath.toString())
                        jVersion.append("/").append(Offline.getFileName(jarVersions)).append("/")
                        val versionPath = File(jVersion.toString())
                        if (!versionPath.exists()) {
                            if (!versionPath.mkdirs()) {
                                Offline.error("fail create " + list1.id2path + " v" + Offline.getFileName(jarVersions))
                            }
                        }

                        jarVersions.listFiles()?.forEach { jarHases ->
                            if (!Offline.isValidArtifactPath(Offline.getFileName(jarHases))) {
                                jarHases.listFiles()?.forEach { artifact ->
                                    if (artifact != null) {
                                        if (Offline.validExtension(artifact.absolutePath)) {
                                            val targetMavenArtifact =
                                                File(versionPath, Offline.getFileName(artifact))
                                            if (Offline.isEmptyFile(targetMavenArtifact)) {
                                                var copyConfirm = false
                                                if (isValidArtifact(artifact)) {
                                                    if (Offline.validateXML(artifact.toPath())) {
                                                        copyConfirm = true
                                                    } else {
                                                        // TODO: delete if target malformed pom
                                                        artifact.delete()
                                                    }
                                                } else {
                                                    copyConfirm = true
                                                }
                                                if (copyConfirm) {
                                                    result.add(
                                                        ResultOffline2(
                                                            artifact,
                                                            targetMavenArtifact,
                                                            resultCount
                                                        )
                                                    )

                                                    result2[artifact.absolutePath] = ResultOffline2(
                                                        artifact,
                                                        targetMavenArtifact,
                                                        resultCount
                                                    )
                                                    //Offline.copy(artifact, targetMavenArtifact)
                                                    resultCount++
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Suppress("unused")
    fun fetchCaches(file: File): MutableList<ResultOffline2> {
        val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        result2 = gson.fromJson(
            file.readText(),
            object : TypeToken<MutableMap<String, ResultOffline2?>?>() {}.type
        )
        fetchCaches()
        file.writeText(gson.toJson(getResult2(), MutableMap::class.java))
        val res = result2.values.toMutableList()
        res.shuffle()
        return res
    }

    private fun isValidArtifact(artifact: File): Boolean {
        val validExtension =
            artifact.name.endsWith(".xml") || artifact.name.endsWith(".pom") || artifact.name.endsWith(".aar")
        return artifact.exists() && validExtension
    }


    fun listJarModules(): MutableList<List1> {
        val result = mutableListOf<List1>()
        listGradleCaches()?.forEachIndexed { index, file ->
            val list = List1()
            list.index = index
            list.id2path = Offline.getFileName(file).replace(".", "/")
            list.mavenPath = StringBuilder(localMaven.toString().trim { it <= ' ' } + "/" + list.id2path)
            file.listFiles()?.let { arr ->
                arr.forEach { file ->
                    if (file != null) {
                        list.files.add(file)
                    }
                }
            }
            result.add(list)
        }
        return result
    }

    /**
     * list files in gradle caches
     */
    fun listGradleCaches(): MutableList<File>? {
        return if (from.exists()) {
            from.listFiles()?.toMutableList()
        } else {
            null
        }
    }

    @Suppress("unused")
    fun getResult(): MutableList<ResultOffline2> {
        return result
    }

    @Suppress("unused")
    fun getResult2(): MutableMap<String, ResultOffline2> {
        return result2
    }

    @Suppress("unused")
    fun reset() {
        result.clear()
        result2.clear()
    }

    class List1 {
        var index: Int = 0
        var files = mutableListOf<File>()
        lateinit var id2path: String
        lateinit var mavenPath: StringBuilder

        override fun toString(): String {
            val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
            return gson.toJson(this)
        }
    }

    class ResultOffline2 {
        lateinit var from: File
        lateinit var to: File
        var index = 0

        constructor() {}
        constructor(gradleCache: File, mavenLocal: File, resultCount: Int) {
            this.from = gradleCache
            this.to = mavenLocal
            this.index = resultCount
        }

        override fun toString(): String {
            val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
            return gson.toJson(this)
        }
    }

    companion object {
        private var result = mutableListOf<ResultOffline2>()
        private var result2 = mutableMapOf<String, ResultOffline2>()

        @JvmStatic
        fun main(args: Array<String>) {
            val off = Offline2(5)
            //off.fetchCaches()
        }

        fun fetchAll() {
            val off = Offline2()
            off.fetchCaches()

            var gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
            val listArtifact = File("build/artifact-list.json")
            listArtifact.writeText(gson.toJson(off.getResult(), MutableList::class.java))
            result = gson.fromJson(listArtifact.readText(), object : TypeToken<MutableList<ResultOffline2?>?>() {}.type)

            gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
            val mapArtifact = File("build/artifact-map.json")
            mapArtifact.writeText(gson.toJson(off.getResult2(), MutableMap::class.java))
            result2 = gson.fromJson(
                mapArtifact.readText(),
                object : TypeToken<MutableMap<String, ResultOffline2?>?>() {}.type
            )
        }
    }
}