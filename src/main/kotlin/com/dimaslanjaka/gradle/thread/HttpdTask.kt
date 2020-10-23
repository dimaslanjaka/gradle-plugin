package com.dimaslanjaka.gradle.thread

import com.dimaslanjaka.webserver.StartHttpd
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class HttpdTask : DefaultTask() {
    @TaskAction
    fun greet() {
        try {
            StartHttpd.run()
            StartHttpd.loopStart()
        } catch (e: Exception) {
            println(e.message)
            //logger.error(e.getMessage(), e);
        }
    }
}