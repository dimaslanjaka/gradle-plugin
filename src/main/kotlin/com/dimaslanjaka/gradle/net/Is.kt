package com.dimaslanjaka.gradle.net

import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder

class Is {
    companion object {
        /**
         * Check url is reachable
         * @return Boolean true or false
         */
        fun reachable(url: String): Boolean {
            return try {
                val client: HttpClient = HttpClientBuilder.create().build()
                val response: HttpResponse = client.execute(HttpGet(url))
                val statusCode: Int = response.statusLine.statusCode
                statusCode == 200
            } catch (e: Exception) {
                false
            }
        }
    }
}