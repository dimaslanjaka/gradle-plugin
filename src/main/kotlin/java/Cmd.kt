package java

import com.dimaslanjaka.kotlin.File
import org.gradle.api.Project
import java.io.IOException
import java.util.*

@Suppress("unused")
class Cmd(project: Project) {
    init {
        Cmd.project = project
    }

    companion object {
        lateinit var project: Project

        @JvmStatic
        fun exec(cmd: String): String {
            return exec(cmd, null)
        }

        @JvmStatic
        fun exec(cmd: String, prefix_output_filename: String?): String {
            if (!this::project.isInitialized) {
                throw Exception("Project not initialized, Cmd.project")
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
            return result
        }
    }
}