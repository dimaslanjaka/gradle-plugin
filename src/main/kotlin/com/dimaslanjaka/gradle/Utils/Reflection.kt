package com.dimaslanjaka.gradle.Utils

import java.io.File
import java.lang.reflect.Modifier
import java.util.*

object Reflection {
    /**
     * ```kotlin
     * override fun toString() = Reflection.reflectionToString(this)
     * ```
     * @param obj thisClass (this)
     * @see "https://stackoverflow.com/questions/40862207/kotlin-generate-tostring-for-a-non-data-class"
     */
    fun toString(obj: Any): String {
        val s = LinkedList<String>()
        var clazz: Class<in Any>? = obj.javaClass
        while (clazz != null) {
            for (prop in clazz.declaredFields.filterNot { Modifier.isStatic(it.modifiers) }) {
                prop.isAccessible = true
                s += "${prop.name}=" + prop.get(obj)?.toString()?.trim()
            }
            clazz = clazz.superclass
        }
        val innerjoin = s.joinToString(",\n\t")
        return "${obj.javaClass.simpleName} = [\n\t${innerjoin}\n]"
    }

    fun toBrackets(obj: Any): String {
        val s = LinkedList<String>()
        var clazz: Class<in Any>? = obj.javaClass
        while (clazz != null) {
            for (prop in clazz.declaredFields.filterNot { Modifier.isStatic(it.modifiers) }) {
                prop.isAccessible = true
                s += "${prop.name}=" + prop.get(obj)?.toString()?.trim()
            }
            clazz = clazz.superclass
        }
        val innerjoin = s.joinToString(",\n\t")
        return "[$innerjoin]"
    }

    fun toProperties(obj: Any, outputFile: File): String {
        val s = LinkedList<String>()
        var clazz: Class<in Any>? = obj.javaClass
        while (clazz != null) {
            for (prop in clazz.declaredFields.filterNot { Modifier.isStatic(it.modifiers) }) {
                prop.isAccessible = true
                s += "${prop.name}=" + prop.get(obj)?.toString()?.trim()
            }
            clazz = clazz.superclass
        }
        val props = s.joinToString("\n")
        outputFile.writeText(props, Charsets.UTF_8)
        return props
    }
}