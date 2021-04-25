package com.dimaslanjaka.gradle.plugin;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;

public class Repository {
    Project project;
    FileResourcesUtils app = new FileResourcesUtils();
    InputStream is = app.getFileFromResourceAsStream("repo.md");
    List<String> listRepos = FileResourcesUtils.getInputStream(is);

    public Repository(Project p) {
        project = p;
        if (!p.getSubprojects().isEmpty()) {
            for (Project sp : p.getSubprojects()) {
                initialize(sp);
            }
        }
        initialize(p);
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

    private void initialize(Project p) {
        p.beforeEvaluate(project1 -> {
            for (String repo : listRepos) {
                if (Utils.isURL(repo, true)) {
                    addRepo(p, repo);
                }
            }
            addDefault(p);
        });
    }

    private void addDefault() {
        addDefault(this.project);
    }

    private void addRepo(Project project1, String repo) {
        addRepoUrl(project1, repo);
    }

    public void addRepo(String url) {
        if (project != null) addRepoUrl(project, url);
    }

    public void addDefault(Project project) {
        if (project != null) {
            project.getRepositories().add(project.getRepositories().mavenLocal());
            project.getRepositories().add(project.getRepositories().google());
            project.getRepositories().add(project.getRepositories().jcenter());
            project.getRepositories().add(project.getRepositories().mavenCentral());
        }
    }
}