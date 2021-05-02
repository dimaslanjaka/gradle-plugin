package com.dimaslanjaka.gradle.api;

import org.gradle.api.Project;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BiConsumer;

public class Extension extends com.dimaslanjaka.gradle.helper.Extension {
    public Extension(Project project) {
        super(project);
    }

    public <T> Object create(String name, Class<T> clazz, Object... args) {
        return create(getProject(), name, clazz, args);
    }

    @Nullable
    public <T> Object get(Object identifier) {
        Map<?, ?> map = getAll();
        final Object[] result = {null};
        map.forEach(new BiConsumer<Object, Object>() {
            @Override
            public void accept(Object o, Object o2) {
                if (o.equals(identifier)) {
                    result[0] = o;
                }
            }
        });
        return result[0];
    }
}
