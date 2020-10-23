package com.dimaslanjaka.gradle.server

import com.dimaslanjaka.gradle.Utils.ConsoleColors
import com.dimaslanjaka.gradle.Utils.file.FileHelper
import com.dimaslanjaka.gradle.Utils.json.SimpleJSON
import com.dimaslanjaka.gradle.curl.Curl
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.net.ServerSocket
import java.util.*

internal object DirectoryListing {
    private val WEB_ROOT: String? = com.dimaslanjaka.gradle.server.Core.getRoot().absolutePath
    private var socket: java.net.Socket? = null
    private var inputStream: java.io.InputStream? = null
    private var outputStream: java.io.OutputStream? = null
    private var dataOutputStream: java.io.DataOutputStream? = null
    private var httpRequestParser: HTTPRequestParser? = null

    @kotlin.jvm.JvmStatic
    fun main(args: Array<String>) {
        run()
    }

    fun run() {
        // Create Server Socket, listen on port passed in via Command Console
        // Use: java WebServer <port number>
        val serverSocket: ServerSocket?
        val port = 8500
        try {
            serverSocket = ServerSocket(port)
            println("Server listening on port $port")
        } catch (e: IOException) {
            java.lang.System.err.println("Unable to listen on port " + port + ": " + e.message)
            return
        }

        // Server listens for incoming requests
        while (true) {
            // Wait client to connect
            try {
                socket = serverSocket.accept()
            } catch (e: IOException) {
                java.lang.System.err.println("Unable to accept connection: " + e.message)
                continue
            }
            println("Connection accepted.")
            try {
                inputStream = socket.getInputStream()
                outputStream = socket.getOutputStream()
                dataOutputStream = java.io.DataOutputStream(outputStream)
                handleRequests()
                socket.close()
                println("Connection closed\n")
            } catch (e: IOException) {
                java.lang.System.err.println("Unable to read/write: " + e.message)
            }
        }
    }

    @kotlin.Throws(IOException::class)
    private fun handleRequests() {
        httpRequestParser = HTTPRequestParser(inputStream)

        // GET or POST REQUEST ONLY
        val requestMethod = httpRequestParser.getRequestMethod()
        if (!(requestMethod == "GET" || requestMethod == "POST")) {
            invalidRequestErr()
            return
        }
        val fileName = httpRequestParser.getFileName()
        val filePath = WEB_ROOT + fileName
        val file: java.io.File = java.io.File(filePath)

        // File permission or not found error.
        if (!file.exists()) {
            fileNotFoundError(fileName)
            return
        }
        if (!file.canRead()) {
            forbiddenAccessError(fileName)
            return
        }

        // Assume everything is OK then.  Send back a reply.
        dataOutputStream.writeBytes("HTTP/1.1 200 OK\r\n")
        val queryString = httpRequestParser.getQueryString()
        if (fileName.endsWith("pl")) {
            val p: java.lang.Process
            var env = "REQUEST_METHOD=$requestMethod "
            env += if (requestMethod == "POST") {
                "CONTENT_TYPE=" + httpRequestParser.getContentType() + " " +
                        "CONTENT_LENGTH=" + httpRequestParser.getContentLength() + " "
            } else {
                "QUERY_STRING=$queryString "
            }
            p = java.lang.Runtime.getRuntime().exec("/usr/bin/env " + env +
                    "/usr/bin/perl " + filePath)
            if (requestMethod == "POST") {
                // Pass form data into Perl process
                val o: java.io.DataOutputStream = java.io.DataOutputStream(p.outputStream)
                o.writeBytes("""
    ${httpRequestParser.getFormData()}
    
    """.trimIndent())
                o.close()
            }
            val br = BufferedReader(java.io.InputStreamReader(p.inputStream))

            // Write response content
            var l: String?
            while (br.readLine().also { l = it } != null) {
                dataOutputStream.writeBytes("""
    $l
    
    """.trimIndent())
            }
            dataOutputStream.writeBytes("\r\n")
        } else {
            if (file.isDirectory) {
                println("Directory Connection")
                folderRequests(file)
            } else {
                println("File Connection")
                staticFileRequests(filePath)
            }
        }
        dataOutputStream.flush()
    }

