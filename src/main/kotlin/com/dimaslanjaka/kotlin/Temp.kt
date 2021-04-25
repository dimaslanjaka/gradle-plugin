package com.dimaslanjaka.kotlin

//import com.dimaslanjaka.gradle.plugin.File
import com.dimaslanjaka.kotlin.ConsoleColors.Companion.println
import java.io.File

@Suppress("unused", "redundant")
class Temp {
    private val temp = System.getProperty("java.io.tmpdir") ?: "build/tmp"
    private var tempDir: File = File(temp, "gradle")
        set(value) {
            field = File(temp, value.toString())
        }
    private lateinit var tempFile: File

    constructor(rootName: String) {
        this.tempDir = File(temp, "gradle/$rootName")
        fix()
    }

    constructor(fileRootName: File) {
        this.tempDir = fileRootName
        fix()
    }

    constructor() {
        //this.tempDir = File(temp, "gradle")
        fix()
    }

    private fun fix() {
        if (this.tempDir.isFile) {
            if (this.tempDir.delete()) {
                if (!this.tempDir.mkdirs()) {
                    println("cannot create temp folder")
                }
            }
        }
    }

    fun getTempFile(name: String): File {
        return File(tempDir, name)
    }

    fun setTempFile(name: String) {
        tempFile = File(tempDir, name)
    }
}