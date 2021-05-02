//file:noinspection GrUnnecessaryPublicModifier
package com.dimaslanjaka.gradle.helper;


import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.regex.Matcher;

public class Logger {
    public static final String TEXT_RESET = "\u001B[0m";
    public static final String TEXT_BLACK = "\u001B[30m";
    public static final String TEXT_RED = "\u001B[31m";
    public static final String TEXT_GREEN = "\u001B[32m";
    public static final String TEXT_YELLOW = "\u001B[33m";
    public static final String TEXT_BLUE = "\u001B[34m";
    public static final String TEXT_PURPLE = "\u001B[35m";
    public static final String TEXT_CYAN = "\u001B[36m";
    public static final String TEXT_WHITE = "\u001B[37m";

    public static java.util.logging.Logger getLogger(@Nullable Class<?> clazz) {
        String c;
        if (clazz == null) {
            c = getCallerClassName();
        } else {
            c = clazz.getName();
        }
        java.util.logging.Logger log = java.util.logging.Logger.getLogger(c);
        Handler handlerObj = new ConsoleHandler();
        handlerObj.setLevel(Level.ALL);
        log.addHandler(handlerObj);
        log.setLevel(Level.ALL);
        log.setUseParentHandlers(false);
        return log;
    }

    private static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Logger.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                return ste.getClassName();
            }
        }
        return null;
    }

    public static void error(String message) {
        String fn = new Object() {
        }.getClass().getEnclosingMethod().getName();
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Logger.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                String[] sclass = ste.getClassName().split("\\.");
                String[] classm1 = Arrays.copyOf(sclass, sclass.length - 1); // splice -1
                String[] classm2 = Arrays.copyOf(sclass, sclass.length - 2); // splice -2
                String[] aclass = new String[]{
                        String.join("/", sclass),
                        String.join("/", classm1),
                        String.join("/", classm2)
                };

                List<String> files = new ArrayList<>();

                for (String c : aclass) {
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/java", c, ste.getFileName())));
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/groovy", c, ste.getFileName())));
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/kotlin", c, ste.getFileName())));
                }
                String fileLocation = null;
                for (String file : files) {
                    File test = new File(file);
                    if (test.exists()) {
                        fileLocation = String.format("%s %s:%s\n%s", fn, file, ste.getLineNumber(), message);
                        break;
                    }
                }

                if (fileLocation != null) {
                    System.out.println(TEXT_RED + fileLocation + TEXT_RESET);
                } else {
                    System.out.println(message);
                }
            }
        }
    }

    public static void info(String message) {
        String fn = new Object() {
        }.getClass().getEnclosingMethod().getName();
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Logger.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                String[] sclass = ste.getClassName().split("\\.");
                String[] classm1 = Arrays.copyOf(sclass, sclass.length - 1); // splice -1
                String[] classm2 = Arrays.copyOf(sclass, sclass.length - 2); // splice -2
                String[] aclass = new String[]{
                        String.join("/", sclass),
                        String.join("/", classm1),
                        String.join("/", classm2)
                };

                List<String> files = new ArrayList<>();

                for (String c : aclass) {
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/java", c, ste.getFileName())));
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/groovy", c, ste.getFileName())));
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/kotlin", c, ste.getFileName())));
                }
                String fileLocation = null;
                for (String file : files) {
                    File test = new File(file);
                    if (test.exists()) {
                        fileLocation = String.format("%s %s:%s\n%s", fn, file, ste.getLineNumber(), message);
                        break;
                    }
                }

                if (fileLocation != null) {
                    System.out.println(TEXT_WHITE + fileLocation + TEXT_RESET);
                } else {
                    System.out.println(message);
                }
            }
        }
    }

    public static void warn(String message) {
        String fn = new Object() {
        }.getClass().getEnclosingMethod().getName();
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Logger.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                String[] sclass = ste.getClassName().split("\\.");
                String[] classm1 = Arrays.copyOf(sclass, sclass.length - 1); // splice -1
                String[] classm2 = Arrays.copyOf(sclass, sclass.length - 2); // splice -2
                String[] aclass = new String[]{
                        String.join("/", sclass),
                        String.join("/", classm1),
                        String.join("/", classm2)
                };

                List<String> files = new ArrayList<>();

                for (String c : aclass) {
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/java", c, ste.getFileName())));
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/groovy", c, ste.getFileName())));
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/kotlin", c, ste.getFileName())));
                }
                String fileLocation = null;
                for (String file : files) {
                    File test = new File(file);
                    if (test.exists()) {
                        fileLocation = String.format("%s %s:%s\n%s", fn, file, ste.getLineNumber(), message);
                        break;
                    }
                }

                if (fileLocation != null) {
                    System.out.println(TEXT_YELLOW + fileLocation + TEXT_RESET);
                } else {
                    System.out.println(message);
                }
            }
        }
    }

    public static void trace(String message, Object... objects) {
        LoggerFactory.getLogger(getCallerClassName()).trace(message, objects);
    }

    public static void error(String message, Object... objects) {
        LoggerFactory.getLogger(getCallerClassName()).error(message, objects);
    }

    public static void info(String message, Object... objects) {
        LoggerFactory.getLogger(getCallerClassName()).error(message, objects);
    }

    public static void trace(String message) {
        String fn = new Object() {
        }.getClass().getEnclosingMethod().getName();
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Logger.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                String[] sclass = ste.getClassName().split("\\.");
                String[] classm1 = Arrays.copyOf(sclass, sclass.length - 1); // splice -1
                String[] classm2 = Arrays.copyOf(sclass, sclass.length - 2); // splice -2
                String[] aclass = new String[]{
                        String.join("/", sclass),
                        String.join("/", classm1),
                        String.join("/", classm2)
                };

                List<String> files = new ArrayList<>();

                for (String c : aclass) {
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/java", c, ste.getFileName())));
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/groovy", c, ste.getFileName())));
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/kotlin", c, ste.getFileName())));
                }
                String fileLocation = null;
                for (String file : files) {
                    File test = new File(file);
                    if (test.exists()) {
                        fileLocation = String.format("%s %s:%s\n%s", fn, file, ste.getLineNumber(), message);
                        break;
                    }
                }

                if (fileLocation != null) {
                    System.out.println(TEXT_PURPLE + fileLocation + TEXT_RESET);
                } else {
                    System.out.println(message);
                }
            }
        }
    }

    public static void debug(String message) {
        String fn = new Object() {
        }.getClass().getEnclosingMethod().getName();
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Logger.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                String[] sclass = ste.getClassName().split("\\.");
                String[] classm1 = Arrays.copyOf(sclass, sclass.length - 1); // splice -1
                String[] classm2 = Arrays.copyOf(sclass, sclass.length - 2); // splice -2
                String[] aclass = new String[]{
                        String.join("/", sclass),
                        String.join("/", classm1),
                        String.join("/", classm2)
                };

                List<String> files = new ArrayList<>();

                for (String c : aclass) {
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/java", c, ste.getFileName())));
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/groovy", c, ste.getFileName())));
                    files.add(normalizePath(join("/", System.getProperty("user.dir"), "src/main/kotlin", c, ste.getFileName())));
                }
                String fileLocation = null;
                for (String file : files) {
                    File test = new File(file);
                    if (test.exists()) {
                        fileLocation = String.format("%s %s:%s\n%s", fn, file, ste.getLineNumber(), message);
                        break;
                    }
                }

                if (fileLocation != null) {
                    System.out.println(TEXT_CYAN + fileLocation + TEXT_RESET);
                } else {
                    System.out.println(message);
                }
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static String join(@NotNull String separator, String... strings) {
        return String.join(separator, strings);
    }

    private static String normalizePath(String path) {
        return path.replaceAll("/", Matcher.quoteReplacement(File.separator));
    }

    @SuppressWarnings("unused")
    private static class test {
        public static void main(String[] args) {
            Logger.info("Iam log info");
            Logger.trace("Iam log trace");
            Logger.debug("Iam log debug");
            Logger.warn("Iam log warn");
            Logger.error("Iam log error");
        }
    }
}

