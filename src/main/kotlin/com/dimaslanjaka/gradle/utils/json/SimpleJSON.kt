package com.dimaslanjaka.gradle.utils.json

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

/**
 * Super Simple JSON Helper Java
 */
object SimpleJSON {
    /**
     * Convert Map to JSON and write to a file
     */
    fun toJson(saveFile: File?, stringObjectMap: MutableMap<String?, Any?>?) {
        val mapper = ObjectMapper()
        try {
            mapper.writeValue(saveFile, stringObjectMap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Convert List to JSON and write to a file
     *
     * @usage SimpleJSON.toJson(file, Arrays.asList ( list.toArray ()));
     */
    fun toJson(saveFile: File?, mapList: MutableList<Any?>?) {
        val mapper = ObjectMapper()
        try {
            mapper.writeValue(saveFile, mapList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Read JSON from a file into a Map
     */
    fun fromJson(loadFile: File?): MutableMap<String?, Any?>? {
        val mapper = ObjectMapper()
        try {
            val type: TypeReference<MutableMap<String?, Any?>?> = object : TypeReference<MutableMap<String?, Any?>?>() {}
            return mapper.readValue(loadFile, type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}