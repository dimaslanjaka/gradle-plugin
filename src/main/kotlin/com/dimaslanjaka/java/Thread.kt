package com.dimaslanjaka.java

import com.dimaslanjaka.gradle.api.Extension
import com.dimaslanjaka.gradle.plugin.Core
import com.dimaslanjaka.gradle.plugin.CoreExtension
import com.dimaslanjaka.kotlin.File
import org.gradle.api.Project
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.nio.file.StandardOpenOption
import java.util.*
import java.lang.Thread as javaThread

@Suppress("MemberVisibilityCanBePrivate")
open class Thread : java.lang.Thread, Runnable {
    lateinit var name_thread: String
    lateinit var path: File
    lateinit var project: Project
    lateinit var lock: FileLock
    lateinit var fileChannel: FileChannel
    lateinit var configuration: CoreExtension
    var debug = false

    constructor(identifier: String) {
        setIdentifier(identifier)
    }

    constructor(p: Project, identifier: String) {
        project = p
        setIdentifier(identifier)
        configuration = Extension.getExtension<CoreExtension>(
            project, Core.CONFIG_NAME
        ) as CoreExtension
        debug = configuration.debug
    }

    constructor(r: Runnable) : super(r) {}

    /**
     * Synchronized thread
     */
    fun sync(runnable: Runnable) {
        synchronized(path) {
            javaThread(runnable).start()
        }
    }

    /**
     * FileLock thread
     */
    fun lock(runnable: Runnable) {
        if (!isFileLocked(path)) {
            lock()
            val newRunnable = Runnable {
                runnable.run()
                release()
            }
            javaThread(newRunnable).start()
        }
    }

    private fun release() {
        lock.release()
        fileChannel.close()
        if (debug) println("Lock thread $name_thread released")
    }

    @Throws(java.io.IOException::class)
    private fun lock() {
        fileChannel = FileChannel.open(
            path.toPath(), StandardOpenOption.READ,
            StandardOpenOption.WRITE
        )

        lock = fileChannel.lock(0, Long.MAX_VALUE, true)
        if (debug) {
            println("Lock thread $name_thread is acquired: " + lock.isValid)
            println("Lock thread $name_thread is shared: " + lock.isShared)
        }
    }

    /**
     * Set thread identifier for lock filename,
     * each thread only run once when lockfile available(unlocked)
     */
    fun setIdentifier(identifier: String) {
        name_thread = identifier
        path = if (this::project.isInitialized) {
            File(project.buildDir.absolutePath, "thread/${identifier}.txt")
        } else {
            File("build", "thread/${identifier}.txt")
        }
        path.resolveParent()
    }

    companion object {
        /**
         * Check file is locked
         */
        @JvmStatic
        fun isFileLocked(p_fi: File): Boolean {
            var bLocked = false
            try {
                RandomAccessFile(p_fi, "rw").use { fis ->
                    val lck: FileLock = fis.channel.lock()
                    lck.release()
                }
            } catch (ex: java.lang.Exception) {
                bLocked = true
            }
            if (bLocked) return bLocked
            // try further with rename
            val parent = p_fi.parent
            val rnd = UUID.randomUUID().toString()
            val newName = File("$parent/$rnd")
            if (p_fi.renameTo(newName)) {
                newName.renameTo(p_fi)
            } else bLocked = true
            return bLocked
        }
    }
}

fun main() {
    val path = File("build", "thread/main")
    path.write(Date())
    val fileChannel: FileChannel = FileChannel.open(path.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE)
    fileChannel.lock()
    println(Thread.isFileLocked(path))
}