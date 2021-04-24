package com.dimaslanjaka.java

import com.dimaslanjaka.kotlin.ConsoleColors.Companion.println
import com.dimaslanjaka.kotlin.File
import java.io.IOException
import java.lang.Thread
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

object FileLockExclusiveRead {
    @Throws(IOException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val file = File("build", "thread/$javaClass").createFile()
        val path = Paths.get(file.absolutePath)
        val fileChannel = FileChannel.open(
            path, StandardOpenOption.READ,
            StandardOpenOption.WRITE
        )
        println("Lock: ${file.isFileLocked()}")
        println("File channel opened for read write. Acquiring lock...")
        val lock = fileChannel.lock() // gets an exclusive lock
        println("Lock: ${file.isFileLocked()}")
        println("Lock acquired: " + lock.isValid)
        println("Lock is shared: " + lock.isShared)
        val buffer = ByteBuffer.allocate(20)
        var noOfBytesRead = fileChannel.read(buffer)
        println("Buffer contents: ")
        while (noOfBytesRead != -1) {
            buffer.flip()
            print("    ")
            while (buffer.hasRemaining()) {
                print(buffer.get().toChar())
            }
            println(" ")
            buffer.clear()
            Thread.sleep(1000)
            noOfBytesRead = fileChannel.read(buffer)
        }
        fileChannel.close()
        println("\nClosing the channel and releasing lock.")
        println("Lock: ${file.isFileLocked()}")
    }
}