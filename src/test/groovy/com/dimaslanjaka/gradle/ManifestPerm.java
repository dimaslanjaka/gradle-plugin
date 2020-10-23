package com.dimaslanjaka.gradle;

import com.dimaslanjaka.gradle.xml.AndroidManifestPermission;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.testkit.runner.BuildResult;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;

//@RunWith(RobolectricTestRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ManifestPerm {
	public final File projectDir = new File("build/JUnit/ManifestPerm").getAbsoluteFile();
	public final com.dimaslanjaka.gradle.testing.ProjectBuilder testProjectDir = new com.dimaslanjaka.gradle.testing.ProjectBuilder(projectDir);
	DependencyHandler dependencies;
	String userhome = System.getProperty("user.home");
	File gradleHome = new File(userhome, ".gradle");
	private Project project;

	public void prepare() throws IOException {
		testProjectDir.create();
		project = testProjectDir.getInstance();
		dependencies = project.getDependencies();
		project.getPluginManager().apply("com.android.application");
		project.getPluginManager().apply("com.dimaslanjaka");
		dependencies.create("androidx.ads:ads-identifier:1.0.0-alpha04");
		dependencies.create("androidx.annotation:annotation:1.1.0");
		dependencies.create("com.google.guava:guava:28.0-android");
		dependencies.create("androidx.appcompat:appcompat:1.0.0");
		dependencies.create("androidx.appcompat:appcompat-resources:1.0.0");
		dependencies.create("androidx.annotation:annotation-experimental:1.0.0");
	}

	@Test
	public void CloneRepo() {
		project = testProjectDir.fromGit("https://github.com/bashar13/EasyProfileSwitch");
		if (!testProjectDir.find("src/main/AndroidManifest.xml")) {
			testProjectDir.newFile("src/main/AndroidManifest.xml");
		}

		AndroidManifestPermission.apply(project);
		System.out.println(AndroidManifestPermission.manifestLocation);
		assertNotNull(project);
		assertNotNull(AndroidManifestPermission.manifestLocation);
	}

	public void testHelloWorldTask(BuildResult result) throws IOException {
		assertTrue(result.getOutput().contains("Hello world!"));
		assertEquals(SUCCESS, Objects.requireNonNull(result.task(":helloWorld")).getOutcome());
	}
}
