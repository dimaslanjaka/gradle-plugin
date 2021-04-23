package com.dimaslanjaka.gradle.plugin

import com.dimaslanjaka.kotlin.ConsoleColors.Companion.println
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import deserialize
import org.codehaus.groovy.runtime.EncodingGroovyMethods
import org.gradle.api.Project
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import javax.xml.parsers.DocumentBuilderFactory

class Offline2 {
    var home = System.getProperty("user.home")
    var from = File(File(home), ".gradle/caches/modules-2/files-2.1")
    var to = File(File(home), ".m2/repository")
    val localMaven = StringBuilder(fixPath(to.absolutePath))
    lateinit var project: Project
    var extensions = arrayOf(".module", ".jar", ".pom", ".aar", ".sha1", ".xml")

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
        fetchCaches(cacheidentifier)
        val res = result2.values.toMutableList()
        res.shuffle()
        var logfile = File("build/.null")
        if (this::project.isInitialized) {
            logfile = File(
                project.buildDir.absolutePath,
                "plugin/com.dimaslanjaka/offline-${project.name}"
            )
            if (!logfile.parentFile.exists()) logfile.parentFile.mkdirs()
            logfile.writeText("Cache Start On ${project.name} With Limit $limit\n")
        } else {
            val temp = File("build").absoluteFile
            if (temp.exists()) {
                logfile = File(temp, "plugin/com.dimaslanjaka/offline-manual")
                if (!logfile.parentFile.exists()) logfile.parentFile.mkdirs()
                logfile.writeText("Cache Start Manual With Limit $limit\n")
            }
        }
        res.forEachIndexed { index, resultOffline2 ->
            if (index <= limit - 1) {
                val indexLog = index + 1
                val log = copy(resultOffline2.from, resultOffline2.to, false)
                logfile.appendText("$indexLog. $log\n", Charset.defaultCharset())
                if (Core.Ext.debug) {
                    println("$indexLog. $log")
                }
            }
        }
        println("Cache Result Saved To ${logfile.absolutePath}")
    }

    fun fetchCaches(cacheFilename: String = "default", overwrite: Boolean = false) {
        var cacheFile = cacheFilename.replace(Regex("[^A-Za-z0-9 ]"), "")
        if (cacheFilename.length > 25) {
            cacheFile = EncodingGroovyMethods.md5(cacheFilename) ?: "default"
        }
        val temp = File("build").absoluteFile
        val cache1 = File(temp.absolutePath, "plugin/com.dimaslanjaka/${cacheFile}.json")
        val cache2 = File(temp.absolutePath, "plugin/com.dimaslanjaka/${cacheFile}2.json")
        var gson: Gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

        if (temp.exists()) {
            if (overwrite) {
                try {
                    write(cache1, gson.toJson(result.toList().distinct()))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    gson = Gson()
                    write(cache2, gson.toJson(result2))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                if (cache1.exists()) {
                    val res = cache1.readText().deserialize(gson, object : TypeToken<MutableList<ResultOffline2>>() {})
                    result.addAll(res)
                }

                if (cache2.exists()) {
                    val res2 = cache2.readText().deserialize(gson, object : TypeToken<Map<String, ResultOffline2>>() {})
                    res2.forEach { (key, value) ->
                        if (!result2.containsKey(key)) {
                            result2.putIfAbsent(key, value)
                        }
                    }
                }

                val run = Runnable {
                    fetchCaches()
                    fetchCaches(cacheFile, true)
                }
                Thread(run).start()
            }
        }
    }

    fun write(file: File, content: Any) {
        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        file.writeText(content.toString())
    }

    fun fetchCaches(): MutableList<ResultOffline2> {
        var resultCount = 0
        listJarModules().forEach { list1 ->
            list1.files.forEach { jarModules ->
                // copy maven path to module directory builder variable
                val modulePath: java.lang.StringBuilder =
                    java.lang.StringBuilder(list1.mavenPath.toString())
                modulePath.append("/").append(getFileName(jarModules)).append("/")

                jarModules.listFiles()?.forEach { jarVersions ->
                    if (jarVersions != null && jarVersions.isDirectory) {
                        // copy jar directory for versioning variable
                        val jVersion = java.lang.StringBuilder(modulePath.toString())
                        jVersion.append("/").append(getFileName(jarVersions)).append("/")
                        val versionPath = File(jVersion.toString())
                        if (!versionPath.exists()) {
                            if (!versionPath.mkdirs()) {
                                error(
                                    "fail create " + list1.id2path + " v" + getFileName(
                                        jarVersions
                                    )
                                )
                            }
                        }

                        jarVersions.listFiles()?.forEach { jarHases ->
                            if (!isValidArtifactPath(getFileName(jarHases))) {
                                jarHases.listFiles()?.forEach { artifact ->
                                    if (artifact != null) {
                                        if (isValidArtifact(artifact)) {
                                            val targetMavenArtifact =
                                                File(versionPath, getFileName(artifact))
                                            if (isEmptyFile(targetMavenArtifact)) {
                                                var copyConfirm = false
                                                if (isPom(artifact)) {
                                                    // if artifact is pom, validate them
                                                    if (validateXML(artifact.toPath())) {
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

                                                    result2.putIfAbsent(
                                                        artifact.absolutePath, ResultOffline2(
                                                            artifact,
                                                            targetMavenArtifact,
                                                            resultCount
                                                        )
                                                    )
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
        return result
    }

    private fun isPom(file: File): Boolean {
        return if (!file.exists()) {
            file.name.endsWith(".pom") || file.name.endsWith(".xml")
        } else {
            false
        }
    }

    fun error(str: String?) {
        if (this::project.isInitialized) {
            project.logger.error(str)
        } else {
            println(str)
        }
    }

    fun validateXML(pathXML: Path): Boolean {
        return if (validExtension(pathXML.toString(), arrayOf(".pom", "xml"))) {
            try {
                val fXmlFile = File(pathXML.toString())
                val dbFactory = DocumentBuilderFactory.newInstance()
                val dBuilder = dbFactory.newDocumentBuilder()
                dBuilder.parse(fXmlFile)
                true
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
    }

    fun fixPath(path: String): String {
        return path
            .replace("\\", "/")
            .replace("/{2,99}".toRegex(), "/")
    }

    fun isEmptyFile(file: File): Boolean {
        return !file.exists() || file.length() == 0L
    }

    fun getFileName(file: File): String {
        val pathObject = Paths.get(file.absolutePath)
        return pathObject.fileName.toString()
    }

    fun getFileName(file: String): String? {
        return getFileName(file, false)
    }

    fun getFileName(file: String, stripExtensions: Boolean): String? {
        val pathObject = Paths.get(file)
        return if (!stripExtensions) {
            pathObject.fileName.toString()
        } else {
            stripExtension(pathObject.fileName.toString())
        }
    }

    fun stripExtension(str: String): String {
        // Get position of last '.'.
        val pos: Int = str.lastIndexOf(".")

        // If there wasn't any '.' just return the string as is.
        if (pos == -1) return str

        // Otherwise return the string, up to the dot.
        val rem: String = str.substring(0, pos)
        return if (validExtension(rem, arrayOf(".jar", ".aar"))) {
            stripExtension(rem)
        } else rem
    }

    fun validExtension(s: String): Boolean {
        for (entry in extensions) {
            if (s.endsWith(entry)) {
                return true
            }
        }
        return false
    }

    fun validExtension(s: String, extensions: Array<String>): Boolean {
        for (entry in extensions) {
            if (s.endsWith(entry)) {
                return true
            }
        }
        return false
    }

    /**
     * If path length < 40 is valid
     *
     * @param path
     * @return
     */
    fun isValidArtifactPath(path: Any): Boolean {
        return path.toString().length < 40
    }

    /**
     * Copy file
     */
    fun copy(from: File, to: File) {
        copy(from, to, false)
    }

    /**
     * Copy file
     */
    fun copy(from: File, to: File, debug: Boolean): String? {
        val xfrom = from.toPath() //convert from File to Path
        val xto: Path = if (!to.isDirectory) {
            Paths.get(to.toString()) //convert from String to Path
        } else {
            Paths.get(File(to, getFileName(from)).toString())
        } //convert from String to Path
        try {
            Files.copy(xfrom, xto, StandardCopyOption.REPLACE_EXISTING)
            val formatted: String = String.format("Cached\n\tf: %s\n\tt: %s", xfrom, xto)
            if (debug) {
                println(formatted)
            } else {
                return formatted
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
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
        val validExtension = validExtension(artifact.absolutePath)
        return artifact.exists() && validExtension
    }

    fun listJarModules(): MutableList<List1> {
        val result = mutableListOf<List1>()
        listGradleCaches()?.forEachIndexed { index, file ->
            val list = List1()
            list.index = index
            list.id2path = getFileName(file).replace(".", "/")
            list.mavenPath =
                StringBuilder(localMaven.toString().trim { it <= ' ' } + "/" + list.id2path)
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
        var cacheidentifier = "Offline2"

        @JvmStatic
        fun main(args: Array<String>) {
            val off = Offline2()
            off.fetchCaches(cacheidentifier)
            off.startCache(Integer.MAX_VALUE)
        }

        fun fetchAll() {
            val off = Offline2()
            off.fetchCaches()

            var gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
            val listArtifact = File("build/artifact-list.json")
            listArtifact.writeText(gson.toJson(off.getResult(), MutableList::class.java))
            result = gson.fromJson(
                listArtifact.readText(),
                object : TypeToken<MutableList<ResultOffline2?>?>() {}.type
            )

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