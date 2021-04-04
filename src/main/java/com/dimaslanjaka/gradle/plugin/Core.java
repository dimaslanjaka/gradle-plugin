package com.dimaslanjaka.gradle.plugin;

import com.dimaslanjaka.kotlin.Temp;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.invocation.Gradle;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

import jar.Repack;

import static com.dimaslanjaka.gradle.plugin.Offline.OfflineMethods;
import static com.dimaslanjaka.gradle.plugin.Utils.println;

public class Core implements Plugin<Project> {
    private final Threading thread = new Threading();
    private Project project = null;

    @Override
    public void apply(final @NotNull Project target) {
        thread.project = target;
        @SuppressWarnings({"unused"})
        Repository repository = new Repository(target);
        Repack jar = new Repack(target);

        // TODO: clear gradle big log files
        Gradle gradle = target.getGradle();
        java.io.File[] cacheFiles = new File(gradle.getGradleUserHomeDir().getAbsolutePath(), "/daemon/" + gradle.getGradleVersion()).listFiles();
        for (java.io.File cf : cacheFiles) {
            if (cf.getName().endsWith(".out.log")) {
                // println("Deleting gradle log file: $it") // Optional debug output
                if (cf.delete()) println(cf + " deleted");
            }
        }

        target.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(@NotNull Project project) {
                Threading.Once once = new Threading.Once();
                once.run(new Runnable() {
                    @Override
                    public void run() {
                        startCache();
                    }
                });
            }
        });
    }

    public void startCache() {
        String identifier = MD5.get("OfflineMethods");
        File tmp = new Temp().getTempFile(identifier);
        if (tmp != null && tmp.isModifiedMoreThanHour(1) || tmp.isFirst()) {
            println("starting cache transformer");
            tmp.write(new Date());
            Runnable r = new Runnable() {
                public void run() {
                    OfflineMethods(project != null ? project : null);
                }
            };

            new Thread(r).start();
        }
    }
}

