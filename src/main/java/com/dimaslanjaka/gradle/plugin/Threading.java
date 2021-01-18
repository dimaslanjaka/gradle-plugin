package com.dimaslanjaka.gradle.plugin;

import org.gradle.api.Project;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

	public void initialize(Callable<Object> func) {
		synchronized (singleThreadLock) {
			if (project != null) {
				File buildDir = new File(project.getRootProject().getBuildDir(), "thread");
				if (!buildDir.exists()) if (!buildDir.mkdirs()) System.out.println("");
				lockFilePath = new File(buildDir, lockFilename);
				if (!lockFilePath.exists()) {
					try {
						if (!lockFilePath.getParentFile().exists()) {
							if (!lockFilePath.getParentFile().mkdirs()) {
								System.out.println("");
							}
						}
						if (lockFilePath.createNewFile()) {
							threading(new Callable<Object>() {
								@Override
								public Object call() throws Exception {
									func.call();
									if (lockFilePath.delete()) {
										System.out.println("Lock file deleted " + new Date());
									}
									return null;
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println(lockFilePath);
					}
				}
			}
		}
	}
}
