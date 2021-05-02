//file:noinspection GrUnnecessaryPublicModifier
package com.dimaslanjaka.gradle.helper;

import org.gradle.api.Project;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gradle api | project extension helper
 */
@SuppressWarnings("unused")
public class Extension {
    private static final Map<Object, Object> registered = new LinkedHashMap<>();
    public Project project;

    public Extension(Project project) {
        this.project = project;
    }

    /**
     * Get all registered extensions
     *
     * @return Map Registry
     */
    public static Map<Object, Object> getAll() {
        return registered;
    }

    /**
     * Create extension
     *
     * @param p                     Project instance
     * @param name                  name of extension
     * @param clazz                 class to registered
     * @param constructionArguments parameters for extension
     * @return class of extension
     */
    public static <T> Object create(Project p, String name, Class<T> clazz, Object... constructionArguments) {
        Object ext = p.getExtensions().create(name, clazz, constructionArguments);
        // add extension name to index key
        registered.put(name, ext);
        // add class index key
        registered.put(clazz.getName(), ext);
        // add unique identifier to index key
        String unique = p.getName() + name;
        registered.put(unique, ext);
        // add reverse unique identifier to index key
        unique = name + p.getName();
        registered.put(unique, ext);

        return ext;
    }

    /**
     * Get extension by project and extension name
     *
     * @param p    Project Instance
     * @param name Extension Name
     * @return Extension Instance
     */
    public static <T> Object get(Project p, String name) {
        return p.getExtensions().getByName(name);
    }

    /**
     * Get extension by extension name
     *
     * @param name Extension Name
     * @return Extension Instance
     */
    public static <T> Object get(String name) throws Exception {
        if (registered.containsKey(name)) {
            return registered.get(name);
        }
        throw new Exception("Extension with name " + name + " not registered");
    }

    /**
     * Get extension by registered class
     *
     * @param clazz registered extension class
     * @return Extension Instance
     */
    public static <T> Object get(Class<T> clazz) throws Exception {
        String named = clazz.getName();
        if (registered.containsKey(named)) {
            return registered.get(named);
        }
        throw new Exception("Extension with Class " + named + " not registered");
    }

    public <T> Object create(String name, Class<T> clazz, Object... constructionArguments) throws Exception {
        if (project == null) throw new Exception("Project not initialized");
        return create(project, name, clazz, constructionArguments);
    }
}
