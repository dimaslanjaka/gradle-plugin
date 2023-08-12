package com.dimaslanjaka.java;

import static com.dimaslanjaka.kotlin.ConsoleColors.println;

import com.dimaslanjaka.gradle.plugin.File;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


public class Resources {
    /**
     * Reads given resource file as a string.
     *
     * @param fileName path to the resource file
     * @return the file's contents
     * @throws IOException if read fails for any reason
     */
    public static String getResourceFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(fileName)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    /**
     * Copy file
     *
     * @param src  source file
     * @param dest destination file
     * @throws IOException IO throwable
     */
    public static void copy(String src, File dest) throws IOException {
        String read = getResourceFileAsString(src);
        dest.write(read);
        println(src + " Copied to " + dest);
    }
}
