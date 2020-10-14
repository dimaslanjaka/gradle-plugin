package com.dimaslanjaka.gradle.core

import com.dimaslanjaka.gradle.Utils.Utils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class KillJavaProcesses extends DefaultTask {
    @TaskAction
    void run() {
        def buildDir = getProject().buildDir
        def classname = Utils.md5(this.class.name)
        if (Utils.isWindows) {
            'cmd /k "taskkill /f /im jqs.exe"'.execute(null, new File("$buildDir/tmp/${classname}.log"))
            'cmd /k "taskkill /f /im javaw.exe"'.execute(null, new File("$buildDir/tmp/${classname}.log"))
            'cmd /k "taskkill /f /im java.exe"'.execute(null, new File("$buildDir/tmp/${classname}.log"))
        } else {
            'bash -c kill -9 $(ps -ef | pgrep -f "java")'.execute(null, new File("$buildDir/tmp/${classname}.log"))
        }
    }
}
