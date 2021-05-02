//file:noinspection GrMethodMayBeStatic
//file:noinspection unused
package com.dimaslanjaka.gradle.offline_dependencies

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler
import org.gradle.internal.reflect.Instantiator
import org.slf4j.LoggerFactory

import static com.dimaslanjaka.gradle.helper.Extension.create

class Plugin implements org.gradle.api.Plugin<Project> {
    public static final String EXTENSION_NAME = 'offlineDependencies'

    public void apply(Project project, Extension extension) {
        createTask(project, extension)
    }

    public void apply(ExtensionInterface configuration) {
        def extension = createExtension(configuration.project)
        extension.setRoot(configuration.root)
        extension.setProject(configuration.project)
        extension.setRepositoryHandler(configuration.repositoryHandler)
        extension.configurations = configuration.configurations
        extension.buildScriptConfigurations = configuration.buildScriptConfigurations
        extension.includeSources = configuration.includeSources
        extension.includeJavadocs = configuration.includeJavadocs
        extension.includePoms = configuration.includePoms
        extension.includeIvyXmls = configuration.includeIvyXmls
        extension.includeBuildscriptDependencies = configuration.includeBuildscriptDependencies
        createTask(configuration.project, extension)
    }

    @Override
    void apply(Project project) {
        createTask(project, createExtension(project))
    }

    public Extension createExtension(Project project) {
        return create(
                project,
                EXTENSION_NAME, Extension.class, project, createRepositoryHandler(project)
        ) as Extension

        /*
        return project.getExtensions().create(
                EXTENSION_NAME, Extension.class, project, createRepositoryHandler(project)
        )
         */
    }

    public RepositoryHandler createRepositoryHandler(Project project) {
        return new DefaultRepositoryHandler(
                project.services.get(BaseRepositoryFactory.class) as BaseRepositoryFactory,
                project.services.get(Instantiator.class) as Instantiator,
                project.services.get(CollectionCallbackActionDecorator.class) as CollectionCallbackActionDecorator
        )
    }

    public void createTask(Project project, Extension extension) {
        String sanitizeName = sanitizeFilename(project.name)
        String taskName = sanitizeFilename("Update Offline Repository-${sanitizeName}")
        /*
        project.task(taskName, type: UpdateTask, group: "offline") {
            conventionMapping.root = { "${extension.root}" }
            conventionMapping.configurationNames = { extension.configurations }
            conventionMapping.buildscriptConfigurationNames = { extension.buildScriptConfigurations }
            conventionMapping.includeSources = { extension.includeSources }
            conventionMapping.includeJavadocs = { extension.includeJavadocs }
            conventionMapping.includePoms = { extension.includePoms }
            conventionMapping.includeIvyXmls = { extension.includeIvyXmls }
            conventionMapping.includeBuildscriptDependencies = { extension.includeBuildscriptDependencies }
            conventionMapping.debug = { extension.debug }
        }
        */

        project.tasks.create(taskName, UpdateTask.class) {
            group = "offline"
            root = "${extension.root}"
            configurationNames = extension.configurations
            buildscriptConfigurationNames = extension.buildScriptConfigurations
            includeSources = extension.includeSources
            includeJavadocs = extension.includeJavadocs
            includePoms = extension.includePoms
            includeIvyXmls = extension.includeIvyXmls
            includeBuildscriptDependencies = extension.includeBuildscriptDependencies
            debug = extension.debug
        }
    }

    public String sanitizeFilename(String inputName) {
        return inputName.replaceAll("[^a-zA-Z0-9-_.]", "_");
    }
}

