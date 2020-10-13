package com.dimaslanjaka.gradle.curl

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Test


class FindIndexOfTest {
    val project = ProjectCore.project
    var doc: Document = Jsoup
            .connect("https://maven.springframework.org/release/com/cloudfoundry/identity/cloudfoundry-login-server/1.2.10/")
            //.data("query", "Java")
            .userAgent("Mozilla")
            .cookie("auth", "token")
            .timeout(3000)
            .get()

    @Test
    fun scan() {
        println(doc.toString())
    }
}