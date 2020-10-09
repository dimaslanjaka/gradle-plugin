package com.dimaslanjaka.gradle.versioning;

import org.gradle.internal.impldep.org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class VersionCheck {
	private static final VersionComparator cmp = new VersionComparator();

	public static boolean decimalOnly(String currentVersion, String newerVersion) {
		DefaultArtifactVersion current = new DefaultArtifactVersion(currentVersion);
		DefaultArtifactVersion newer = new DefaultArtifactVersion(newerVersion);
		boolean isNewerAvailable = current.compareTo(newer) > 0;
		System.out.println("Newer Version Is: " + isNewerAvailable);
		return isNewerAvailable;
	}

	public static void test(String[] versions) {
		for (int i = 0; i < versions.length; i++) {
			for (int j = i; j < versions.length; j++) {
				test(versions[i], versions[j]);
			}
		}
	}

	/**
	 * ```
	 * String result = VersionCheck.test("1.0.0", "1.1.1");
	 * System.out.println("Current Version Is " + result);
	 * ```
	 *
	 * @param v1 Current Version
	 * @param v2 Other Version
	 * @return LESS GREATER EQUALS
	 */
	public static String test(String v1, String v2) {
		int result = cmp.compare(v1, v2);
		String res = "EQUALS";
		String op = "==";
		if (result < 0) {
			op = "<";
			res = "LESS";
		}
		if (result > 0) {
			op = ">";
			res = "GREATER";
		}
		System.out.printf("%s %s %s is %s\n", v1, op, v2, res);
		return res;
	}
}
