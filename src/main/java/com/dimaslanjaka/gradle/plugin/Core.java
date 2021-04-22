package com.dimaslanjaka.gradle.plugin;

import jar.Repack;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.invocation.Gradle;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;

import static com.dimaslanjaka.gradle.plugin.Utils.println;

public class Core implements Plugin<Project> {
    private final Threading thread = new Threading();
    public static Ext extension;
    private static Project project = null;

    public interface CoreExtension {
        String home = System.getProperty("user.home");
        int limit = Integer.MAX_VALUE;
        File localRepository = new File(home, ".m2/repository");
        public boolean force = false;

        void setLimit(int i);

        int getLimit();

        void setLocalRepository(File file);

        File getLocalRepository();
    }

    public static class Ext implements CoreExtension {
        public int limit = CoreExtension.limit;
        public File localRepository = CoreExtension.localRepository;
        public boolean force = CoreExtension.force;

        public Ext(Project p) {
            project = p;
        }

        @Override
        public void setLimit(int i) {
            limit = i;
        }

        @Override
        public int getLimit() {
            return limit;
        }

        @Override
        public void setLocalRepository(File file) {
            localRepository = file;
        }

        @Override
        public File getLocalRepository() {
            return localRepository;
        }
    }

    @Override
    public void apply(final @NotNull Project target) {
        thread.project = target;
        project = target;
        @SuppressWarnings({"unused"})
        Repository repository = new Repository(target);
        Repack jar = new Repack(target);

        // TODO: clear gradle big log files
        Gradle gradle = target.getGradle();
        java.io.File[] cacheFiles = new File(gradle.getGradleUserHomeDir().getAbsolutePath(), "/daemon/" + gradle.getGradleVersion()).listFiles();
        if (cacheFiles != null) {
            for (java.io.File cf : cacheFiles) {
                if (cf.getName().endsWith(".log")) { // .out.log
                    // println("Deleting gradle log file: $it") // Optional debug output
                    if (cf.delete()) {
                        println(cf + " deleted");
                    }
                }
            }
        }

        // TODO: Configuring Rules
        extension = (Ext) createExtension(project, "offlineConfig", Ext.class, project);
        project.getExtensions().create("offlineRepositoryRoot", File.class, extension.localRepository.getAbsolutePath());
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

    public static Object createExtension(Project p, String name, Class clazz, Object... constructionArguments) {
        return p.getExtensions().create(name, clazz, constructionArguments);
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
        if (!tmp.getParentFile().exists()) tmp.getParentFile().mkdirs();
        Runnable r = new Runnable() {
            public void run() {
                //OfflineMethods(targetProject);
                new Offline2(targetProject, extension.limit);
            }
        };

        if ((!tmp.exists() || tmp.isModifiedMoreThanHour(1)) && !extension.force) {
            tmp.write(new Date());
            new Thread(r).start();
        } else if (extension.force) {
            tmp.write(new Date());
            new Thread(r).start();
        }
    }
}

