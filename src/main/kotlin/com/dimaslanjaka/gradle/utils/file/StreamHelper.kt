package com.dimaslanjaka.gradle.utils.file

import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class StreamHelper(var fileName: String?) {
    var inputStream: InputStream?
    var fileSize: Long

    init {
        val file = File(fileName)
        inputStream = FileInputStream(file)
        fileSize = file.length()
    }
}