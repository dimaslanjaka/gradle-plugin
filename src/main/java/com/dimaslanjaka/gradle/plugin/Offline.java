package com.dimaslanjaka.gradle.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;

public class Offline {
	static String[] extensions = {".module", ".jar", ".pom", ".aar", ".sha1", ".xml"};
	static String customLocalRepository = null; //fill your custom local repository directory path, retain null for default local maven repository

	public static void main(String[] args) {
		OfflineMethods();
	}

	static void copy(File from, File to) {
		Path xfrom = from.toPath(); //convert from File to Path
		Path xto; //convert from String to Path
		if (!to.isDirectory()) {
			xto = Paths.get(to.toString()); //convert from String to Path
		} else {
			xto = Paths.get(String.valueOf(new File(to, getFileName(from))));
		}
		try {
			Files.copy(xfrom, xto, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String fixPath(StringBuilder sb) {
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

	static void testGetFileName() {
		print(getFileName("/cdd/eee/sds/s/ds/assas.jar"));
		print(getFileName("C:\\Users\\dimas\\.m2\\repository\\kk.jar.sha1"));

		print(getFileName("/cdd/eee/sds/s/ds/assas.jar", true));
		print(getFileName("C:\\Users\\dimas\\.m2\\repository\\kk.jar.sha1", true));
	}

	static void OfflineMethods() {
		OfflineMethods(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				return null;
			}
		});
	}

	static void OfflineMethods(java.util.concurrent.Callable<Object> callback) {
		int resultCount = 0;
		String home = System.getProperty("user.home");
		File from = new File(new File(home), ".gradle/caches/modules-2/files-2.1");
		File to = new File(new File(home), ".m2/repository");
		if (!to.exists()) if (!to.mkdirs()) print("fail create local repository");
		StringBuilder localMaven = new StringBuilder(fixPath(to.getAbsolutePath()));
		if (from.exists()) {
			File[] directoryListing = from.listFiles();
			if (directoryListing != null) {
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
													print("fail create " + id2path + " v" + getFileName(jarVersions));
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
																			copy(artifact, targetMavenArtifact);
																			resultCount++;
																			print(resultCount + " Successful cached " + getFileName(artifact, true));
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
			callback.call();
		} catch (Exception e) {
			e.printStackTrace();
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

