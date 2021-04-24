package com.dimaslanjaka.gradle.api;

import org.gradle.api.Project;

public class Extension {
    public static <T> Object createExtension(Project p, String name, Class<T> clazz, Object... constructionArguments) {
        return p.getExtensions().create(name, clazz, constructionArguments);
    }

    public static <T> Object getExtension(Project p, String name) {
        return p.getExtensions().getByName(name);
    }
}
