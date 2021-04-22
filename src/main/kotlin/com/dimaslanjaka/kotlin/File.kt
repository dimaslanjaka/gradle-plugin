package com.dimaslanjaka.kotlin

import java.io.File

object File {
    @Suppress("unused")
    @JvmStatic
    fun isSameFileSize(file1: File, file2: File): Boolean {
        return if (!file1.exists() || !file2.exists()) {
            false
        } else {
            file1.length().compareTo(file2.length()) == 0
        }
    }

    @Suppress("unused")
    @JvmStatic
    fun isNotSameFileSize(file1: File, file2: File): Boolean {
        return !isSameFileSize(file1, file2)
    }
}