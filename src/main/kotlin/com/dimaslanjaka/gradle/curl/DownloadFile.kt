package com.dimaslanjaka.gradle.curl

import java.io.*
import java.net.URL
import java.net.URLConnection
import kotlin.properties.Delegates


open class DownloadFile(urlx: String, saveLocation: File, rename: String? = null) {
    var filesize by Delegates.notNull<Float>()

    init {
        if (!saveLocation.exists()) saveLocation.mkdirs()
        if (!saveLocation.canWrite()) throw Throwable("Write Access Denied ${saveLocation.absolutePath}")
        var fileName: String = urlx.substring(urlx.lastIndexOf('/') + 1)
        if (!rename.isNullOrEmpty()) fileName = rename
        try {
            val url = URL(urlx)
            println("get size for -$url")
            val urlConnection: URLConnection
            urlConnection = url.openConnection()
            urlConnection.connect()
            filesize = urlConnection.contentLength / 1024f
            filesize /= 1024f
            println("size of file:- $filesize")
            val input: InputStream = BufferedInputStream(url.openStream())
            val output: OutputStream = FileOutputStream(File("$saveLocation/$fileName"))
            val data = ByteArray(1024)
            var total: Long = 0
            var count: Int
            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                output.write(data, 0, count)
            }
            output.flush()
            output.close()
            input.close()
        } catch (e: IOException) {
            println(e.message)
        }
    }

}