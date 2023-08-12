package com.dimaslanjaka.gradle.plugin;

import static com.dimaslanjaka.gradle.plugin.Offline.OfflineMethods;

import com.dimaslanjaka.gradle.api.Extension;
import com.dimaslanjaka.java.Thread;
import com.dimaslanjaka.java.cmd;
import com.dimaslanjaka.kotlin.File;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Date;

import jar.Repack;

/**
 * plugin core
 */
public class Core implements Plugin<Project> {
    public static CoreExtension extension;
    public static String CONFIG_NAME = "offlineConfig";
    private final Threading thread = new Threading();

    @Override
    public void apply(final @NotNull Project target) {
        thread.project = target;
        cmd.Companion.setProject(target);

        // TODO: Configuring Rules
        extension = (CoreExtension) Extension.create(target, CONFIG_NAME, CoreExtension.class, target);

        // TODO: Applying to root project
        Repository repository = new Repository(target);
        Repack jar = new Repack(target);
        Utils.cleanGradleDaemonLog(target);
        new Offline3(target);
        new Info(target);

        target.afterEvaluate(project -> {
            startCache(project);
            if (com.dimaslanjaka.gradle.api.Utils.isAndroid(project)) {
                new Android(project);
            }
        });
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
                new Thread(targetProject, "Offline1-2").lock(new Runnable() {
                    @Override
                    public void run() {
                        if (extension.limit == Integer.MAX_VALUE) {
                            OfflineMethods(targetProject);
                        } else {
                            new Offline2(targetProject, extension.limit);
                        }
                    }
                });

                // Clean folder pom without jar
                new Thread(targetProject, "Cleaner").lock(new Runnable() {
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

