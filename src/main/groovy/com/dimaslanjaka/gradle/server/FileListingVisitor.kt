package com.dimaslanjaka.gradle.server

import com.dimaslanjaka.gradle.Utils.ConsoleColors
import com.dimaslanjaka.gradle.Utils.file.FileHelper
import com.dimaslanjaka.gradle.Utils.json.SimpleJSON
import com.dimaslanjaka.gradle.curl.Curl
import java.io.IOException
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

/**
 * Recursive listing with SimpleFileVisitor in JDK 7.
 */
object FileListingVisitor {
    @kotlin.Throws(IOException::class)
    @kotlin.jvm.JvmStatic
    fun main(args: Array<String>) {
        val userHome: java.io.File = java.io.File(java.lang.System.getProperty("user.home"))
        val mavenDir: java.io.File = java.io.File(userHome, ".m2/repository")
        val ROOT: String = mavenDir.absolutePath
        val fileProcessor: java.nio.file.FileVisitor<java.nio.file.Path?> = ProcessFile()
        java.nio.file.Files.walkFileTree(java.nio.file.Paths.get(ROOT), fileProcessor)
    }

    internal class ProcessFile : SimpleFileVisitor<java.nio.file.Path?>() {
        @kotlin.Throws(IOException::class)
        override fun visitFile(
                file: java.nio.file.Path?, attrs: BasicFileAttributes?
        ): java.nio.file.FileVisitResult? {
            //System.out.println("Processing file:" + file);
            return java.nio.file.FileVisitResult.CONTINUE
        }

        @kotlin.Throws(IOException::class)
        override fun preVisitDirectory(
                dir: java.nio.file.Path?, attrs: BasicFileAttributes?
        ): java.nio.file.FileVisitResult? {
            //System.out.println("Processing directory:" + dir);
            return java.nio.file.FileVisitResult.CONTINUE
        }
    }
}