package com.dimaslanjaka.versioning;

import com.dimaslanjaka.gradle.versioning.VersionCompare;
import org.gradle.internal.impldep.org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestVersionCompare {
	@Test
	public void TestVersion() {
		VersionCompare a = new VersionCompare("1.1");
		VersionCompare b = new VersionCompare("1.1.1");
		a.compareTo(b); // return -1 (a<b)
		a.equals(b);    // return false

		a = new VersionCompare("2.0");
		b = new VersionCompare("1.9.9");
		a.compareTo(b); // return 1 (a>b)
		a.equals(b);    // return false

		a = new VersionCompare("1.0");
		b = new VersionCompare("1");
		a.compareTo(b); // return 0 (a=b)
		a.equals(b);    // return true

		a = new VersionCompare("1");
		b = null;
		a.compareTo(b); // return 1 (a>b)
		a.equals(b);    // return false

		List<VersionCompare> versions = new ArrayList<VersionCompare>();
		versions.add(new VersionCompare("2"));
		versions.add(new VersionCompare("1.0.5"));
		versions.add(new VersionCompare("1.01.0"));
		versions.add(new VersionCompare("1.00.1"));
		Collections.min(versions).get(); // return min version
		Collections.max(versions).get(); // return max version

		// WARNING
		a = new VersionCompare("2.06");
		b = new VersionCompare("2.060");
		a.equals(b);   // return false
	}

	@Test
	public void TestUsingMaven() {
		DefaultArtifactVersion minVersion = new DefaultArtifactVersion("1.0.1");
		DefaultArtifactVersion maxVersion = new DefaultArtifactVersion("1.10");

		DefaultArtifactVersion version = new DefaultArtifactVersion("1.11");

		if (version.compareTo(minVersion) < 0 || version.compareTo(maxVersion) > 0) {
			System.out.println("Sorry, your version is unsupported");
		}
	}

	@Test
	public void TestCurrentNewer() {
		DefaultArtifactVersion current = new DefaultArtifactVersion("1.0.0");
		DefaultArtifactVersion newer = new DefaultArtifactVersion("1.0.1");
		boolean isNewerAvailable = current.compareTo(newer) > 0;
		System.out.println("Newer Version Is: " + isNewerAvailable);
	}
}
