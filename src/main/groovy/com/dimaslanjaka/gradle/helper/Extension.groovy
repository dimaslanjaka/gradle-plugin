package com.dimaslanjaka.gradle.helper

import org.gradle.api.Project

class Extension {
    private static Map<Object, Object> registered = new LinkedHashMap<>()

    /**
     * Create extension
     * @param project
     * @param name name of extension
     * @param aClass class of extension
     * @param any parameters for extension
     * @return class of extension
     */
    @SuppressWarnings("unused")
    static Object create(Project project, String name, Class aClass, Object... any) {
        Object ext = project.extensions.create(name, aClass, any)
        registered.put(name, ext)
        return ext
    }
}
