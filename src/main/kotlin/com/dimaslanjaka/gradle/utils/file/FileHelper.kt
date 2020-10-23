package com.dimaslanjaka.gradle.utils.file

import com.google.gson.Gson
import org.apache.commons.io.FileUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class FileHelper {
    companion object {
        private val FILE_NAME: String? = "hackers_%s.csv"
        private val FILE_NAME_EXTENSION: String? = ".jpg"
        private val NEW_LINE: String? = "\n"

        @Throws(IOException::class)
        @JvmStatic
        fun createFor(image: ByteArray?, fileName: String?) {
            val file = File(formatPath(fileName, FILE_NAME_EXTENSION))
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(image)
            fileOutputStream.close()
        }

        @Throws(IOException::class)
        @JvmStatic
        fun getFileWriter(line: String?) {
            val fileWriter = FileWriter(getName(), true)
            fileWriter.write(line)
            fileWriter.close()
        }

        @Throws(IOException::class)
        @JvmStatic
        fun readFile(): MutableList<String?>? {
            val file = File(getName())
            val lines: MutableList<String?> = ArrayList()
            if (file.exists()) {
                BufferedReader(FileReader(getName())).use { fileReader ->
                    var line = fileReader.readLine()
                    while (line != null) {
                        lines.add(line)
                        line = fileReader.readLine()
                    }
                }
            }
            return lines
        }

        @JvmStatic
        fun formatPath(fileName: String?, extension: String?): String? {
            getPhotoPath()?.let { createFolderIfDoesNotExistsFor(it) }
            return String.format("%s%s%s", getPhotoPath(), fileName, extension)
        }

        @JvmStatic
        fun delete(filePath: String?): Boolean {
            val file = File(filePath)
            return if (file.exists()) {
                file.delete()
            } else false
        }

        @JvmStatic
        fun getFilesFrom(filePath: String?): MutableList<File?>? {
            val folder = File(filePath)
            val files: MutableList<File?> = ArrayList()
            for (file in folder.listFiles()) {
                if (file.isFile && isCsv(file)) {
                    files.add(file)
                }
            }
            return files
        }

        @Throws(IOException::class)
        @JvmStatic
        fun merge(files: MutableList<File?>?, outputFile: File) {
            if (files != null) {
                for (file in files) {
                    val stream = FileInputStream(file!!.absolutePath)
                    val reader = InputStreamReader(stream)
                    val bufferedReader = BufferedReader(reader)
                    var line = bufferedReader.readLine()
                    while (line != null) {
                        addLine(outputFile.toString(), line)
                        line = bufferedReader.readLine()
                    }
                }
            }
        }

        @Throws(FileNotFoundException::class)
        @JvmStatic
        fun getStreamFor(fileName: String?): StreamHelper? {
            return StreamHelper(fileName)
        }

        @JvmStatic
        private fun getPhotoPath(): String? {
            return System.getenv()[EnvironmentVariable.PHOTO_PATH] + "/"
        }

        @JvmStatic
        private fun isCsv(file: File?): Boolean {
            if (file != null) {
                return file.name.endsWith(".csv")
            }
            return false
        }

        @JvmStatic
        @Throws(IOException::class)
        private fun addLine(filePath: String, content: String) {
            val fileWriter = FileWriter(filePath, true)
            fileWriter.write(content + NEW_LINE)
            fileWriter.close()
        }

        @JvmStatic
        //public static Logger log = LoggerFactory.getLogger(FileHelper.class);
        fun getName(): String? {
            val dateFormat = SimpleDateFormat("yyyyMMdd")
            val date = Date()
            return String.format(getFilePath() + FILE_NAME, dateFormat.format(date))
        }

        @JvmStatic
        fun getFilePath(): String? {
            return System.getenv()[EnvironmentVariable.FILE_PATH] + "/"
        }

        @Throws(IOException::class)
        fun copy(source: File?, target: File?) {
            FileUtils.copyFile(source, target)
        }

        /**
         * Check is a file is writable.
         * Detects write issues on external SD card.
         *
         * @param file The file.
         * @return true if the file is writable.
         */
        @JvmStatic
        fun isWritable(file: File?): Boolean {
            val isExisting = file!!.exists()
            try {
                val output = FileOutputStream(file, true)
                try {
                    output.close()
                } catch (e: IOException) {
                    // do nothing.
                }
            } catch (e: FileNotFoundException) {
                if (!file.isDirectory) {
                    return false
                }
            }
            val result = file.canWrite()

            // Ensure that file is not created during this process.
            if (!isExisting) {
                file.delete()
            }
            return result
        }

        /**
         * Checks if file could be read or created
         *
         * @param file - The file (as a String).
         * @return true if file's is writable.
         */
        @JvmStatic
        fun isReadable(file: String): Boolean {
            return isReadable(File(file))
        }

        /**
         * Checks if file could be read or created
         *
         * @param file - The file.
         * @return true if file's is writable.
         */
        @JvmStatic
        fun isReadable(file: File?): Boolean {
            if (file != null) {
                if (!file.isFile) {
                    println("isReadable(): Not a File")
                    return false
                }
                return file.exists() && file.canRead()
            }
            return false
        }

        @JvmStatic
        fun delete(path: File?, vararg exclude: String?): MutableList<Boolean?>? {
            val excludedPaths = listOf(*exclude)
            val result: MutableList<Boolean?> = ArrayList()
            val files = path?.listFiles()
            if (files != null) {
                for (file in files) {
                    if (!excludedPaths.contains(file.name)) {
                        if (file.isDirectory) {
                            delete(file)
                        }
                        result.add(file.delete())
                    }
                }
            }
            return result
        }

        @JvmStatic
        fun findFile(folder: File?, regex: Pattern?): File? {
            val files = folder?.listFiles()
            if (files != null) {
                for (file in files) {
                    if (regex != null) {
                        if (regex.matcher(file.name).find()) {
                            return file
                        }
                    }
                }
            }
            return null
        }

        @JvmStatic
        fun createFolderIfDoesNotExistsFor(folderName: String): Boolean {
            val folderFile = File(folderName)
            return if (!folderFile.exists()) {
                folderFile.mkdir()
            } else false
        }

        @JvmStatic
        @Throws(IOException::class)
        fun createFileIfDoesNotExistsFor(fileName: String): Boolean {
            val file = File(fileName)
            return if (!file.exists()) {
                file.createNewFile()
            } else false
        }

        @JvmStatic
        fun createNew(path: Any, data: Any): Boolean {
            var filepath: File? = null
            var filedata: String? = ""
            if (path is String) {
                filepath = File(path)
            } else if (path is File) {
                filepath = path
            }
            filedata = if (data is String) {
                data
            } else if (data is JSONObject || data is JSONArray || data is Gson) {
                data.toString()
            } else {
                data.toString()
            }
            if (filepath != null) {
                try {
                    createFileIfDoesNotExistsFor(filepath.absolutePath)
                    return if (filepath.canWrite()) {
                        val myWriter = FileWriter(filepath)
                        myWriter.write(filedata)
                        myWriter.close()
                        true
                    } else {
                        false
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return false
        }
    }
}