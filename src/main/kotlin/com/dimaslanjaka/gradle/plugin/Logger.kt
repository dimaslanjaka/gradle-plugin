package com.dimaslanjaka.gradle.plugin

import org.gradle.api.Project
import java.io.File
import java.util.regex.Matcher
import javax.validation.constraints.NotNull

@Suppress("JoinDeclarationAndAssignment", "ConvertSecondaryConstructorToPrimary")
class Logger {
    private var project: Project
    private var template = ""
    val result = mutableListOf<String>()

    constructor(project: Project) {
        this.project = project
        this.template = "[${project.name}] %s:\n\t%s\n"
    }

    fun i(vararg message: Any?) {
        val trace = trace(Thread.currentThread().stackTrace)
        val debugTemplate = "${this.template}%s\n"
        if (message.size == 1) {
            if (trace.trim().isNotEmpty()) {
                System.out.printf(debugTemplate, "DEBUG", trace, message[0])
            } else {
                System.out.printf(this.template, "DEBUG", message[0])
            }
        } else {
            if (trace.trim().isNotEmpty()) {
                System.out.printf(debugTemplate, "DEBUG", trace, message)
            } else {
                System.out.printf(this.template, "DEBUG", message)
            }
        }
    }

    fun d(vararg message: Any?) {
        val trace = trace(Thread.currentThread().stackTrace)
        val debugTemplate = "${this.template}%s\n"
        if (message.size == 1) {
            if (trace.trim().isNotEmpty()) {
                System.out.printf(debugTemplate, "DEBUG", trace, message[0])
            } else {
                System.out.printf(this.template, "DEBUG", message[0])
            }
        } else {
            if (trace.trim().isNotEmpty()) {
                System.out.printf(debugTemplate, "DEBUG", trace, message)
            } else {
                System.out.printf(this.template, "DEBUG", message)
            }
        }
    }

    /**
     * Usage <code>trace(Thread.currentThread().stackTrace)</code>
     */
    private fun trace(stackTrace: Array<StackTraceElement>?): String {
        if (stackTrace != null) {
            val workdir = System.getProperty("user.dir")
            val c = stackTrace[2].className
                // split and drop last element, remove dot to forward slash
                .split(".").dropLast(1).joinToString("/")
                // Remove kotlin lambda
                .replace("\\\$+[a-zA-Z0-9]+\\\$+[a-zA-Z0-9]+\\\$+[a-zA-Z0-9]+\\\$+[a-zA-Z0-9]+\\\$+\\d".toRegex(), "")
            val m = stackTrace[2].methodName;
            val f = stackTrace[2].fileName;
            val l = stackTrace[2].lineNumber;
            listOf(
                "$workdir/src/main/kotlin",
                "$workdir/src/main/groovy",
                "$workdir/src/main/java",
                "$workdir/src/test/kotlin",
                "$workdir/src/test/groovy",
                "$workdir/src/test/java"
            ).forEach { srcDir ->
                val merged = "$srcDir/$c/$f:$l".replace("/", java.io.File.separator)
                if (File(merged).exists()) {
                    return "($m) -> $merged"
                }
            }
        }
        return ""
    }

    fun append(string: String) {
        this.result.add(string)
    }

    @Suppress("SameParameterValue")
    private fun join(@NotNull separator: String, vararg strings: String?): String {
        return java.lang.String.join(separator, *strings)
    }

    private fun normalizePath(path: String): String {
        return path.replace("/".toRegex(), Matcher.quoteReplacement(File.separator))
    }
}