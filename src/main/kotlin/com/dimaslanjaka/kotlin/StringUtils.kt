package com.dimaslanjaka.kotlin

class StringUtils {
    companion object {
        @JvmStatic
        fun getLastSlashChar(string: String): String {
            return string.substring(string.lastIndexOf("/") + 1, string.indexOf("."))
        }
    }
}