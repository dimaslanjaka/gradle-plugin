package com.dimaslanjaka.gradle.Utils

import nu.studer.java.util.OrderedProperties

class Properties {
    File pFile
    OrderedProperties prop
    boolean autoSave = false
    def signature = ""

    Properties(File file, boolean autosave, String sign) {
        pFile = file
        readProp(file)
        autoSave = autosave
        signature = sign
    }

    Properties(File file, boolean autosave) {
        this(file, autosave, null)
    }

    Properties(File file) {
        this(file, false, null)
    }

    Properties set(String key, String value) {
        prop.setProperty(key, value)
        if (autoSave) saveProp(prop, pFile)
        return this
    }

    Properties setProperty(String key, String value) {
        return set(key, value)
    }

    String get(String key) {
        return prop.getProperty(key)
    }

    String getProperty(String key) {
        return get(key)
    }

    String get(String key, Object defaultValue) {
        if (prop.hasProperty(key)) return prop.getProperty(key)
        return defaultValue
    }

    Properties add(String key, String value) {
        return set(key, value)
    }

    Properties remove(String key) {
        prop.removeProperty(key)
        if (autoSave) saveProp(prop, pFile)
        return this
    }

    boolean contains(String key) {
        return prop.containsProperty(key)
    }

    boolean hasProperty(String key) {
        return contains(key)
    }

    OrderedProperties readProp(File file) {
        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        if (!file.exists()) file.createNewFile()
        prop = new OrderedProperties()
        FileInputStream fis = new FileInputStream(file)
        prop.load(fis)
        fis.close()
        return prop
    }

    void saveProp(OrderedProperties properties, File file) {
        Writer writer = file.newWriter()
        properties.store(writer, signature)
        writer.close()
    }
}