package com.dimaslanjaka.kotlin

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.concurrent.Callable
import javax.xml.parsers.DocumentBuilderFactory

class FixMavenLocal {
    var home = System.getProperty("user.home")!!
    var temp = System.getProperty("java.io.tmpdir")!!
    var m2 = File(home, ".m2")

    fun run() {
        Thread {
            start()
        }
    }

    fun start() {
        try {
            val walk = Files.walk(m2.toPath()).filter { path -> Files.isRegularFile(path) }
            walk.forEach { f ->
                if (f.toString().endsWith(".pom")) {
                    validatepom(f.toFile(), {
                        //println("$f pom is valid")
                    }, {
                        if (f.toFile().delete()) {
                            println("$f invalid pom, deleted")
                        } else {
                            println("$f invalid pom, cannot deleted")
                        }
                    })
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun validatepom(FilePom: File, success: Callable<Any>?, failed: Callable<Any>?){
        try {
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc = dBuilder.parse(FilePom)
            success!!.call()
        } catch (e: Exception) {
            failed!!.call()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            FixMavenLocal().start()
        }
    }
}