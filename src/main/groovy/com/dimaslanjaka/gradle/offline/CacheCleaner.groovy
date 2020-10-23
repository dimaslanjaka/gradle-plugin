package com.dimaslanjaka.gradle.offline

import com.dimaslanjaka.gradle.utils.Properties
import com.dimaslanjaka.gradle.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.lang.reflect.Type
import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

import static com.dimaslanjaka.gradle.utils.ConsoleColors.*

class CacheCleaner {
    static def home = System.properties['user.home']
    static String caches = "$home/.gradle/caches"
    static String[] cacheList = ["jars-", "build-cache-", "journal-", "modules-", "transforms-", "\\d{1,2}.\\d{1,2}"]
    static int max = 50
    static Project project

    static void main(String[] a) {
        listWildCard(getRandom(cacheList) as String)
    }

    static apply(Project project1) {

        project = project1

        Date today = new Date()
        def log = new File(project.buildDir, "tmp/${Utils.md5(this.class.name)}.properties")
        Properties orderedProperties = new Properties(log, true)
        def allowToModify = false
        if (!orderedProperties.contains("TIMESTAMP")) {
            allowToModify = true
        } else {
            def value = orderedProperties.get("TIMESTAMP").toLong()
            Date created = new Date(value)
            def difference = new Date().time - created.time
            if (difference > TimeUnit.MINUTES.toMillis(60)) {
                allowToModify = true
                orderedProperties.set("TIMESTAMP", today.getTime().toString())
            }
        }

        if (allowToModify) listWildCard(getRandom(cacheList) as String)
    }

    /**
     * List directory with wilcard
     * @param wilcardPath
     */
    static void listWildCard(String wilcardPath) {
        File dir = new File(caches)
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            boolean accept(File pathnamed) {
                boolean regex = pathnamed.name.startsWith(wilcardPath)
                regex = regex || pathnamed.name.endsWith(wilcardPath)
                regex = regex || Pattern.compile(wilcardPath)
                        .matcher(pathnamed.toString()).matches()
                return regex && pathnamed.isDirectory()
            }
        })
        shuffleArray(files)
        for (int i = 0; i < files.length; i++) {
            def jars = files[i]
            recursiveCheck(jars)
        }
    }

    /**
     * To prevent memory heap usage, we schedule it at next build/sync
     */
    static List<File> queuedScan = new ArrayList<>()

    static def toList(String value) {
        return [value]
    }

    static def toList(value) {
        value ?: []
    }

    /**
     * Directory Recursive Listing
     * @param directory directory to be listed
     */
    static void recursiveCheck(File directory) {
        List<File> list = new ArrayList<>()
        def get = directory.listFiles().toSorted()
        list.addAll(get)
        if (project != null) {
            def read = FileUtils.fileRead("${project.buildDir}/cacheCleanupQueues.json")
            Type listType = new TypeToken<File[]>() {}.getType()
            def json = new Gson().fromJson(read, listType) as File[]
            list.addAll(json)
        }

        shuffleArray(list)
        list.each { File test ->
            if (test != null) {
                BasicFileAttributes attr = Files.readAttributes(test.toPath(), BasicFileAttributes.class)
                if (test.isFile()) {
                    delete(attr, test)
                } else {
                    if (isDirEmpty(test.toPath())) {
                        delete(attr, test)
                    } else {
                        queuedScan.add(test.absoluteFile)
                    }
                }
            }
        }

        if (project != null) {
            FileUtils.fileWrite("${project.buildDir}/cacheCleanupQueues.json", new Gson().toJson(queuedScan))
        }
    }

    /**
     * Shuffle arrays
     * Implementing Fisher–Yates shuffle
     * @param ar array
     * @link "https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array"
     */
    static void shuffleArray(Object[] ar) {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current()
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1)
            // Simple swap
            Object a = ar[index]
            ar[index] = ar[i]
            ar[i] = a
        }
    }

    /**
     * Check if directory empty
     * @param directory directory to be checked
     * @return boolean true false
     */
    static boolean isDirEmpty(final Path directory) {
        try {
            DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)
            return !dirStream.iterator().hasNext()
        } catch (Exception ignored) {
            return false
        }
    }

    /**
     * Delete file and folder
     * @param attr file attributes to get last access time
     * @param test file/directory to be test
     */
    static void delete(BasicFileAttributes attr, File test) {
        long lastAcc = attr.lastAccessTime().toMillis()
        long access3days = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000)
        if (lastAcc < access3days) {
            if (test.canWrite()) {
                if (test.isFile() && test.delete()) {
                    print("${styler(CYAN, test.absolutePath)} ${styler(GREEN, "deleted")}")
                } else if (test.isDirectory() && test.deleteDir()) {
                    print("${styler(RED, test.name)} ${styler(GREEN, "deleted")}")
                }
            }
        }
    }

    /**
     * Get random item from array
     * @param array array to be picked
     * @return Object for any type
     */
    static Object getRandom(Object[] array) {
        if (array.size() > 0) {
            int rnd = new Random().nextInt(array.length)
            return array[rnd]
        }
        return array
    }

    /**
     * List only folder
     * @param pathname directory to be listed
     * @return File[] file array
     */
    static def listFolder(String pathname) {
        File file = new File(pathname)
        File[] files = file.listFiles(new FileFilter() {
            @Override
            boolean accept(File pathnamed) {
                return pathnamed.isDirectory()
            }
        })
        return files
    }

    static def listFiles(String pathname) {
        File file = new File(pathname)
        File[] files = file.listFiles(new FileFilter() {
            @Override
            boolean accept(File pathnamed) {
                return pathnamed.isFile()
            }
        })
        return files
    }

    /**
     * Directory recursive listing by walking them
     * @param path directory path to be listed
     */
    static void walk(String path) {

        File root = new File(path)
        File[] list = root.listFiles()

        if (list == null) return

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath())
                System.out.println("Dir:" + f.getAbsoluteFile())
            } else {
                System.out.println("File:" + f.getAbsoluteFile())
            }
        }
    }
}
