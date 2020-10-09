package com.dimaslanjaka.gradle.curl;

import com.dimaslanjaka.gradle.repackaged.org.apache.maven.artifact.versioning.ComparableVersion;
import org.junit.Assert;
import org.junit.Test;

public class TempTest {
	@Test
	public void testVersions() {
		Assert.assertTrue(new ComparableVersion("1.0-beta1-SNAPSHOT").compareTo(
						new ComparableVersion("1.0-beta1")) < 0);
		Assert.assertTrue(new ComparableVersion("1.0-beta1").compareTo(
						new ComparableVersion("1.0-beta2-SNAPSHOT")) < 0);
		Assert.assertTrue(new ComparableVersion("1.0-beta2-SNAPSHOT").compareTo(
						new ComparableVersion("1.0-rc1-SNAPSHOT")) < 0);
		Assert.assertTrue(new ComparableVersion("1.0-rc1-SNAPSHOT").compareTo(
						new ComparableVersion("1.0-rc1")) < 0);
		Assert.assertTrue(new ComparableVersion("1.0-rc1").compareTo(
						new ComparableVersion("1.0-SNAPSHOT")) < 0);
		Assert.assertTrue(new ComparableVersion("1.0-SNAPSHOT").compareTo(
						new ComparableVersion("1.0")) < 0);
		Assert.assertTrue(new ComparableVersion("1.0").compareTo(
						new ComparableVersion("1")) == 0);
		Assert.assertTrue(new ComparableVersion("1.0").compareTo(
						new ComparableVersion("1.0-sp")) < 0);
		Assert.assertTrue(new ComparableVersion("1.0-sp").compareTo(
						new ComparableVersion("1.0-whatever")) < 0);
		Assert.assertTrue(new ComparableVersion("1.0-whatever").compareTo(
						new ComparableVersion("1.0.1")) < 0);
	}
}