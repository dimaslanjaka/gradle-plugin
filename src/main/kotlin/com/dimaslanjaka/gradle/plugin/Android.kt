package com.dimaslanjaka.gradle.plugin

import com.dimaslanjaka.kotlin.ConsoleColors.Companion.println
import com.dimaslanjaka.kotlin.File
import org.apache.commons.lang3.SystemUtils
import org.gradle.api.Project
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL


class Android(project: Project) {
    val config_name = "androidResolver"
    var os: String = if (SystemUtils.IS_OS_WINDOWS) {
        "windows"
    } else /*if (SystemUtils.IS_OS_LINUX)*/ {
        "linux"
    }
    val localRepository = Core.extension.localRepository

    init {
        project.configurations.create(config_name)
        listOf(
            "3.2.0-alpha14-4748712",
            "3.2.0-beta01-4818971",
            "3.4.0-5326820",
            "3.5.0-5435860",
            "3.5.2-5435860",
            "4.0.0-6051327",
            "4.0.1-6197926",
            "4.1.0-6503028",
            "4.1.0-rc03-6503028",
            "4.1.1-6503028",
            "4.1.2-6503028",
            "4.1.3-6503028"
        ).map { downloadAapt2(it) }
    }

    fun downloadAapt2(version: String) {
        val url =
            "https://dl.google.com/dl/android/maven2/com/android/tools/build/aapt2/$version/aapt2-$version-$os.jar"
        val target = File("$localRepository/com/android/tools/build/aapt2/$version/aapt2-$version-$os.jar")
        if (!target.exists()) {
            download(url, "${target.normalize()}")
        }
    }

    fun download(url: String, destination: String) {
        println("Download:\n\t$url\n\t-> $destination")
        try {
            BufferedInputStream(URL(url).openStream()).use { inputStream ->
                FileOutputStream(File(destination).resolveParent()).use { fileOS ->
                    val data = ByteArray(1024)
                    var byteContent: Int
                    while (inputStream.read(data, 0, 1024).also { byteContent = it } != -1) {
                        fileOS.write(data, 0, byteContent)
                    }
                }
            }
        } catch (e: IOException) {
            // handles IO exceptions
            if (Core.extension.getDebug()) {
                e.printStackTrace()
            }
        }
    }
}