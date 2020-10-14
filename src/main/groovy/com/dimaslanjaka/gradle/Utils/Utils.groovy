package com.dimaslanjaka.gradle.Utils


import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

final class Utils {
    static <K, V> void addToMultimap(Map<K, Set<V>> map, K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, [] as Set<V>)
        }
        map.get(key).add(value)
    }

    static boolean isWindows = System.getProperty("os.name")
            .toLowerCase(Locale.ROOT)
            .contains("windows")

    static String md5(String md5) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5")
            byte[] array = md.digest(md5.getBytes())
            StringBuffer sb = new StringBuffer()
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3))
            }
            return sb.toString()
        } catch (NoSuchAlgorithmException e) {
            println(e.message)
        }
        return null
    }
}
