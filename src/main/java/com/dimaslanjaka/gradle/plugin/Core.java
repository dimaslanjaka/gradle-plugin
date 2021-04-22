package com.dimaslanjaka.gradle.plugin;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.invocation.Gradle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Date;

import static com.dimaslanjaka.gradle.plugin.Utils.println;

public class Core implements Plugin<Project> {
    private final Threading thread = new Threading();
    CoreExtension extension;
    @Nullable
    private Project project = null;

    @Override
    public void apply(final @NotNull Project target) {
        thread.project = target;
        this.project = target;
        @SuppressWarnings({"unused"})
        Repository repository = new Repository(target);
        //Repack jar = new Repack(target);

        // TODO: clear gradle big log files
        Gradle gradle = target.getGradle();
        java.io.File[] cacheFiles = new File(gradle.getGradleUserHomeDir().getAbsolutePath(), "/daemon/" + gradle.getGradleVersion()).listFiles();
        if (cacheFiles != null) {
            for (java.io.File cf : cacheFiles) {
                if (cf.getName().endsWith(".out.log")) {
                    // println("Deleting gradle log file: $it") // Optional debug output
                    if (cf.delete()) {
                        println(cf + " deleted");
                    }
                }
            }
        }

        // TODO: Configuring Rules
        extension = project.getExtensions().create("offlineConfig", CoreExtension.class);

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
     * Resolve file, prevent not found exception
     *
     * @param tmp file target
     */
    private void resolveFile(File tmp) {
        if (!tmp.getParentFile().exists()) {
            if (!tmp.getParentFile().mkdirs()) {
                println("Cannot create folder " + tmp.getParentFile().getAbsolutePath());
            }
        }
        if (!tmp.exists()) {
            try {
                if (!tmp.createNewFile()) {
                    println("Cannot create file " + tmp.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Start cache gradle jar to local maven repository
     *
     * @param targetProject project
     */
    public void startCache(Project targetProject) {
        File tmp = new File(targetProject.getBuildDir().getAbsolutePath(), "/plugin/com.dimaslanjaka/offline");
        if (!tmp.exists() || tmp.isModifiedMoreThanHour(1)) {
            resolveFile(tmp);
            tmp.write(new Date());
            Runnable r = new Runnable() {
                public void run() {
                    //OfflineMethods(targetProject);
                    new Offline2(targetProject, CoreExtension.getLimit());
                }
            };

            new Thread(r).start();
        }
    }
}

