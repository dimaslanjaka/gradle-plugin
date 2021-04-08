package io.pry.gradle.offline_dependencies.maven

import dimas.org.apache.maven.model.Dependency
import dimas.org.apache.maven.model.Parent
import dimas.org.apache.maven.model.Repository
import dimas.org.apache.maven.model.building.FileModelSource
import dimas.org.apache.maven.model.building.ModelSource2
import dimas.org.apache.maven.model.resolution.InvalidRepositoryException
import dimas.org.apache.maven.model.resolution.ModelResolver
import dimas.org.apache.maven.model.resolution.UnresolvableModelException
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.result.UnresolvedArtifactResult
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.DefaultModuleIdentifier
import org.gradle.internal.component.external.model.DefaultModuleComponentIdentifier
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact

class PomDependencyModelResolver implements ModelResolver {

    private Project project
    private Map<String, FileModelSource> pomCache = [:]
    private Map<ModuleComponentIdentifier, File> componentCache = [:]

    PomDependencyModelResolver(Project project) {
        this.project = project
    }

    @Override
    ModelSource2 resolveModel(Parent parent) throws UnresolvableModelException {
        return resolveModel(parent.groupId, parent.artifactId, parent.version)
    }

    @Override
    ModelSource2 resolveModel(Dependency dependency) throws UnresolvableModelException {
        return resolveModel(dependency.groupId, dependency.artifactId, dependency.version)
    }

    @Override
    ModelSource2 resolveModel(String groupId, String artifactId, String version) throws UnresolvableModelException {
        def id = "$groupId:$artifactId:$version".toString()

        if (!pomCache.containsKey(id)) {
            def mavenArtifacts = project.dependencies.createArtifactResolutionQuery()
                    .forComponents(DefaultModuleComponentIdentifier.newId(DefaultModuleIdentifier.newId(groupId, artifactId), version))
                    .withArtifacts(MavenModule, MavenPomArtifact)
                    .execute()

            def component = mavenArtifacts.resolvedComponents.first()

            def poms = component.getArtifacts(MavenPomArtifact)
            if (poms?.empty) {
                return null
            }

            def pomArtifact = poms.first()

            if (pomArtifact instanceof UnresolvedArtifactResult) {
                logger.error("Resolver was unable to resolve artifact '{}'", pomArtifact.id, pomArtifact.getFailure())
                return null
            }

            def pomFile = pomArtifact.file as File

            def componentId = DefaultModuleComponentIdentifier.newId(DefaultModuleIdentifier.newId(groupId, artifactId), version)
            componentCache[componentId] = pomFile

            def pom = new FileModelSource(pomFile)
            pomCache[id] = pom
            return pom
        }


        return pomCache[id]
    }

    @Override
    void addRepository(Repository repository, boolean replace) throws InvalidRepositoryException {}

    @Override
    void addRepository(Repository repository) throws InvalidRepositoryException {}

    @Override
    ModelResolver newCopy() { return this }

    public componentCache() {
        return this.componentCache
    }
}
