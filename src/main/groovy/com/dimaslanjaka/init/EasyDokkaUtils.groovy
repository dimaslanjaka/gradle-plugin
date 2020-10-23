package com.dimaslanjaka.init
/*
 * Copyright 2018 Vorlonsoft LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * <p>com.dimaslanjaka.init.EasyDokkaUtils Class - utility class, thread-safe and a fast singleton
 * implementation.</p>
 *
 * @since 1.1.0 Yokohama
 * @version 1.1.0 Yokohama
 * @author Alexander Savin
 */

final class EasyDokkaUtils {

    private static volatile EasyDokkaUtils singleton = null

    private final def project

    private EasyDokkaUtils(project) {
        this.project = project
    }

    /**
     * Only method to get singleton object of com.dimaslanjaka.init.EasyDokkaUtils Class
     *
     * @param project project
     * @return thread-safe singleton
     */
    static EasyDokkaUtils with(project) {
        if (singleton == null) {
            synchronized (EasyDokkaUtils.class) {
                if (singleton == null) {
                    singleton = new EasyDokkaUtils(project)
                }
            }
        }
        return singleton
    }

    /**
     * Downloads library and puts it to local repository.
     *
     * @param url library url
     * @param path local repository path
     * @param version library version
     * @param name local repository file name
     */
    static void downloadLib(String url, String path, String version, String name) {
        File file = new File("${System.properties['user.home']}/.m2/repository/${path}/${version}/${name}")
        file.parentFile.mkdirs()
        if (!file.exists()) {
            new URL(url).withInputStream { downloadStream ->
                file.withOutputStream { fileOutputStream ->
                    fileOutputStream << downloadStream
                }
            }
        }
    }

    /**
     * Returns Java API specification link.
     *
     * @param currentJavaVersion current Java version
     * @return Java API specification link
     */
    static String getJavaAPISpecificationLink(String currentJavaVersion) {
        switch (currentJavaVersion) {
            case '1.5':
                return 'https://docs.oracle.com/javase/1.5.0/docs/api/'
            case '1.6':
                return 'https://docs.oracle.com/javase/6/docs/api/'
            case '1.7':
                return 'https://docs.oracle.com/javase/7/docs/api/'
            case '1.8':
                return 'https://docs.oracle.com/javase/8/docs/api/'
            case '1.9':
                return 'https://docs.oracle.com/javase/9/docs/api/'
            case '1.10':
            case '11':
            case '12':
            case '13':
            case '14':
                return 'https://docs.oracle.com/javase/10/docs/api/'
            default:
                return ''
        }
    }

    /**
     * Checks Android or non-Android project.
     *
     * @return true if Android project, false otherwise
     */
    boolean isAndroid() {
        return project.getPlugins().hasPlugin('com.android.application') ||
                project.getPlugins().hasPlugin('com.android.library') ||
                project.getPlugins().hasPlugin('com.android.feature') ||
                project.getPlugins().hasPlugin('com.android.dynamic-feature') ||
                project.getPlugins().hasPlugin('com.android.instantapp') ||
                project.getPlugins().hasPlugin('android') ||
                project.getPlugins().hasPlugin('android-library')
    }

    /**
     * Checks Kotlin or non-Kotlin project.
     *
     * @return true if Kotlin project, false otherwise
     */
    boolean isKotlin() {
        return project.getPlugins().hasPlugin('kotlin') ||
                project.getPlugins().hasPlugin('kotlin-platform-common') ||
                project.getPlugins().hasPlugin('kotlin-platform-jvm') ||
                project.getPlugins().hasPlugin('kotlin-platform-js') ||
                project.getPlugins().hasPlugin('org.jetbrains.kotlin') ||
                project.getPlugins().hasPlugin('org.jetbrains.kotlin.jvm') ||
                project.getPlugins().hasPlugin('org.jetbrains.kotlin.js') ||
                project.getPlugins().hasPlugin('kotlin2js') ||
                project.getPlugins().hasPlugin('kotlin-android') ||
                project.getPlugins().hasPlugin('kotlin-android-extensions')
    }
}