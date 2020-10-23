package com.dimaslanjaka.init

import org.gradle.api.Project
import org.gradle.api.reporting.dependencies.HtmlDependencyReportTask
import org.gradle.api.tasks.bundling.Jar

class ProjectInfo {

    static void apply(Project rootProject) {
        if (!rootProject.plugins.hasPlugin('project-report')) {
            rootProject.plugins.apply('project-report')
        }
        // TODO: Reporting for all projects
        rootProject.htmlDependencyReport { HtmlDependencyReportTask reporter ->
            reporter.projects = rootProject.allprojects
        }
        Set<Project> subprojects = getJavaSubprojects(rootProject)
        subprojects.add(rootProject)
        if (!subprojects.isEmpty()) {
            subprojects.each { Project sp ->
                dump(sp)
                if (sp.hasProperty('test')) {
                    sp.test.systemProperty "file.encoding", "UTF-8"
                }
                if (sp.hasProperty('javadoc')) {
                    sp.javadoc.options.charSet = 'utf-8'
                    sp.javadoc.options.docEncoding = 'utf-8'
                    sp.javadoc.options.encoding = 'utf-8'
                    sp.javadoc.options.addBooleanOption('html5', true)
                    sp.javadoc.options.addStringOption 'Xdoclint:none', '-quiet'
                }

                String subprojectName = sp.displayName.replaceAll("[^a-zA-Z0-9-_. ]", "")
                sp.tasks.create("listClasses_${subprojectName}") {
                    group = "gradle-plugin"
                    def tree = new File("$sp.buildDir/classes").listFiles(new FileFilter() {
                        @Override
                        boolean accept(File pathname) {
                            return pathname.name.endsWith("class")
                        }
                    })
                    tree.each { File file ->
                        println file
                    }
                }

                sp.task("testJar_${subprojectName}", type: Jar) {
                    group = "gradle-plugin"
                    classifier = 'tests'
                    if (sp.hasProperty('sourceSets')) {
                        from sp.sourceSets.main.output.classesDirs
                    } else if (sp.hasProperty('android')) {
                        if (sp.android.hasProperty('sourceSets')) {
                            from sp.android.sourceSets.main.output.classesDirs
                        }
                    }
                }
            }
        }
    }

    static void dump(Project current) {
        boolean hasKotlin = current.plugins.hasPlugin("kotlin-android-extensions") || current.plugins.hasPlugin("kotlin-android") || current.plugins.hasPlugin("kotlin-multiplatform") || current.plugins.hasPlugin("kotlin")
        boolean isAndroidProject = current.plugins.hasPlugin("com.android.library") || current.plugins.hasPlugin("com.android.application")
        String currentProjectName = current.displayName.replaceAll("[^a-zA-Z0-9-_. ]", "")

        String stringBuilder = ""
        stringBuilder += "------------------------------------\n"
        stringBuilder += "   _____               _ _          \n"
        stringBuilder += "  / ____|             | | |         \n"
        stringBuilder += " | |  __ _ __ __ _  __| | | ___     \n"
        stringBuilder += " | | |_ | '__/ _` |/ _` | |/ _ \\   \n"
        stringBuilder += " | |__| | | | (_| | (_| | |  __/    \n"
        stringBuilder += "  \\_____|_|  \\__,_|\\__,_|_|\\___|\n"
        stringBuilder += "                                    \n"
        stringBuilder += "------------------------------------\n"
        stringBuilder += "Using build file '$current.buildFile.name' in '$current.buildFile.parentFile.name'."
        if (current.configurations.hasProperty('runtime')) {
            current.configurations.runtime.each { File f -> stringBuilder += "runtime: $f\n" }
        }

        if (current.tasks.findByName("info_${current.name}") == null) {
            current.task("info_${current.name}") {
                stringBuilder += "\n"
                stringBuilder += "-----------------------------------------------\n"
                stringBuilder += "Project Name: ${current.name}\n"
                stringBuilder += "Version: ${current.version}\n"
                stringBuilder += "Home directory: ${current.rootDir}\n"
                if (current.hasProperty('compileJava')) stringBuilder += "Build output: ${current.compileJava.destinationDir}\n"
                if (current.hasProperty('processResources')) stringBuilder += "Resources output: ${current.processResources.destinationDir}\n"
                stringBuilder += "Has Kotlin: ${hasKotlin}\n"
                stringBuilder += "Android Project: ${isAndroidProject}\n"
                //println "Test output dir: " + relativePath(sourceSets.test.output.classesDirs)
                //println "Test result dir: " + relativePath(it.buildDir)
                //println "Report dir: " + relativePath("$buildDir/reports")
                stringBuilder += "-----------------------------------------------"
            }
        }

        new File("${current.rootProject.buildDir}/reports/${currentProjectName}").mkdirs()
        FileWriter writer = new FileWriter("${current.rootProject.buildDir}/reports/${currentProjectName}/runtime.txt")
        writer.write(stringBuilder)
        writer.close()
    }

    static Set<Project> getJavaSubprojects(Project rootProject) {
        return rootProject.subprojects
    }
}
