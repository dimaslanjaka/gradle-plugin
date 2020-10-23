package com.dimaslanjaka.gradle;

import com.dimaslanjaka.gradle.testing.ProjectBuilder;
import com.dimaslanjaka.gradle.utils.ConsoleColors;
import kotlin.jvm.internal.Intrinsics;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import static com.dimaslanjaka.gradle.xml.AndroidManifestPermission.apply;

public class AndroidManifest {
	static ConsoleColors.Companion console = ConsoleColors.Companion;

	public static void main(@NotNull String[] args) {
		Intrinsics.checkParameterIsNotNull(args, "args");
		ProjectBuilder builder = (new ProjectBuilder()).setRoot("build/test");
		Intrinsics.checkExpressionValueIsNotNull(builder, "builder");
		Project project = builder.getInstance();

		try {
			builder.newFile("src/main/AndroidManifest.xml", builder.getText("https://raw.githubusercontent.com/ezzieyguywuf/Settings-Profiles/master/AndroidManifest.xml"));
			Intrinsics.checkExpressionValueIsNotNull(project, "project");
			apply(project);

		} catch (Exception var4) {
			var4.printStackTrace();
		}

	}
}
