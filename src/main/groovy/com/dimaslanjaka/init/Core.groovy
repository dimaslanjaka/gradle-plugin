package com.dimaslanjaka.init

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.GradleBuild
import org.gradle.api.tasks.bundling.Jar

class Core {
    static void apply(Project project) {
        project.afterEvaluate {
            final boolean androidOs = EasyDokkaUtils.with(project).isAndroid()
            final boolean kotlinLanguage = EasyDokkaUtils.with(project).isKotlin()
            final String dokkaFatJarVersion = EasyDokka.with(project).getDokkaFatJarVersion()
            final String dokkaFatJarPath = 'org/jetbrains/dokka/dokka-fatjar'
            final String dokkaOutputFormat = EasyDokka.with(project).getDokkaOutputFormat()
            final String javaAPISpecificationLink = EasyDokkaUtils.getJavaAPISpecificationLink(JavaVersion.current().toString())
            final def buildDir = project.buildDir

            project.task("easyDokkaInitializer") {
                EasyDokkaUtils.downloadLib("https://jcenter.bintray.com/${dokkaFatJarPath}/${dokkaFatJarVersion}/dokka-fatjar-${dokkaFatJarVersion}.pom",
                        dokkaFatJarPath, dokkaFatJarVersion, "dokka-fatjar-${dokkaFatJarVersion}.pom")
                EasyDokkaUtils.downloadLib("https://jcenter.bintray.com/${dokkaFatJarPath}/${dokkaFatJarVersion}/dokka-fatjar-${dokkaFatJarVersion}.jar",
                        dokkaFatJarPath, dokkaFatJarVersion, "dokka-fatjar-${dokkaFatJarVersion}.jar")
            }

            project.task("dokkaJavadocsJar", type: GradleBuild) {
                description 'Generates the documentation by Dokka in Javadoc or other formats.'
                tasks = ['clean', 'easyDokkaJar']
            }

            project.task("easyDokka", type: Exec/*, dependsOn: EasyDokkaInitializer*/) {
                description 'Generates the documentation by Dokka.'
                byte srcDirsJavaNumber
                byte srcDirsKotlinNumber
                byte classpathNumber
                String classpath
                String srcDirOne
                String srcDirTwo
                String srcDirThree
                String links
                if (androidOs) {
                    srcDirsJavaNumber = (byte) ((project.android.sourceSets.main.java.srcDirs != null) ? android.sourceSets.main.java.srcDirs.size() : 0)
                    srcDirsKotlinNumber = (byte) ((kotlinLanguage && (android.sourceSets.main.kotlin.srcDirs != null)) ? android.sourceSets.main.kotlin.srcDirs.size() : 0)
                    classpathNumber = (byte) ((android.getBootClasspath() != null) ? android.getBootClasspath().size() : 0)
                    classpath = (classpathNumber > 0) ? android.getBootClasspath()[0] : ''
                    srcDirOne = (srcDirsJavaNumber > 0) ? android.sourceSets.main.java.srcDirs[0] : ''
                    srcDirTwo = (srcDirsJavaNumber > 1) ? android.sourceSets.main.java.srcDirs[1] : ''
                    srcDirThree = (srcDirsKotlinNumber > 0) ? android.sourceSets.main.kotlin.srcDirs[0] : ''
                    links = 'https://developer.android.com/reference/^https://developer.android.com/reference/package-list^^' +
                            "file:///${android.sdkDirectory}/docs/reference/^file:///${android.sdkDirectory}/docs/reference/package-list^^" +
                            "${javaAPISpecificationLink}^${javaAPISpecificationLink}"
                    if (javaAPISpecificationLink == 'https://docs.oracle.com/javase/10/docs/api/') {
                        links += 'element-list'
                    } else {
                        links += 'package-list'
                    }
                    if (srcDirsJavaNumber > 0) {
                        mkdir android.sourceSets.main.java.srcDirs[0]
                        if (srcDirsJavaNumber > 1) {
                            mkdir android.sourceSets.main.java.srcDirs[1]
                        }
                    }
                    if (srcDirsKotlinNumber > 0) {
                        mkdir android.sourceSets.main.kotlin.srcDirs[0]
                    }
                } else if (project.hasProperty("sourceSets")) {
                    srcDirsJavaNumber = (byte) ((project.sourceSets.main.java.srcDirs != null) ? project.sourceSets.main.java.srcDirs.size() : 0)
                    srcDirsKotlinNumber = (byte) ((kotlinLanguage && (sourceSets.main.kotlin.srcDirs != null)) ? project.sourceSets.main.kotlin.srcDirs.size() : 0)
                    classpathNumber = 0
                    classpath = ''
                    srcDirOne = (srcDirsJavaNumber > 0) ? project.sourceSets.main.java.srcDirs[0] : ''
                    srcDirTwo = (srcDirsJavaNumber > 1) ? project.sourceSets.main.java.srcDirs[1] : ''
                    srcDirThree = (srcDirsKotlinNumber > 0) ? project.sourceSets.main.kotlin.srcDirs[0] : ''
                    links = javaAPISpecificationLink
                    if (srcDirsJavaNumber > 0) {
                        new File(project.sourceSets.main.java.srcDirs[0].toString()).mkdirs()
                        if (srcDirsJavaNumber > 1) {
                            new File(project.sourceSets.main.java.srcDirs[1].toString()).mkdirs()
                        }
                    }
                    if (srcDirsKotlinNumber > 0) {
                        project.sourceSets.main.kotlin.srcDirs[0].mkdirs()
                    }
                }
                if (srcDirsJavaNumber + srcDirsKotlinNumber > 0) {
                    commandLine 'java', '-jar',
                            "${System.properties['user.home']}/.m2/repository/${dokkaFatJarPath}/${dokkaFatJarVersion}/dokka-fatjar-${dokkaFatJarVersion}.jar",
                            srcDirOne, srcDirTwo, srcDirThree, '-output', "${buildDir}/${dokkaOutputFormat}", '-format', dokkaOutputFormat, '-links', links,
                            (classpathNumber > 0) ? '-classpath' : '', (classpathNumber > 0) ? classpath : ''
                } else {
                    commandLine 'java', '-jar',
                            "${System.properties['user.home']}/.m2/repository/${dokkaFatJarPath}/${dokkaFatJarVersion}/dokka-fatjar-${dokkaFatJarVersion}.jar"
                }
            }

            project.task("easyDokkaJar", type: Jar) {
                description = "Archives the documentation created by Dokka."
                classifier = dokkaOutputFormat
                destinationDir = buildDir
                from "${buildDir}/${dokkaOutputFormat}"
            }
            project.tasks.findByName("easyDokkaJar").dependsOn("easyDokka")
        }
    }
}
