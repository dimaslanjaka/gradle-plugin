package com.dimaslanjaka.gradle.plugin;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.invocation.Gradle;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static com.dimaslanjaka.gradle.plugin.Offline.OfflineMethods;
import static com.dimaslanjaka.gradle.plugin.Utils.println;

public class Core implements Plugin<Project> {
	private final Threading thread = new Threading();

	@Override
	public void apply(final @NotNull Project target) {
		thread.project = target;
		Repository r = new Repository(target);
		FileResourcesUtils app = new FileResourcesUtils();
		InputStream is = app.getFileFromResourceAsStream("repo.md");
		List<String> listRepos = FileResourcesUtils.getInputStream(is);

		// TODO: clear gradle big log files
		Gradle gradle = target.getGradle();
		java.io.File[] cacheFiles = new File(gradle.getGradleUserHomeDir().getAbsolutePath(), "/daemon/"+gradle.getGradleVersion()).listFiles();
		for (java.io.File cf: cacheFiles){
			if (cf.getName().endsWith(".out.log")) {
				// println("Deleting gradle log file: $it") // Optional debug output
				if (cf.delete()) println(cf + " deleted");
			}
		}

		target.beforeEvaluate(new Action<Project>() {
			@Override
			public void execute(@NotNull Project project1) {
				for (String repo : listRepos) {
					if (Utils.isURL(repo, true)) {
						r.addRepo(repo);
					}
				}
				r.addDefault();
			}
		});

		target.afterEvaluate(new Action<Project>() {
			@Override
			public void execute(@NotNull Project project1) {
				thread.initialize(new Callable<Object>() {
					@Override
					public Object call() {
						File tmp = Utils.getTempFile(MD5.get("OfflineMethods"), true);
						if (tmp.isModifiedMoreThanHour(1) || tmp.isFirst()) {
							tmp.write(new Date());
							OfflineMethods();
						}
						return null;
					}
				});
			}
		});
	}


}
