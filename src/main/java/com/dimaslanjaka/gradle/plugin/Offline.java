package com.dimaslanjaka.gradle.plugin;

import org.gradle.api.Project;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Offline {
    static String[] extensions = {".module", ".jar", ".pom", ".aar", ".sha1", ".xml"};
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
        try {
            Files.copy(xfrom, xto, StandardCopyOption.REPLACE_EXISTING);
            String formatted = String.format("Cached\n\tf: %s\n\tt: %s", xfrom, xto);
            if (debug) {
                System.out.println(formatted);
            } else {
                return formatted;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        return path
                .replace("\\", "/")
                .replaceAll("/{2,99}", "/");
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
        for (String entry : extensions) {
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

    static void error(String str) {
        if (project != null) {
            project.getLogger().error(str);
        } else {
            println(str);
        }
    }

    static void println(String str) {
        System.out.println(str);
    }

    @SuppressWarnings({"DefaultLocale", "ActualValue"})
    static void OfflineMethods(@Nullable Project p, @Nullable java.util.concurrent.Callable<Object> callback) {
        project = p;
        int resultCount = 0;
        int limitCount = 0;
        String home = Core.extension.home;
        File from = new File(new File(home), ".gradle/caches/modules-2/files-2.1");
        File to = Core.extension.localRepository;
        if (!to.exists()) if (!to.mkdirs()) print("fail create local repository");
        StringBuilder localMaven = new StringBuilder(fixPath(to.getAbsolutePath()));
        if (from.exists()) {
            File[] directoryListing = from.listFiles();
            if (directoryListing != null) {
                outerloop:
                for (File child : directoryListing) {
                    String id2path = getFileName(child).replace(".", "/");
                    StringBuilder mavenPath = new StringBuilder(localMaven.toString().trim() + "/" + id2path);

                    File[] listFrom = child.listFiles();
                    if (listFrom != null) {
                        for (File jarModules : listFrom) {
                            if (jarModules.isDirectory()) {
                                // copy maven path to module directory builder variable
                                StringBuilder modulePath = new StringBuilder(mavenPath.toString());
                                modulePath.append("/").append(getFileName(jarModules)).append("/");

                                File[] jarModulesDirectory = jarModules.listFiles();
                                if (jarModulesDirectory != null) {
                                    for (File jarVersions : jarModulesDirectory) {
                                        if (jarVersions.isDirectory()) {
                                            // copy jar directory for versioning variable
                                            StringBuilder jVersion = new StringBuilder(modulePath.toString());
                                            jVersion.append("/").append(getFileName(jarVersions)).append("/");
                                            File versionPath = new File(fixPath(jVersion));
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
                                                                        if (isEmptyFile(targetMavenArtifact)) {
                                                                            boolean copyConfirm = false;
                                                                            // TODO: delete if target malformed pom
                                                                            if (targetMavenArtifact.exists() && (targetMavenArtifact.getName().endsWith(".xml") || targetMavenArtifact.getName().endsWith(".pom"))) {
                                                                                if (!validateXML(targetMavenArtifact.toPath())) {
                                                                                    targetMavenArtifact.delete();
                                                                                }
                                                                            }
                                                                            if (artifact.exists() && (artifact.getName().endsWith(".xml") || artifact.getName().endsWith(".pom"))) {
                                                                                if (validateXML(artifact.toPath())) {
                                                                                    copyConfirm = true;
                                                                                } else {
                                                                                    artifact.delete();
                                                                                }
                                                                            } else {
                                                                                copyConfirm = true;
                                                                            }
                                                                            if (copyConfirm) {
                                                                                if (limitCount >= limit) {
                                                                                    error(String.format("Limit cache reached (%d >= %d)", limitCount, limit));
                                                                                    break outerloop;
                                                                                } else {
                                                                                    info("Limit counter " + limitCount);
                                                                                    limitCount++;
                                                                                }
                                                                                copy(artifact, targetMavenArtifact);
                                                                                resultCount++;

                                                                                String successlog = String.format("%d. Cached\n\tf: %s\n\tt: %s", resultCount, artifact, targetMavenArtifact);
                                                                                //System.out.println(successlog);
                                                                                info(successlog);
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
                }
            } else {
                // Handle the case where dir is not really a directory.
                // Checking dir.isDirectory() above would not be sufficient
                // to avoid race conditions with another process that deletes
                // directories.
            }
        }
        try {
            assert callback != null;
            callback.call();
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

