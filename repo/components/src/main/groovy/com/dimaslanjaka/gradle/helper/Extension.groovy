//file:noinspection GrUnnecessaryPublicModifier
package com.dimaslanjaka.gradle.helper

import org.gradle.api.Project

class Extension {
    private static Map<Object, Object> registered = new LinkedHashMap<>()
    Project project;

    public Extension(Project project) {
        this.project = project
    }

    public <T> Object create(String name, Class<T> clazz, Object... constructionArguments) {
        if (project == null) throw new Exception("Project not initialized")
        return create(project, name, clazz, constructionArguments)
    }

    /**
     * Create extension
     * @param project
     * @param name name of extension
     * @param aClass class to registered
     * @param any parameters for extension
     * @return class of extension
     */
    @SuppressWarnings("unused")
    public static <T> Object create(Project p, String name, Class<T> clazz, Object... constructionArguments) {
        Object ext = p.getExtensions().create(name, clazz, constructionArguments);
        // add extension name to index key
        registered.put(name, ext);
        // add class index key
        registered.put(clazz.name.toString(), ext);
        return ext
    }

    /**
     * Get extension by project and extension name
     * @param p
     * @param name
     * @return
     */
    public static <T> Object get(Project p, String name) {
        return p.getExtensions().getByName(name);
    }

    /**
     * Get extension by extension name
     * @param name
     * @return
     */
    public static <T> Object get(String name) {
        if (registered.containsKey(name)) {
            return registered.get(name)
        }
        throw new Exception("Extension with name $name not registered")
    }

    /**
     * Get extension by registered class
     * @param clazz registered class
     * @return
     */
    public static <T> Object get(Class<T> clazz) {
        String named = clazz.name.toString()
        if (registered.containsKey(named)) {
            return registered.get(named)
        }
        throw new Exception("Extension with Class $named not registered")
    }
}
