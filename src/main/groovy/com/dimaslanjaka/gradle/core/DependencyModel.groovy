package com.dimaslanjaka.gradle.core

import org.gradle.api.artifacts.Dependency
import org.jetbrains.annotations.Nullable

class DependencyModel implements Dependency {
    Dependency deps

    def Dependency(Dependency dependencyModel) {
        deps = dependencyModel
        this.groupId = dependencyModel.group
        this.name = dependencyModel.name
        this.version = dependencyModel.version
    }

    @Override
    String getGroup() {
        return deps.group
    }

    @Override
    String getName() {
        return deps.name
    }

    @Override
    String getVersion() {
        return deps.version
    }

    @Override
    boolean contentEquals(Dependency dependency) {
        return deps.contentEquals(dependency)
    }

    @Override
    Dependency copy() {
        return deps.copy()
    }

    @Override
    String getReason() {
        return deps.getReason()
    }

    @Override
    void because(@Nullable @javax.annotation.Nullable String reason) {
        deps.because(reason)
    }
}
