package com.dimaslanjaka.gradle.curl

import okio.IOException
import java.io.File
import java.util.concurrent.TimeUnit

class CurlCmd {
    /** Run a system-level command.
     * Note: This is a system independent java exec (e.g. | doesn't work). For shell: prefix with "bash -c"
     * Inputting the string in stdIn (if any), and returning stdout and stderr as a string. */
    fun exec(cmd: String, stdIn: String = "", captureOutput:Boolean = false, workingDir: File = File(".")): String? {
        try {
            val process = ProcessBuilder(*cmd.split("\\s".toRegex()).toTypedArray())
                    .directory(workingDir)
                    .redirectOutput(if (captureOutput) ProcessBuilder.Redirect.PIPE else ProcessBuilder.Redirect.INHERIT)
                    .redirectError(if (captureOutput) ProcessBuilder.Redirect.PIPE else ProcessBuilder.Redirect.INHERIT)
                    .start().apply {
                        if (stdIn != "") {
                            outputStream.bufferedWriter().apply {
                                write(stdIn)
                                flush()
                                close()
                            }
                        }
                        waitFor(60, TimeUnit.SECONDS)
                    }
            if (captureOutput) {
                return process.inputStream.bufferedReader().readText()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}