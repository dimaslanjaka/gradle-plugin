package com.dimaslanjaka.gradle.plugin;

import org.gradle.api.Project;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import static com.dimaslanjaka.kotlin.ConsoleColors.println;

public class Offline {
    static String[] artifactExtensions = {".module", ".jar", ".pom", ".aar", ".sha1", ".xml"};
    CoreExtension extension;
    static String customLocalRepository = null; //fill your custom local repository directory path, retain null for default local maven repository
    static int limit = 5;
    private static Project project = null;

    public static void main(String[] args) {
        OfflineMethods(null);
    }

    public static void copy(File from, File to) {
        copy(from, to, false);
    }

    public static String copy(File from, File to, boolean debug) {
        Path xfrom = from.toPath(); //convert from File to Path
        Path xto; //convert from String to Path
        if (!to.isDirectory()) {
            xto = Paths.get(to.toString()); //convert from String to Path
        } else {
            xto = Paths.get(String.valueOf(new File(to, getFileName(from))));
        }

        StringBuilder log = new StringBuilder();
        String gradle = String.format("Gradle -> %s", xfrom);
        String maven = String.format("Maven -> %s", xto);
        try {
            Files.copy(xfrom, xto, StandardCopyOption.REPLACE_EXISTING);
            log.append(String.format("Cached Success\n\t%s\n\t%s", gradle, maven));
        } catch (IOException e) {
            if (debug) {
                e.printStackTrace();
            }
            log.append(String.format("Cached Failed\n\t%s\n\t%s\n\t%s", gradle, maven, e.getMessage()));
        }
        if (debug) {
            println(log.toString());
        }
        return log.toString();
    }

    public static String fixPath(StringBuilder sb) {
        return fixPath(sb.toString());
    }

    static String getFileName(File file) {
        Path pathObject = Paths.get(file.getAbsolutePath());
        return pathObject.getFileName().toString();
    }

    static String getFileName(String file) {
        return getFileName(file, false);
    }

    @SuppressWarnings("all")
    static String getFileName(String file, boolean stripExtensions) {
        Path pathObject = Paths.get(file);
        if (!stripExtensions) {
            return pathObject.getFileName().toString();
        } else {
            return stripExtension(pathObject.getFileName().toString());
        }
    }

    @SuppressWarnings("all")
    static String getFileName(File file, boolean stripExtensions) {
        return getFileName(file.getAbsolutePath(), stripExtensions);
    }

    static String stripExtension(String str) {
        // Handle null case specially.

        if (str == null) return null;

        // Get position of last '.'.

        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.

        if (pos == -1) return str;

        // Otherwise return the string, up to the dot.

        String rem = str.substring(0, pos);
        if (validExtension(rem, new String[]{".jar", ".aar"})) {
            return stripExtension(rem);
        }
        return rem;
    }

    @SuppressWarnings("unused")
    private static void print(Object o) {
        System.out.println(o);
    }

    @SuppressWarnings("unused")
    static String fixPath(String path) {
        return new File(path).toPath().normalize().toString();
    }

    /**
     * If path length < 40 is valid
     *
     * @param path
     * @return
     */
    @SuppressWarnings("unused")
    static boolean isValidArtifactPath(Object path) {
        return path.toString().length() < 40;
    }

