package com.dimaslanjaka.gradle.plugin;

import java.io.File;

public interface CoreExtensionInterface {
    String home = System.getProperty("user.home");
    int limit = Integer.MAX_VALUE;
    File localRepository = new com.dimaslanjaka.kotlin.File(home, ".m2/repository");
    public boolean force = false;
    public boolean debug = false;

    boolean validExtension(String s);

    String getHome();

    boolean getDebug();

    void setDebug(boolean debug);

    int getLimit();

    void setLimit(int i);

    File getLocalRepository();

    void setLocalRepository(File file);
}