package com.dimaslanjaka.gradle.thread

import com.dimaslanjaka.webserver.StartHttpd

object TestHttpd {
    @Throws(InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        StartHttpd.run()
        StartHttpd.loopStart()
    }
}