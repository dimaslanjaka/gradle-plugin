package com.dimaslanjaka.java

import com.dimaslanjaka.kotlin.File
import org.gradle.api.Project
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.lang.Thread as javaThread

open class Thread : java.lang.Thread, Runnable {
    private val rlock: ReentrantLock = ReentrantLock()
    lateinit var path: File
    lateinit var project: Project
    lateinit var lock: FileLock
    lateinit var fileChannel: FileChannel

    constructor(identifier: String) {
        setIdentifier(identifier)
    }

    constructor(p: Project, identifier: String) {
        setIdentifier(identifier)
        project = p
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
    }

    private fun lock() {
        fileChannel = FileChannel.open(
            path.toPath(), StandardOpenOption.READ,
            StandardOpenOption.WRITE
        )

        lock = fileChannel.lock(0, Long.MAX_VALUE, true)
        println("Lock acquired: " + lock.isValid)
        println("Lock is shared: " + lock.isShared)
    }

    fun setIdentifier(identifier: String) {
        path = File("build", "thread/$identifier")
        path.resolveParent()
    }

    companion object {
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