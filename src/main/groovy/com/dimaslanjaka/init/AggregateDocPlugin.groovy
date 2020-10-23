package com.dimaslanjaka.init

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.javadoc.Javadoc

class AggregateDocPlugin {

    static void apply(Project project) {
        Project rootProject = project.rootProject
        rootProject.gradle.projectsEvaluated {
            Set<Project> javaSubprojects = getJavaSubprojects(rootProject)
            if (!javaSubprojects.isEmpty()) {
                if (rootProject.tasks.findByName("aggregateJavadocs-${project.name}") == null) {
                    rootProject.task("aggregateJavadocs-${project.name}", type: Javadoc, group: "gradle-plugin") {
                        description = 'Aggregates Javadoc API documentation of all subprojects.'
                        group = "gradle-plugin" //JavaBasePlugin.DOCUMENTATION_GROUP
                        dependsOn javaSubprojects.javadoc

                        source javaSubprojects.javadoc.source
                        destinationDir = rootProject.file("$rootProject.buildDir/docs/javadoc")
                        classpath = rootProject.files(javaSubprojects.javadoc.classpath)

                        options.addBooleanOption('html5', true)
                        options.addStringOption 'Xdoclint:none', '-quiet'
                        options.encoding = 'utf-8'
                        options.docEncoding = 'utf-8'
                        options.charSet = 'utf-8'
                        options.links 'https://docs.spring.io/spring-framework/docs/current/javadoc-api/'
                        options.links 'https://fasterxml.github.io/jackson-databind/javadoc/2.8/'
                        options.author true
                        title = "$project.name $project.version API"
                    }
                }
            }
        }
    }

    static Set<Project> getJavaSubprojects(Project rootProject) {
        rootProject.subprojects.findAll { subproject -> subproject.plugins.hasPlugin(JavaPlugin) }
    }
}