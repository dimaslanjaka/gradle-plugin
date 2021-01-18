package com.dimaslanjaka.gradle.plugin;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;

public class Core implements Plugin<Project> {
	private final com.dimaslanjaka.gradle.plugin.Threading thread = new com.dimaslanjaka.gradle.plugin.Threading();

	@Override
	public void apply(final @NotNull Project target) {
		thread.project = target;
		Repository r = new Repository(target);
		FileResourcesUtils app = new FileResourcesUtils();
		InputStream is = app.getFileFromResourceAsStream("repo.md");
		List<String> listRepos = FileResourcesUtils.getInputStream(is);

		target.beforeEvaluate(new Action<Project>() {
			@Override
			public void execute(@NotNull Project project1) {
				for (String repo : listRepos){
					if (repo.startsWith("http")){
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
						//com.dimaslanjaka.gradle.plugin.Offline.OfflineMethods();
						return null;
					}
				});
			}
		});
	}


}