    private fun folderRequests(folder: java.io.File?) {
        try {
            dataOutputStream.writeBytes("Content-type: text/html\r\n")
            dataOutputStream.writeBytes("\r\n")
            dataOutputStream.writeBytes("<html><body>") // html open tag
            for (file in folder.listFiles()) {
                // calculate relative path
                val rootPath: java.nio.file.Path = java.nio.file.Paths.get(java.io.File(WEB_ROOT).toURI()) // this can be static
                val filePath: java.nio.file.Path = java.nio.file.Paths.get(file.toURI())
                val pathString: String = rootPath.relativize(filePath).toString()

                // construct html
                dataOutputStream.writeBytes("<a href=\"/" + pathString + "\">"
                        + file.name + "</a><br>")
            }
            dataOutputStream.writeBytes("</body></html>") // html close tag
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun staticFileRequests(filePath: String?) {
        try {
            if (filePath.endsWith(".html")) {
                dataOutputStream.writeBytes("Content-type: text/html\r\n")
            } else if (filePath.endsWith(".jpg")) {
                dataOutputStream.writeBytes("Content-type: image/jpeg\r\n")
            } else if (filePath.endsWith("gif")) {
                dataOutputStream.writeBytes("Content-type: image/gif\r\n")
            } else if (filePath.endsWith("css")) {
                dataOutputStream.writeBytes("Content-type: text/css\r\n")
            }
            dataOutputStream.writeBytes("\r\n")
            // Read content at 1KB rate.
            val file: java.io.File = java.io.File(filePath)
            val buffer = ByteArray(file.length() as Int)
            val fis = FileInputStream(file)
            var size: Int = fis.read(buffer)
            while (size > 0) {
                dataOutputStream.write(buffer, 0, size)
                size = fis.read(buffer)
            }
        } catch (e: IOException) {
            java.lang.System.err.println("Unable to READ/WRITE: " + e.message)
        }
    }

    @kotlin.Throws(IOException::class)
    private fun invalidRequestErr() {
        val errorMessage = "This Web Server only demonstrates GET or POST requests\r\n"
        dataOutputStream.writeBytes("HTTP/1.1 400 Bad Request\r\n")
        dataOutputStream.writeBytes("""
    Content-length: ${errorMessage.length}
    
    
    """.trimIndent())
        dataOutputStream.writeBytes(errorMessage)
    }

    @kotlin.Throws(IOException::class)
    private fun fileNotFoundError(fileName: String?) {
        val errorMessage = "Unable to find $fileName on this server.\r\n"
        dataOutputStream.writeBytes("HTTP/1.1 404 Not Found\r\n")
        dataOutputStream.writeBytes("""
    Content-length: ${errorMessage.length}
    
    
    """.trimIndent())
        dataOutputStream.writeBytes(errorMessage)
    }

    @kotlin.Throws(IOException::class)
    private fun forbiddenAccessError(fileName: String?) {
        val errorMessage = "You need permission to access $fileName on this server.\r\n"
        dataOutputStream.writeBytes("HTTP/1.1 403 Forbidden\r\n")
        dataOutputStream.writeBytes("""
    Content-length: ${errorMessage.length}
    
    
    """.trimIndent())
        dataOutputStream.writeBytes(errorMessage)
    }
}

internal class HTTPRequestParser(`is`: java.io.InputStream?) {
    private val requestMethod: String?
    private val fileName: String?
    private val queryString: String?
    private val formData: String?
    private val headers: Hashtable<String?, String?>?
    private val ver: IntArray?
    fun getRequestMethod(): String? {
        return requestMethod
    }

    fun getFileName(): String? {
        return fileName
    }

    fun getQueryString(): String? {
        return queryString
    }

    fun getContentType(): String? {
        return headers.get("content-type")
    }

    fun getContentLength(): Int {
        return headers.get("content-length").toInt()
    }

    fun getFormData(): String? {
        return formData
    }

    companion object {
        fun fileList(directoryName: String?): MutableList<java.io.File?>? {
            val directory: java.io.File = java.io.File(directoryName)
            val resultList: MutableList<java.io.File?> = java.util.ArrayList<java.io.File?>()

            // get all the files from the directory
            val fileList: Array<java.io.File?> = directory.listFiles()
            for (file in fileList) {
                if (file.isFile()) {
                    println(file.getAbsolutePath())
                } else if (file.isDirectory()) {
                    resultList.addAll(fileList(file.getAbsolutePath()))
                }
            }
            return resultList
        }
    }

    init {
        val br = BufferedReader(java.io.InputStreamReader(`is`))
        requestMethod = ""
        fileName = ""
        queryString = ""
        formData = ""
        headers = Hashtable<String?, String?>()
        try {
            // Wait for HTTP request from the connection
            var line: String = br.readLine()

            // Bail out if line is null. In case some client tries to be
            // funny and close immediately after connection.  (I am
            // looking at you, Chrome!)
            if (line == null) {
                //System.out.println("LINE --NULL--");
                //return;
                line = "/"
            }

            // Shows Client requests in server log.
            println("Request: $line")
            val tokens: Array<String?> = line.split(" ".toRegex()).toTypedArray()
            requestMethod = tokens[0]
            if (tokens[1].contains("?")) {
                val urlComponents: Array<String?> = tokens[1].split("\\?".toRegex()).toTypedArray()
                fileName = urlComponents[0]
                queryString = urlComponents[1]
            } else {
                fileName = tokens[1]
            }

            // Read/Parse HTTP headers
            var idx: Int
            line = br.readLine()
            while (line != "") {
                idx = line.indexOf(":")
                if (idx < 0) {
                    headers = null
                    break
                } else {
                    headers.put(line.substring(0, idx).toLowerCase(),
                            line.substring(idx + 1).trim { it <= ' ' })
                }
                line = br.readLine()
            }

            // Read POST Data
            if (requestMethod == "POST") {
                val contentLength = getContentLength()
                val data = CharArray(contentLength)
                for (i in 0 until contentLength) {
                    data[i] = br.read() as Char
                }
                formData = String(data)
            }
        } catch (e: IOException) {
            java.lang.System.err.println("Unable to READ | WRITE: " + e.message)
        }
    }
}