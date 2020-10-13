package com.dimaslanjaka.gradle.curl

import com.dimaslanjaka.gradle.repository.ParseArtifact
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.IOException


class Artifact2url {
    private val project = ProjectCore.createProject(javaClass.canonicalName)

    @Test
    fun test() {
        val parsed = ParseArtifact("android.arch.core:common:1.1.1", project)
        println(parsed.list.toString())
    }

    @Test
    @Throws(ClientProtocolException::class, IOException::class)
    @Disabled
    fun givenGetRequestExecuted_whenAnalyzingTheResponse_thenCorrectStatusCode() {
        val client: HttpClient = HttpClientBuilder.create().build()
        val response: HttpResponse = client.execute(HttpGet("http://google.me"))
        val statusCode: Int = response.statusLine.statusCode

        println("Received Status Code $statusCode")
        //assertEquals(statusCode, HttpStatus.SC_OK)
    }
}