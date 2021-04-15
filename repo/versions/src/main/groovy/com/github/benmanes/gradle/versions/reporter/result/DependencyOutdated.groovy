package com.github.benmanes.gradle.versions.reporter.result

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

@CompileStatic
@TupleConstructor(callSuper = true, includeSuperProperties = true, includeSuperFields = true)
class DependencyOutdated extends Dependency {
  VersionAvailable available
}
