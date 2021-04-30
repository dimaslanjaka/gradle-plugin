package com.dimaslanjaka.java

import com.dimaslanjaka.kotlin.ConsoleColors.Companion.println
import com.dimaslanjaka.kotlin.File
import gson
import org.gradle.api.Project
import java.io.IOException
import java.util.*


@Suppress("unused", "ClassName", "LocalVariableName", "FunctionName", "MemberVisibilityCanBePrivate")
open class cmd(project: Project) {
    init {
        cmd.project = project
    }

    fun setProject(project: Project) {
        cmd.project = project
    }

    companion object {
        lateinit var project: Project

        @JvmStatic
        fun main(args: Array<String>) {
            project = File("build/test-results/shell").createGradleProject()
            //println(adb_devices())
            //println(adb_list_files("/sdcard/DCIM"))
            //adb_list_files("/sdcard/Download")
            adb_list_files("/sdcard/DCIM")
        }

        @JvmStatic
        fun adb(param: List<String>): String {
            val combined_shell = mutableListOf("adb")
            combined_shell.addAll(param)
            val result_shell = combined_shell.joinToString(" ")
            return exec(result_shell)
            //return result_shell
        }

        @JvmStatic
        fun adb(param: String): String {
            return exec("adb $param")
        }

        @JvmStatic
        fun adb_list_files(path: String): adbListFiles {
            val clazz = adbListFiles()
            clazz.parent = path
            val exe = adb("shell ls -Ral $path")
            //println("$exe\n----------------")

            val trim_stdout = exe.split("\\n".toRegex()).joinToString("\n") { o -> o.trim() }

            trim_stdout.split("\n\n").forEach { sub ->
                val split_n = sub.split("\n").toMutableList()
                val key = split_n[0].replace(":", "")
                val value = mutableListOf<String>()
                // remove 2 first line
                listOf(split_n[0], split_n[1]).map { split_n.remove(it) }
                split_n.forEach { file ->
                    val filter_file = file.replace("^[d-]*.+\\d{2}\\:\\d{2}".toRegex(), "").trim()
                    if (filter_file != "." && filter_file != "..") {
                        //println(filter_file)
                        value.add("$key/$filter_file")
                        clazz.list.add("$key/$filter_file")
                    }
                }
                clazz.groups[key] = value
            }

            val name = object {}.javaClass.enclosingMethod.name
            val outfile = File(project.buildDir, "shell/${name}.json")
            outfile.write(clazz.toString())
            println("Log $name saved on $outfile")

            return clazz
        }

        class adbListFiles {
            lateinit var parent: String
            val list = mutableListOf<String>()
            var groups = mutableMapOf<String, MutableList<String>>()
            override fun toString(): String {
                return gson().toJson(this)
            }
        }

        /**
         * List all connected devices through adb
         */
        @JvmStatic
        fun adb_devices(): MutableMap<String, String> {
            val exe = adb(listOf("devices")).replace("List of devices attached", "")
            val split_n = exe.split("\n")
            val result = mutableMapOf<String, String>()
            split_n.forEach { n ->
                if (n.isNotEmpty()) {
                    /**
                     * 0 = device id
                     * 1 = unauthorized | device (authorize granted)
                     */
                    val split_t = n.split("\\s+|\\t".toRegex()).map { t -> t.trim() }
                    if (split_t.size >= 2) {
                        if (split_t[0].trim().isNotEmpty()) result.putIfAbsent(split_t[0], split_t[1])
                    }
                }
            }
            return result
        }

        @JvmStatic
        fun exec(cmd: String): String {
            return exec(cmd, null)
        }

        @JvmStatic
        fun exec(cmd: String, prefix_output_filename: String?): String {
            if (!this::project.isInitialized) {
                throw Exception("Project not initialized, Cmd.project: \n - cmd.Companion.setProject(YourProject); // static\n - cmd.setProject(YourProject)")
            }
            var result = "null"
            try {
                val inputStream = Runtime.getRuntime().exec(cmd).inputStream
                val s = Scanner(inputStream).useDelimiter("\\A")
                result = if (s.hasNext()) s.next() else "null"
            } catch (e: IOException) {
                e.printStackTrace()
            }

            var cmdname = cmd.split("\\s".toRegex()).toTypedArray()[0]
            if (prefix_output_filename != null) {
                cmdname = "${prefix_output_filename}-${cmdname}"
            }
            val outfile = File(project.buildDir, "shell/$cmdname")
            outfile.write(result)
            println("Log command line saved on $outfile")

            return result
        }
    }
}