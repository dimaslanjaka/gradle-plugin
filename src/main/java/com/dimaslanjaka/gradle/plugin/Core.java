package com.dimaslanjaka.gradle.plugin;

import com.dimaslanjaka.java.Thread;
import com.dimaslanjaka.kotlin.File;
import jar.Repack;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectCollectionSchema;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.invocation.Gradle;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.function.Consumer;

import static com.dimaslanjaka.gradle.api.Extension.createExtension;
import static com.dimaslanjaka.gradle.plugin.Offline.OfflineMethods;
import static com.dimaslanjaka.kotlin.ConsoleColors.println;

public class Core implements Plugin<Project> {
    private final Threading thread = new Threading();
    public static CoreExtension extension;
    private static Project project = null;
    public static String CONFIG_NAME = "offlineConfig";

    @Override
    public void apply(final @NotNull Project target) {
        thread.project = target;
        project = target;

        project.getAllprojects().forEach(new Consumer<Project>() {
            @Override
            public void accept(Project project) {
                setupProjectConfiguration(project);
            }
        });


        Repository repository = new Repository(target);
        Repack jar = new Repack(target);

        // TODO: clear gradle big log files
        Gradle gradle = target.getGradle();
        String gradleHome = gradle.getGradleUserHomeDir().getAbsolutePath();
        File daemon = new File(gradleHome, "/daemon/" + gradle.getGradleVersion());
        File[] cacheFiles = daemon.listFiles();
        for (File cf : cacheFiles) {
            if (cf.getName().endsWith(".log")) { // .out.log
                // println("Deleting gradle log file: $it") // Optional debug output
                if (cf.delete()) {
                    println(cf + " deleted");
                }
            }
        }

        // TODO: Configuring Rules
        extension = (CoreExtension) createExtension(project, CONFIG_NAME, CoreExtension.class, project);
        //project.getPlugins().apply(OfflineDependenciesPlugin.class);

        target.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(@NotNull Project project) {
                startCache(project);
                if (com.dimaslanjaka.gradle.api.Utils.isAndroid(project)) {
                    new Android(project);
                }
            }
        });
    }

    private void setupProjectConfiguration(Project project) {
        ConfigurationContainer configurationContainer = project.getConfigurations();
        NamedDomainObjectCollectionSchema collectionSchema = configurationContainer.getCollectionSchema();
        for (Configuration conf : configurationContainer) {
            println(project.getName(), conf.getName());
            conf.setTransitive(true);
            conf.setCanBeConsumed(true);
            conf.setCanBeResolved(true);
        }
    }

    /**
     * Start cache gradle jar to local maven repository
     *
     * @param targetProject project
     */
    public void startCache(Project targetProject) {
        final File tmp = new File(targetProject.getBuildDir().getAbsolutePath(), "/plugin/com.dimaslanjaka/offline");
        tmp.resolveParent();
        final Runnable start = new Runnable() {
            public void run() {
                // Cache all gradle dependency caches
                if (extension.limit == Integer.MAX_VALUE) {
                    OfflineMethods(targetProject);
                } else {
                    new Offline2(targetProject, extension.limit);
                }

                // Cache Configured Classpath in project
                new Offline3(targetProject);

                // Clean folder pom without jar
                new Thread("startCache").lock(new Runnable() {
                    @Override
                    public void run() {
                        new Cleaner(targetProject);
                    }
                });
            }
        };
        final Runnable launch = new Runnable() {
            @Override
            public void run() {
                tmp.write(new Date(), Charset.defaultCharset());
                new Thread(start).start();
            }
        };

        boolean expiredOrNotCreated = !tmp.exists() || tmp.getDateAttributes().getModified().getMinutes() >= extension.expire;
        if (extension.force) { // force cached
            new Thread(launch).start();
        } else if (expiredOrNotCreated) {
            new Thread(launch).start();
        }
    }
}

