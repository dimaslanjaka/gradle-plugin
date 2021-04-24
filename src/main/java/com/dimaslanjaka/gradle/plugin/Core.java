package com.dimaslanjaka.gradle.plugin;

import com.dimaslanjaka.kotlin.File;
import jar.Repack;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.invocation.Gradle;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

import static com.dimaslanjaka.gradle.api.Extension.createExtension;
import static com.dimaslanjaka.gradle.plugin.Offline.OfflineMethods;
import static com.dimaslanjaka.gradle.plugin.Utils.println;

public class Core implements Plugin<Project> {
    private final Threading thread = new Threading();
    public static CoreExtension extension;
    private static Project project = null;
    public static String CONFIG_NAME = "offlineConfig";

    @Override
    public void apply(final @NotNull Project target) {
        thread.project = target;
        project = target;
        @SuppressWarnings({"unused"})
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
        new Offline3(project);

        target.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(@NotNull Project project) {
                //startCache(project);
                Threading.Once once = new Threading.Once();
                once.run(new Runnable() {
                    @Override
                    public void run() {
                        startCache(project);
                    }
                });
            }
        });
    }

    /**
     * Start cache gradle jar to local maven repository
     *
     * @param targetProject project
     */
    public void startCache(Project targetProject) {
        File tmp = new File(targetProject.getBuildDir().getAbsolutePath(), "/plugin/com.dimaslanjaka/offline");
        tmp.resolveParent();
        Runnable r = new Runnable() {
            public void run() {
                if (extension.limit == Integer.MAX_VALUE) {
                    OfflineMethods(targetProject);
                } else {
                    new Offline2(targetProject, extension.limit);
                }
            }
        };

        println(tmp.getDateAttributes());
        boolean expiredOrNotCreated = !tmp.exists(); //|| tmp.isModifiedMoreThanHour(1);
        if (expiredOrNotCreated && !extension.force) {
            tmp.write(new Date());
            new Thread(r).start();
        } else if (extension.force) {
            tmp.write(new Date());
            new Thread(r).start();
        }
    }
}

