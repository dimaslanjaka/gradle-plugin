package com.dimaslanjaka.gradle.offline

import org.gradle.api.Project
import org.gradle.util.GradleVersion

class ThirdPartyDependenciesExtension {
    public Project project = null

    ThirdPartyDependenciesExtension(Project project1) {
        project = project1
    }

    def compile(Map dependency) {
        if (GradleVersion.current() < GradleVersion.version("4.0")) {
            add("compile", dependency)
        } else {
            add("implementation", dependency)
        }
    }

    def add(String scope, Map dependency) {
        def id = dependency.id
        def group = dependency.group
        def version = dependency.version
        project.dependencies {
            delegate."$scope"("$id:$group:$version")
        }
    }
}