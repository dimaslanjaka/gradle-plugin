package com.dimaslanjaka.gradle.Utils

final class Utils {
    static <K, V> void addToMultimap(Map<K, Set<V>> map, K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, [] as Set<V>)
        }
        map.get(key).add(value)
    }

    boolean isWindows = System.getProperty("os.name")
            .toLowerCase(Locale.ROOT)
            .contains("windows")
}
