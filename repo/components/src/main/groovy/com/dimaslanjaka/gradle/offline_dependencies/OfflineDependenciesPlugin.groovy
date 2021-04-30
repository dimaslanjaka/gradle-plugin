package com.dimaslanjaka.gradle.offline_dependencies


import groovy.json.JsonOutput
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler
import org.gradle.internal.reflect.Instantiator

class OfflineDependenciesPlugin implements Plugin<Project> {
    static final String EXTENSION_NAME = 'offlineDependencies'

    void setup(Project project) {
        apply(project);
    }

    @Override
    void apply(Project project) {
        RepositoryHandler repositoryHandler = new DefaultRepositoryHandler(
                project.services.get(BaseRepositoryFactory.class) as BaseRepositoryFactory,
                project.services.get(Instantiator.class) as Instantiator,
                project.services.get(CollectionCallbackActionDecorator.class) as CollectionCallbackActionDecorator
        )

        def extension = project.extensions.create(EXTENSION_NAME, OfflineDependenciesExtension, project, repositoryHandler)
        //println(JsonOutput.prettyPrint(JsonOutput.toJson(extension)))
        //println("Configured Extension: " + extension.toJson())

        project.task("updateOfflineRepository-${project.name}", type: UpdateOfflineRepositoryTask, group: "offline") {
            conventionMapping.root = { "${extension.root}" }
            conventionMapping.configurationNames = { extension.configurations }
            conventionMapping.buildscriptConfigurationNames = { extension.buildScriptConfigurations }
            conventionMapping.includeSources = { extension.includeSources }
            conventionMapping.includeJavadocs = { extension.includeJavadocs }
            conventionMapping.includePoms = { extension.includePoms }
            conventionMapping.includeIvyXmls = { extension.includeIvyXmls }
            conventionMapping.includeBuildscriptDependencies = { extension.includeBuildscriptDependencies }
        }
    }
}

