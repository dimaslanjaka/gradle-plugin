package jar

import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar

class Repack {
    def mavenVersion = '3.6.0'
    def jarjarVersion = '1.2.1'

    public Repack(Project root_project) {
        if (!root_project.getSubprojects().isEmpty()) {
            root_project.getSubprojects().each { Project sub_project ->
                initialize(sub_project)
            }
        }
        initialize(root_project);
    }

    private void initialize(Project project) {
        project.configurations.create("jarjar")
        project.configurations.create("thirdparty")
        project.dependencies.add("jarjar", "org.gradle.jarjar:jarjar:$jarjarVersion")
        project.dependencies.add("thirdparty", "org.apache.maven:maven-model-builder:$mavenVersion")

        String cleanProjectName = project.name.replaceAll("[^A-Za-z0-9]","");

        project.tasks.create("repack-${cleanProjectName}", Jar) { Jar jar ->
            jar.group = "build"
            jar.setArchiveName("repack-${cleanProjectName}-all")
            jar.setVersion(project.version as String)
            jar.setDestinationDir(new File("${project.buildDir}/libs"))

            doLast {
                project.ant {
                    taskdef name: "jarjar", classname: "com.tonicsystems.jarjar.JarJarTask", classpath: configurations.jarjar.asPath
                    jarjar(destfile: archivePath) {
                        configurations.all.each { originalJar ->
                            zipfileset(src: originalJar)
                        }
                        rule(pattern: 'groovy**', result: 'dimas.groovy.@1')
                        rule(pattern: 'kotlin**', result: 'dimas.kotlin.@1')
                        rule(pattern: 'guava**', result: 'dimas.guava.@1')
                        rule(pattern: 'org.**', result: 'dimas.org.@1')
                        rule(pattern: 'ai.**', result: 'dimas.ai.@1')
                        rule(pattern: 'io.**', result: 'dimas.io.@1')
                        rule(pattern: 'au.**', result: 'dimas.au.@1')
                        rule(pattern: 'com.**', result: 'dimas.com.@1')
                        rule(pattern: 'licenses.**', result: 'dimas.licenses.@1')
                    }
                }
            }
        }
    }
}
