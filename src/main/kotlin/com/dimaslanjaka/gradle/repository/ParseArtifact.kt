package com.dimaslanjaka.gradle.repository

import com.dimaslanjaka.gradle.Utils.Reflection
import com.dimaslanjaka.gradle.net.Is
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenDependency
import java.net.URI

class ParseArtifact(artifact: String, project: Project? = null) {
    val list = mutableListOf<Any>()

    init {
        if (project != null) {
            Resolver(project)
        }
        val repositories = Core.repositories
        repositories.forEach {
            list.add(parseArtifact(artifact, it))
        }
    }

    fun parseArtifact(artifact: String, repository: String): Artifactory {
        val split = artifact.split(":")
        val group = split[0]
        val path = group.replace(".", "/")
        val artifactId = split[1]
        val version = split[2]
        val transform2path = "$path/$artifactId/$version/$artifactId-$version"
        val mapper = mutableMapOf<String, URI>()
        arrayOf("jar", "war", "pom", "aar", "xml", "module").forEach { ext ->
            mapper[ext] = URI("$repository/$transform2path.$ext")
        }
        return Artifactory(group, artifactId, version, mapper)
    }

    class Artifactory(group: String, artifact: String, version: String, mapper: Map<String, URI>) : MavenDependency {
        private var jar: String = ""
        private var xml: String = ""
        private var war: String = ""
        private var pom: String = ""
        private var aar: String = ""
        private var module: String = ""
        var groupID = group
        var artifactID = artifact
        var versionID = version

        init {
            mapper.forEach { (ext, url) ->
                if (Is.reachable(url.toString())) {
                    when (ext.isNotEmpty()) {
                        ext == "pom" ->
                            pom = url.toString()
                        ext == "war" ->
                            war = url.toString()
                        ext == "module" ->
                            module = url.toString()
                        ext == "aar" ->
                            aar = url.toString()
                        ext == "xml" ->
                            xml = url.toString()
                        ext == "jar" ->
                            jar = url.toString()
                    }
                }
            }
        }

        fun getAllUrl(): MutableList<String> {
            val mapping = mutableListOf<String>()
            if (jar.isNotEmpty()) mapping.add(jar)
            if (xml.isNotEmpty()) mapping.add(xml)
            if (war.isNotEmpty()) mapping.add(war)
            if (pom.isNotEmpty()) mapping.add(pom)
            if (aar.isNotEmpty()) mapping.add(aar)
            if (module.isNotEmpty()) mapping.add(module)
            return mapping
        }

        override fun toString() = Reflection.toString(this)

        override fun getGroupId(): String {
            return groupID
        }

        override fun getArtifactId(): String {
            return artifactID
        }

        override fun getVersion(): String {
            return versionID
        }

        override fun getType(): String? {
            return this.javaClass.canonicalName
        }

    }
}