package com.dimaslanjaka.gradle.server

import com.dimaslanjaka.gradle.server.Core
import org.junit.Test

class Server {
    @Test
    void run() {
        Core.start()
    }
}