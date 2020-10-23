package com.dimaslanjaka.gradle.offline

import com.dimaslanjaka.gradle.utils.ConsoleColors
import com.dimaslanjaka.gradle.utils.Jar
import com.dimaslanjaka.gradle.utils.Properties
import com.dimaslanjaka.gradle.utils.Utils
import org.gradle.api.Project

import java.nio.file.Files
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

abstract class GradleCacheToMavenRepo implements Runnable {
    GradleCacheToMavenRepo(Project project) {
        Date today = new Date()
        def log = new File(project.buildDir, "tmp/${Utils.md5(this.class.name)}.properties")
        Properties orderedProperties = new Properties(log, true)
        def allowToModify = false
        if (!orderedProperties.contains("TIMESTAMP")) {
            allowToModify = true
        } else {
            def value = orderedProperties.get("TIMESTAMP").toLong()
            Date created = new Date(value)
            def difference = new Date().time - created.time
            if (difference > TimeUnit.MINUTES.toMillis(60)) {
                allowToModify = true
                orderedProperties.set("TIMESTAMP", today.getTime().toString())
            }
        }
        if (allowToModify) {
            ConsoleColors.success("Artifact caching going to background")
            orderedProperties.set("TIMESTAMP", today.getTime().toString())
            ExecutorService service = Executors.newFixedThreadPool(4)
            service.submit(this)
        }
    }

    /**
     * On finish callback
     */
    abstract void onFinish()

    @Override
    void run() {
        int resultCount = 0
        def home = System.properties['user.home']
        def from = new File(home as File, '.gradle/caches/modules-2/files-2.1')
        def to = new File(home as File, '.m2/repository')
        from.eachFile {
            List<String> parts = fixPath(it.path).split("/")
            def localMaven = new StringBuilder(fixPath(to.absolutePath))
            localMaven.append("/${parts[7].replace(".", "/")}")
            it.eachDir {
                if (isValidArtifactPath(it.name)) {
                    def artif = localMaven.toString() + "/${it.name}/"
                    it.eachDir {
                        if (isValidArtifactPath(it.name)) {
                            def version = fixPath("${artif}/${it.name}/")
                            it.eachDir {
                                if (!isValidArtifactPath(it.name)) {
                                    it.eachFile {
                                        def toArtifact = new File(fixPath("${version}/${it.name}"))
                                        if (!toArtifact.parentFile.exists()) toArtifact.parentFile.mkdirs()
                                        if (!toArtifact.exists()) {
                                            resultCount++
                                            Files.copy(it.toPath(), toArtifact.toPath())
                                            print(String.format("%s Cached Successfully", ConsoleColors.styler(ConsoleColors.CYAN_UNDERLINED, it.name)))
                                        } else {
                                            if (!Jar.validate(toArtifact.absolutePath)) {
                                                toArtifact.delete()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        println("")
        ConsoleColors.success("Auto Sync Local Repository")
        StringBuilder sb = new StringBuilder("Synced ")
        sb.append(ConsoleColors.styler(ConsoleColors.CYAN, resultCount.toString()) + " ")
        sb.append("Artifact")
        if (resultCount > 0) sb.append("'s ")
        println(sb.toString().trim())

        onFinish()
    }

    static String fixPath(Object path) {
        return path.toString().replace("\\", "/").replaceAll('/{2,99}', '/')
    }

    static boolean isValidArtifactPath(Object path) {
        return path.toString().length() < 40
    }

    static void print(Object... messages) {
        //boolean contains = Array.stringContainsItemFromList(messages[0].toString(), ["%s", "%d", "%b"] as String[])
        /*
        if (contains){
            print(String.format(messages[0].toString()))
        }
         */
        println(messages.toArrayString())
    }

    static void print(Object message) {
        println(message.toString())
    }
}
