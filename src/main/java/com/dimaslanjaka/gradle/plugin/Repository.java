package com.dimaslanjaka.gradle.plugin;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public class Repository {
	Project project = null;

	public Repository(Project p) {
		project = p;
	}

	public static void addRepositories(Project project, LinkedHashMap<String, String> repositories) {
		project
						.getRepositories()
						.stream()
						.filter(repository -> repository instanceof MavenArtifactRepository)
						.forEach(
										repository -> {
											MavenArtifactRepository mavenRepository = (MavenArtifactRepository) repository;
											String name = mavenRepository.getName().toLowerCase();
											String url = String.valueOf(mavenRepository.getUrl());

											repositories.put(name, url);
										});
	}

	public static MavenArtifactRepository addMavenRepo(Project proj, final String name, final String url) {
		return proj.getRepositories().maven(new Action<MavenArtifactRepository>() {
			@Override
			public void execute(@NotNull MavenArtifactRepository repo) {
				repo.setName(name);
				repo.setUrl(url);
			}
		});
	}

	public static void addRepoUrl(Project p, String url) {
		p.getRepositories().maven(new Action<MavenArtifactRepository>() {
			@Override
			public void execute(MavenArtifactRepository mavenArtifactRepository) {
				mavenArtifactRepository.setName(MD5.get(url));
				mavenArtifactRepository.setUrl(url);
			}
		});
	}

	public void addRepo(String url) {
		if (project != null) addRepoUrl(project, url);
	}

	public void addDefault() {
		if (project != null) {
			project.getRepositories().add(project.getRepositories().mavenLocal());
			project.getRepositories().add(project.getRepositories().google());
			project.getRepositories().add(project.getRepositories().jcenter());
			project.getRepositories().add(project.getRepositories().mavenCentral());
		}
	}
}