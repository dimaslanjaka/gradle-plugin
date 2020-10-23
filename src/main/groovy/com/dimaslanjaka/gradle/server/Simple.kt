package com.dimaslanjaka.gradle.server

import com.dimaslanjaka.gradle.Utils.ConsoleColors
import com.dimaslanjaka.gradle.Utils.file.FileHelper
import com.dimaslanjaka.gradle.Utils.json.SimpleJSON
import com.dimaslanjaka.gradle.curl.Curl
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.file.attribute.BasicFileAttributes

object Simple {
    var userHome: java.io.File? = java.io.File(java.lang.System.getProperty("user.home"))
    var mavenDir: java.io.File? = java.io.File(userHome, ".m2/repository")

    @kotlin.Throws(Exception::class)
    @kotlin.jvm.JvmStatic
    fun main(args: Array<String>) {
        val server: com.sun.net.httpserver.HttpServer = com.sun.net.httpserver.HttpServer.create(InetSocketAddress(8500), 0)
        val ROOT: String = mavenDir.getAbsolutePath()
        java.nio.file.Files.walkFileTree(java.nio.file.Paths.get(ROOT), object : java.nio.file.FileVisitor<java.nio.file.Path?> {
            @kotlin.Throws(IOException::class)
            override fun preVisitDirectory(dir: java.nio.file.Path?, attrs: BasicFileAttributes?): java.nio.file.FileVisitResult? {
                var dirPath: String? = dir.toString().replace(mavenDir.toString(), "/")
                dirPath = fixPath(dirPath)
                server.createContext(dirPath, object : com.sun.net.httpserver.HttpHandler {
                    @kotlin.Throws(IOException::class)
                    override fun handle(httpExchange: com.sun.net.httpserver.HttpExchange?) {
                        val response = "This is the response"
                        httpExchange.sendResponseHeaders(200, response.length.toLong())
                        val os: java.io.OutputStream = httpExchange.getResponseBody()
                        os.write(response.toByteArray())
                        os.close()
                    }
                })
                //server.setExecutor(null); // creates a default executor
                //System.out.println(dirPath);
                return java.nio.file.FileVisitResult.CONTINUE
            }

            @kotlin.Throws(IOException::class)
            override fun visitFile(file: java.nio.file.Path?, attrs: BasicFileAttributes?): java.nio.file.FileVisitResult? {
                return java.nio.file.FileVisitResult.CONTINUE
            }

            @kotlin.Throws(IOException::class)
            override fun visitFileFailed(file: java.nio.file.Path?, exc: IOException?): java.nio.file.FileVisitResult? {
                return java.nio.file.FileVisitResult.CONTINUE
            }

            @kotlin.Throws(IOException::class)
            override fun postVisitDirectory(dir: java.nio.file.Path?, exc: IOException?): java.nio.file.FileVisitResult? {
                return java.nio.file.FileVisitResult.CONTINUE
            }
        })
        server.start()
    }

    fun fixPath(path: String?): String? {
        var path = path
        path = path.replace("\\", "/")
        return path.replace("/{1,9}".toRegex(), "/")
    }

    internal class MyHandler : com.sun.net.httpserver.HttpHandler {
        @kotlin.Throws(IOException::class)
        override fun handle(t: com.sun.net.httpserver.HttpExchange?) {
        }
    }
}