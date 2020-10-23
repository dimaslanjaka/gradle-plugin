package com.dimaslanjaka.gradle.testing


import org.gradle.api.Project

import java.nio.charset.StandardCharsets

class Reporter {
    static Project project

    static void apply(Project p) {
        project = p
        project.afterEvaluate {
            project.allprojects.each { Project ap ->
                ap.repositories.each {
                    //println "Project: ${ap.name}; RepoName: ${it.name}; RepoUrl: ${it.url}"
                    if (String.valueOf(it.url as String).startsWith("http")) {
                        report(it.name, it.url as String)
                    }
                }
            }
        }
    }

    /**
     * String urlParameters  = "param1=data1&param2=data2&param3=data3";
     */
    static void send(String endpoint, String urlParameters) {
        try {
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8)
            int postDataLength = postData.length
            String request = endpoint
            URL url = new URL(request)
            HttpURLConnection conn = (HttpURLConnection) url.openConnection()
            conn.setDoOutput(true)
            //conn.setReadTimeout(300)
            //conn.setConnectTimeout(300)
            conn.setInstanceFollowRedirects(true)
            conn.setRequestMethod("POST")
            conn.setRequestProperty("User-Agent", "Mozilla/5.0")
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.setRequestProperty("charset", "utf-8")
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength))
            conn.setUseCaches(false)

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream())
            wr.write(postData)

            int code = conn.getResponseCode()
            //println(code)
        } catch (Exception e) {
            println(e.message)
        }
    }

    static void report(String name, String url) {
        File log = new File("${project.buildDir}/report/repositories.properties")
        if (!log.parentFile.exists()) log.parentFile.mkdirs()
        if (!log.exists()) log.createNewFile()
        Properties prop = new Properties()
        InputStream stream = new FileInputStream(log)
        prop.load(stream)
        stream.close()
        boolean write = false
        if (!prop.toString().contains(url)) {
            write = true
            prop.put(name, url)
            send(
                    "http://backend.webmanajemen.com/artifact/servers/index.php",
                    "name=${name}&url=${url}&custom=true"
            )
        }
        if (write) {
            prop.store(new FileOutputStream(log), null)
        }
    }
}
