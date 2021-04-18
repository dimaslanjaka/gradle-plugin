package com.dimaslanjaka.gradle.plugin

import java.io.File

interface CoreExtension {
    /*
    override fun toString(): String {
        val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
        return gson.toJson(this)
    }

    init {
        if (!localRepository.exists()) {
            if (!localRepository.mkdirs()) {
                project.logger.error("Cannot create local repository " + localRepository.absolutePath)
            }
        }
    }
     */
    companion object {
        @JvmStatic
        val home: String = System.getProperty("user.home")

        @JvmStatic
        var localRepository = File(File(home), ".m2/repository")

        @JvmStatic
        var limit = 5
    }
}