package com.dimaslanjaka.gradle.offline_dependencies

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler
import org.gradle.internal.reflect.Instantiator

class Utils {
    static <K, V> void addToMultimap(Map<K, Set<V>> map, K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, [] as Set<V>)
        }
        map.get(key).add(value)
    }

    @SuppressWarnings('unused')
    static RepositoryHandler getRepositoryHandler(Project project) {
        RepositoryHandler repositoryHandler = new DefaultRepositoryHandler(
                project.services.get(BaseRepositoryFactory.class) as BaseRepositoryFactory,
                project.services.get(Instantiator.class) as Instantiator,
                project.services.get(CollectionCallbackActionDecorator.class) as CollectionCallbackActionDecorator
        )
        return repositoryHandler
    }

    /**
     * Transform string into GString groovy
     * @param string
     * @return string with instance of GString
     */
    @SuppressWarnings('unused')
    static GString parseGString(String string) {
        return "$string"
    }
}