    public static boolean validExtension(String s) {
        for (String entry : artifactExtensions) {
            if (s.endsWith(entry)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public static boolean validExtension(String s, String[] extensions) {
        for (String entry : extensions) {
            if (s.endsWith(entry)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    static void testGetFileName() {
        print(getFileName("/cdd/eee/sds/s/ds/assas.jar"));
        print(getFileName("C:\\Users\\dimas\\.m2\\repository\\kk.jar.sha1"));

        print(getFileName("/cdd/eee/sds/s/ds/assas.jar", true));
        print(getFileName("C:\\Users\\dimas\\.m2\\repository\\kk.jar.sha1", true));
    }

    static void OfflineMethods(Project p) {
        OfflineMethods(p, null);
    }

    static void info(String str) {
        if (project != null) {
            project.getLogger().info(str);
        } else {
            println(str);
        }
    }

    public static void error(String str) {
        if (project != null) {
            project.getLogger().error(str);
        } else {
            println(str);
        }
    }

    public static void OfflineMethods(@Nullable Project p, @Nullable java.util.concurrent.Callable<Object> callback) {
        artifactExtensions = Core.extension.extensions;
        project = p;
        int resultCount = 0;
        com.dimaslanjaka.kotlin.File logfile = null;
        boolean android = false;
        StringBuilder log = new StringBuilder();
        if (p != null) {
            android = p.getPlugins().hasPlugin("com.android.library") || p.getPlugins().hasPlugin("com.android.application");
            logfile = new com.dimaslanjaka.kotlin.File(p.getBuildDir().getAbsolutePath(), "plugin/com.dimaslanjaka/offline-" + p.getName());
            log.append(java.lang.String.format("(Project: %s) (Android: %b)\n", p.getName(), android));
        }
        log.append(java.lang.String.format("Cache Start %s\n\n", new Date()));

        if (Core.extension == null) {
            Core.extension = new CoreExtension(p);
        }
        String home = Core.extension.getHome();
        File from = new File(home, ".gradle/caches/modules-2/files-2.1");
        File to = Core.extension.getLocalRepository();
        if (!to.exists()) if (!to.mkdirs()) print("fail create local repository");
        StringBuilder localMaven = new StringBuilder(fixPath(to.getAbsolutePath()));
        if (from.exists()) {
            File[] directoryListing = from.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    String id2path = getFileName(child).replace(".", File.separator);
                    StringBuilder mavenPath = new StringBuilder(localMaven.toString().trim() + File.separator + id2path);
                    //println(mavenPath);

                    File[] listFrom = child.listFiles();
                    if (listFrom != null) {
                        for (File jarModules : listFrom) {
                            if (jarModules.isDirectory()) {
                                // copy maven path to module directory builder variable
                                StringBuilder modulePath = new StringBuilder(mavenPath.toString());
                                modulePath.append(File.separator).append(getFileName(jarModules)).append(File.separator);
                                //println(modulePath);

                                File[] jarModulesDirectory = jarModules.listFiles();
                                if (jarModulesDirectory != null) {
                                    for (File jarVersions : jarModulesDirectory) {
                                        if (jarVersions.isDirectory()) {
                                            // copy jar directory for versioning variable
                                            File versionPath = new File(modulePath + File.separator + getFileName(jarVersions) + File.separator);
                                            //println(versionPath);
                                            if (!versionPath.exists()) {
                                                if (!versionPath.mkdirs()) {
                                                    error("fail create " + id2path + " v" + getFileName(jarVersions));
                                                }
                                            }

                                            File[] jarHash = jarVersions.listFiles();
                                            if (jarHash != null) {
                                                for (File jarHases : jarHash) {
                                                    if (!isValidArtifactPath(getFileName(jarHases))) {
                                                        File[] artefak = jarHases.listFiles();
                                                        if (artefak != null) {
                                                            for (File artifact : artefak) {
                                                                if (artifact != null) {
                                                                    if (validExtension(artifact.getAbsolutePath())) {
                                                                        File targetMavenArtifact = new File(versionPath, getFileName(artifact));
                                                                        // println(targetMavenArtifact);
                                                                        boolean copyConfirm = false;
                                                                        // TODO: delete if target malformed pom
                                                                        if (targetMavenArtifact.exists() && (targetMavenArtifact.getName().endsWith(".xml") || targetMavenArtifact.getName().endsWith(".pom"))) {
                                                                            if (!validateXML(targetMavenArtifact.toPath())) {
                                                                                log.append(String.format("\t(Delete=%b) %s\n", targetMavenArtifact.delete(), targetMavenArtifact.getAbsolutePath()));
                                                                            }
                                                                        }
                                                                        if (artifact.exists() && (artifact.getName().endsWith(".xml") || artifact.getName().endsWith(".pom"))) {
                                                                            //println("pom: " + artifact);
                                                                            if (validateXML(artifact.toPath())) {
                                                                                copyConfirm = true;
                                                                            } else {
                                                                                log.append(String.format("\t(Delete=%b) %s\n", artifact.delete(), artifact.getAbsolutePath()));
                                                                            }
                                                                        } else {
                                                                            copyConfirm = true;
                                                                        }
                                                                        if (copyConfirm) {
                                                                            copy(artifact, targetMavenArtifact);
                                                                            resultCount++;

                                                                            String successlog = String.format("%d. Cached\n\tf: %s\n\tt: %s\n", resultCount, artifact, targetMavenArtifact);
                                                                            log.append(successlog);
                                                                            //println(successlog);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Handle the case where dir is not really a directory.
                // Checking dir.isDirectory() above would not be sufficient
                // to avoid race conditions with another process that deletes
                // directories.
            }
        }
        try {
            if (callback != null) {
                callback.call();
            }
            if (logfile != null) {
                logfile.write(log.toString(), Charset.defaultCharset());
            } else {
                com.dimaslanjaka.kotlin.File tmp = new com.dimaslanjaka.kotlin.File("build");
                if (tmp.exists()) {
                    logfile = new com.dimaslanjaka.kotlin.File(tmp.getAbsolutePath(), "plugin/com.dimaslanjaka/offline-manual");
                    logfile.write(log.toString());
                }
            }
        } catch (Exception ignored) {
            //e.printStackTrace();
        }
    }

    static boolean validateXML(Path pathXML) {
        try {
            File fXmlFile = new File(pathXML.toString());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static boolean isEmptyFile(File file) {
        return !file.exists() || file.length() == 0;
    }

    @SuppressWarnings("unused")
    void test() {
        String pathStringToAFile = "U:\\temp\\TestOutput\\TestFolder\\test_file.txt";
        String pathStringToAFolder = "U:\\temp\\TestOutput\\TestFolder";
        String pathStringToAFolderWithTrailingBackslash = "U:\\temp\\TestOutput\\TestFolder\\";

        Path pathToAFile = Paths.get(pathStringToAFile);
        Path pathToAFolder = Paths.get(pathStringToAFolder);
        Path pathToAFolderWithTrailingBackslash
                = Paths.get(pathStringToAFolderWithTrailingBackslash);

        System.out.println(pathToAFile.getFileName().toString());
        System.out.println(pathToAFolder.getFileName().toString());
        System.out.println(pathToAFolderWithTrailingBackslash.getFileName().toString());
    }
}

