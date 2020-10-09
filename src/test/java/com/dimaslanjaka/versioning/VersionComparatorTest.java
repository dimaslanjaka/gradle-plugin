package com.dimaslanjaka.versioning;

import com.dimaslanjaka.gradle.versioning.VersionCheck;
import com.dimaslanjaka.gradle.versioning.VersionComparator;
import org.junit.Test;

public class VersionComparatorTest {
	private static VersionComparator cmp = new VersionComparator();

	@Test
	public void decimal() {
		cmp = new VersionComparator();
		VersionCheck.test(new String[]{"1.1.2", "1.2", "1.2.0", "1.2.1", "1.12"});
	}

	@Test
	public void snapshot() {
		cmp = new VersionComparator();
		VersionCheck.test(new String[]{"1.3", "1.3a", "1.3b", "1.3-SNAPSHOT"});
	}

	@Test
	public void single() {
		String result = VersionCheck.test("1.0.0", "1.1.1");
		System.out.println("Current Version Is " + result);
	}
}
