package com.dimaslanjaka.gradle.plugin;

import org.gradle.api.Project;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.dimaslanjaka.kotlin.ConsoleColors.println;


public class Threading {
    public static final Object singleThreadLock = new Object();
    private static String lockFilename = "LOCK";
    private static File lockFilePath = null;
    public Project project = null;

    public Threading() {
    }

    public Threading(Project p) {
        setProject(p);
    }

    public Threading(Project p, String lockFilename) {
        setProject(p);
        Threading.lockFilename = lockFilename;
    }

    public void setProject(Project p) {
        project = p;
    }

    public void threading(Callable<Object> func) {
        Runnable r = new Runnable() {
            public void run() {
                try {
                    func.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(r).start();
    }

    public void threading2(Callable<Object> func) {
        Runnable r = new Runnable() {
            public void run() {
                try {
                    func.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(r);
        // this line will execute immediately, not waiting for your task to complete
        executor.shutdown(); // tell executor no more work is coming
        // this line will also execute without waiting for the task to finish
    }

    public void resolve() {
        if (project != null) {
            File buildDir = new File(project.getRootProject().getBuildDir(), "thread");
            if (!buildDir.exists()) if (!buildDir.mkdirs()) System.out.println("");
            lockFilePath = new File(buildDir, lockFilename);
            if (lockFilePath.exists()) {
                if (lockFilePath.delete()) System.out.println(lockFilename + " initialized");
            }
        }
    }

    public void initlialize(Callable<Object> func) {
        initialize(func, "unamed", null);
    }

    public void initialize(Callable<Object> func, String threadName, String newLockFileName) {
        if (newLockFileName != null) {
            lockFilename = newLockFileName;
        }
        println("initialize thread " + threadName);
        synchronized (singleThreadLock) {
            println("start single thread lock");
            if (project != null) {
                File buildDir = new File(project.getRootProject().getBuildDir(), "thread");
                if (!buildDir.exists()) if (!buildDir.mkdirs()) println("");
                lockFilePath = new File(buildDir, lockFilename);
                lockFilePath.deleteOnExit();
                if (!lockFilePath.exists()) {
                    try {
                        if (!lockFilePath.getParentFile().exists()) {
                            if (!lockFilePath.getParentFile().mkdirs()) {
                                println("cannot create parent directory thread lock");
                            }
                        }
                        if (lockFilePath.createNewFile()) {
                            threading(new Callable<Object>() {
                                @Override
                                public Object call() throws Exception {
                                    func.call();
                                    if (lockFilePath.delete()) {
                                        println("Lock file deleted " + new Date());
                                    }
                                    return null;
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        println(lockFilePath);
                    }
                } else {
                    println("lock file exist: " + lockFilePath);
                }
            }
        }
    }

    public static class Once {
        private final AtomicBoolean done = new AtomicBoolean();

        public void run(Runnable task) {
            if (done.get()) return;
            if (done.compareAndSet(false, true)) {
                task.run();
            }
        }
    }
}

