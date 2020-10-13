package com.dimaslanjaka.gradle.curl

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result

class Fuel {
    fun get() {
        "http://httpbin.org/get".httpGet().response { request, response, result ->
            println(result)
        }
    }
}

fun main(args: Array<String>) {
    val (request, response, result) = "https://m.facebook.com"
            .httpGet()
            .allowRedirects(true)
            .useHttpCache(false)
            .responseString()
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex.message)
        }
        is Result.Success -> {
            val data = result.get()
            //println(data)
            val cookie = response.headers["Set-Cookie"]
            println(cookie.toTypedArray())
        }
    }
}