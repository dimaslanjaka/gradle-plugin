package com.dimaslanjaka.gradle.utils


import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class Jar {
    static boolean validate(String pathname) {
        try {
            ZipFile file = new ZipFile(new File(pathname))
            Enumeration<? extends ZipEntry> e = file.entries()
            int i = 0
            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement()
                //System.out.println(entry.getName());
                i++
            }
            if (i > 0) return true
        } catch (Exception ignored) {
            //ex.printStackTrace();
            return false
        }
    }
}
