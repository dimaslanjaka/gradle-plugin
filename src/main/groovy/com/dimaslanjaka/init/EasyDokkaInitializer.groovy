package com.dimaslanjaka.init

/**
 * <p>EasyDokkaInitializer Class - class with EasyDokkaPlugin Gradle Script plugin initializers.</p>
 *
 * @since 1.1.0 Yokohama
 * @version 1.1.0 Yokohama
 * @author Alexander Savin
 */
final class EasyDokkaInitializer {

    private EasyDokkaInitializer() {
        throw new AssertionError()
    }

    /**
     * Downloads Groovy Class source file
     *
     * @param url Groovy Class source file url
     * @param name Groovy Class source file local repository name
     * @return Groovy Class source file
     */
    static File downloadGroovyClass(String url, String name) {
        File file = new File("${System.properties['user.home']}/.m2/repository/com/vorlonsoft/EasyDokkaPlugin/${name}")
        file.parentFile.mkdirs()
        if (file.exists()) {
            file.delete()
        }
        new URL(url).withInputStream { downloadStream ->
            file.withOutputStream { fileOutputStream ->
                fileOutputStream << downloadStream
            }
        }
        return file
    }
}