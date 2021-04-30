package com.dimaslanjaka.kotlin

import com.dimaslanjaka.java.Thread
import com.dimaslanjaka.kotlin.ConsoleColors.Companion.println
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import deserialize
import gson
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import toLongDateFormat
import java.io.*
import java.net.URI
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes
import java.util.Date
import java.util.concurrent.TimeUnit
import java.io.File as javaFile


/**
 * TODO: Extends default files
 */
open class File : javaFile, Serializable, Comparable<javaFile?> {
    private var file: javaFile

    constructor(pathname: String) : super(pathname) {
        this.file = javaFile(pathname)
    }

    constructor(parent: String, child: String) : super(parent, child) {
        this.file = javaFile(parent, child)
    }

    constructor(parent: javaFile, child: String) : super(parent, child) {
        this.file = javaFile(parent, child)
    }

    constructor(uri: URI) : super(uri) {
        this.file = javaFile(uri)
    }

    inner class WalkResult {
        val dirs = mutableListOf<File>()
        val files = mutableListOf<File>()
    }

    private var walkResult = WalkResult()
    private fun walk(path: String): WalkResult {
        val root = javaFile(path)
        val list = root.listFiles()
        list?.forEach { file ->
            file?.let { f ->
                if (f.isDirectory) {
                    walk(f.absolutePath)
                    //println("Dir:" + f.absoluteFile)
                    walkResult.dirs.add(File(f.absolutePath))
                } else {
                    //println("File:" + f.absoluteFile)
                    walkResult.files.add(File(f.absolutePath))
                }
            }
        }
        return walkResult
    }

    /**
     * Check file locked by java.nio.FileLock
     */
    fun isFileLocked(): Boolean {
        return Thread.isFileLocked(File(this.file.absolutePath))
    }

    /**
     * Recursive iterator of directory
     * <code>
     *     File("directory/path").walk().forEach { f ->
     *          if (f.isDirectory) {
     *              println("Dir:" + f.absoluteFile)
     *          } else if (f.isFile) {
     *              println("File:" + f.absoluteFile)
     *          }
     *     }
     * </code>
     */
    fun walk(): WalkResult? {
        // re-initialize result class
        walkResult = WalkResult()
        // re-start iterator
        if (this.file.isDirectory) {
            return walk(this.file.absolutePath)
        }
        return null
    }

    /**
     * Normalize path separator
     */
    fun normalize(): Path {
        return this.file.toPath().normalize()
    }

    fun normalizeAsString(): String {
        return normalize().toString()
    }

    fun normalizeAsCanonicalFile(): File {
        return File(normalize().toString())
    }

    /**
     * Create new file
     */
    fun createFile(): File {
        resolveParent()
        this.file.createNewFile()
        return this
    }

    fun resolveParent(): File {
        if (!this.file.parentFile.exists()) {
            this.file.parentFile.mkdirs()
        }
        return this
    }

    override fun listFiles(): Array<out java.io.File> {
        val mlist = mutableListOf<java.io.File>()
        this.file.listFiles()?.forEachIndexed { index, file ->
            mlist.add(index, java.io.File(file.absolutePath))
        }
        return mlist.toTypedArray()
    }

    /**
     * Write to file
     */
    fun write(content: Any): File {
        return write(content, Charset.defaultCharset())
    }

    /**
     * Write to file
     */
    fun write(content: Any, charset: Charset = Charset.defaultCharset()): com.dimaslanjaka.kotlin.File {
        // resolve parent directory
        resolveParent()
        // begin write
        if (content is Date) {
            this.file.writeText(content.time.toString())
        } else if (content is String) {
            this.file.writeText(content, charset)
        } else if (content is Class<*> || content is Collection<*>) {
            val json = gson().toJson(content)
            this.file.writeText(json)
        } else {
            this.file.writeText(content.toString())
        }
        return this
    }

    fun appendText(content: Any) {
        this.file.appendText(content.toString())
    }

    /**
     * Read file as String
     */
    fun read(charset: Charset = Charset.defaultCharset()): String {
        return this.file.readText(charset)
    }

    fun inputStream(): InputStream {
        return FileInputStream(file)
    }

    fun outputStream(): OutputStream {
        return FileOutputStream(this.file)
    }

    fun readByte(): ByteArray {
        return this.file.readBytes()
    }

    @JvmName("copyTo")
    fun copy(targetFile: javaFile) {
        resolveParent(targetFile)
        val initialStream: InputStream = FileInputStream(this.file)
        Files.copy(
            initialStream,
            targetFile.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )
    }

    fun resolveParent(targetFile: javaFile): Boolean {
        val parent = targetFile.parentFile
        return if (!parent.exists()) {
            parent.mkdirs()
        } else {
            false
        }
    }

    /**
     * Read and Deserialize JSON
     */
    fun <T> read(typeToken: TypeToken<T>): T {
        return this.file.readText(Charset.defaultCharset()).deserialize(gson(), typeToken)
    }

    fun createGradleProject(): Project {
        val project: Project = ProjectBuilder.builder().withProjectDir(this.file).build()
        project.repositories.add(project.repositories.mavenLocal())
        return project
    }

    private fun gson(): Gson = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create()

    /**
     * Create file in directory
     */
    @JvmName("file_in_dir")
    fun fileInDir(filename: String, autocreate: Boolean): File? {
        val parent: javaFile = this.file
        this.file = File(this.file, filename)
        if (parent.isDirectory) {
            if (autocreate) {
                if (!parent.exists()) if (!parent.mkdirs()) println("Cannot create parent dir $parent")
                try {
                    if (!this.file.exists()) if (!this.file.createNewFile()) println("cannot create file $filename")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return File(this.file.absolutePath)
        }
        return null
    }

    @JvmName("file_in_dir")
    fun fileInDir(filename: String): File? {
        return fileInDir(filename, false)
    }

    /**
     * Convert to com.dimaslanjaka.gradle.plugin.File
     */
    fun toDimaslanjakaGradlePluginFile(): com.dimaslanjaka.gradle.plugin.File {
        return com.dimaslanjaka.gradle.plugin.File(this.file.absolutePath)
    }

    /**
     * Convert to java.io.File
     */
    fun toJavaIoFile(): java.io.File {
        return javaFile(this.file.absolutePath)
    }

    /**
     * File information about dates (creation, modify, access)
     */
    fun getDateAttributes(): DateAttributes {
        return DateAttributes(this.file)
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun isSameFileSize(file1: javaFile, file2: javaFile): Boolean {
            return if (!file1.exists() || !file2.exists()) {
                false
            } else {
                file1.length().compareTo(file2.length()) == 0
            }
        }

        @Suppress("unused")
        @JvmStatic
        fun isNotSameFileSize(file1: javaFile, file2: javaFile): Boolean {
            return !isSameFileSize(file1, file2)
        }
    }

    class DateAttributes(file: javaFile) {
        val attr: BasicFileAttributes = Files.readAttributes(
            file.toPath(),
            BasicFileAttributes::class.java
        )

        /**
         * Created Date
         */
        val created = attr.creationTime().to(TimeUnit.MILLISECONDS).toLongDateFormat()

        /**
         * Last Modified Time
         */
        val modified = attr.lastModifiedTime().to(TimeUnit.MILLISECONDS).toLongDateFormat()

        /**
         * Last Access Time
         */
        val access = attr.lastAccessTime().to(TimeUnit.MILLISECONDS).toLongDateFormat()

        override fun toString(): String {
            return gson().toJson(this)
        }
    }
}