package com.dimaslanjaka.gradle.testing

import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.gradle.api.Project
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

class ProjectBuilder {
    File root
    org.gradle.testfixtures.ProjectBuilder builder = org.gradle.testfixtures.ProjectBuilder.builder()
    Project project

    ProjectBuilder(File location) {
        setRoot(location)
    }

    ProjectBuilder(File location, String name) {
        this(location)
        setName(name)
    }

    ProjectBuilder() {

    }

    ProjectBuilder setRoot(File location) {
        if (!location.exists()) location.mkdirs()
        root = location
        builder.withProjectDir(root)
        return this
    }

    static def download(String url, String filename) {
        while (url) {
            new URL(url).openConnection().with { conn ->
                conn.instanceFollowRedirects = false
                url = conn.getHeaderField("Location")
                if (!url) {
                    new File(filename).withOutputStream { out ->
                        conn.inputStream.with { inp ->
                            out << inp
                            inp.close()
                        }
                    }
                }
            }
        }
    }

    def "build gradle from url"(String url = null) {
        def bl = new File(root.absolutePath, "build.gradle")
        if (!bl.parentFile.exists()) bl.parentFile.mkdirs()
        if (url == null) {
            download(
                    "https://gist.githubusercontent.com/akalongman/de76ad46d8d510e71823/raw/00c2480c8ba8cf02dd9f055c776383a8bc3db74c/build.gradle",
                    bl.absolutePath
            )
        }
    }

    void setName(String name) {
        builder.withName(name)
    }
    Git git
    def userHome = new File(System.properties["user.home"].toString())
    def mavenDir = new File(userHome, ".m2/repository")

    /**
     * Project from git
     * @param url url git repository
     * @param force force overwrite
     * @return Project* @throws Exception
     */
    Project fromGit(String url, boolean force) throws Exception {
        validate()
        if (force) if (root.exists()) root.deleteDir()
        if (!new File(root, ".git").exists()) {
            git = Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(root)
                    .setCloneAllBranches(true)
                    .call()
        } else {
            git = Git.open(root)
        }

        return project = builder.withProjectDir(root)
                .withName(root.name)
                .withGradleUserHomeDir(userHome).build()
    }

    void evaluate() {
        File settings = file(root, "settings.gradle")
    }

    static File file(String parent, String child) {
        return new File(parent, child)
    }

    static File file(File parent, String child) {
        return file(parent.absolutePath, child)
    }

    Project fromGit(String url) {
        return fromGit(url, false)
    }

    BuildResult result

    BuildResult run(String... var1) {
        create()
        result = GradleRunner.create()
                .withProjectDir(root)
                .withArguments(var1)
                .withTestKitDir(new File(root, "test-kit"))
                .withPluginClasspath()
                .build()
        return result
    }

    BuildResult run(List<String> var1) {
        String[] param = []
        var1.eachWithIndex { item, index ->
            param[index] = item
        }
        return run(param)
    }

    BuildResult run(Project project, String... param) {}

    String getResult() {
        if (result != null) return result.getOutput()
        return null
    }

    private void validate() throws Exception {
        if (root == null) {
            throw new Exception("""Root project not defined, initialize with : 
new ${this.getClass()}(new File("build/foldername")).fromGit("http://git/repo/name")
""")
        }
    }

    Project getInstance() {
        create()
        return builder.build()
    }

    File getRoot() {
        return root
    }

    File newFolder(String path) {
        File loc = new File(root.absolutePath, path)
        FileUtils.forceMkdir(loc)
        return loc
    }

    File newFolder(String... paths) throws IOException {
        if (paths.length == 0) {
            throw new IllegalArgumentException("must pass at least one path")
        } else {
            File root = this.getRoot()
            String[] arr$ = paths
            int len$ = paths.length

            for (int i$ = 0; i$ < len$; ++i$) {
                String path = arr$[i$]
                if ((new File(path)).isAbsolute()) {
                    throw new IOException("folder path '" + path + "' is not a relative path")
                }
            }

            File relativePath = null
            File file = root
            boolean lastMkdirsCallSuccessful = true
            arr$ = paths
            len$ = paths.length

            for (int i$ = 0; i$ < len$; ++i$) {
                String path = arr$[i$]
                relativePath = new File(relativePath, path)
                file = new File(root, relativePath.getPath())
                lastMkdirsCallSuccessful = file.mkdirs()
                if (!lastMkdirsCallSuccessful && !file.isDirectory()) {
                    if (file.exists()) {
                        throw new IOException("a file with the path '" + relativePath.getPath() + "' exists")
                    }

                    throw new IOException("could not create a folder with the path '" + relativePath.getPath() + "'")
                }
            }

            if (!lastMkdirsCallSuccessful) {
                throw new IOException("a folder with the path '" + relativePath.getPath() + "' already exists")
            } else {
                return file
            }
        }
    }

    static String getText(String url) throws Exception {
        URL website = new URL(url)
        URLConnection connection = website.openConnection()
        BufferedReader input = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()))

        StringBuilder response = new StringBuilder()
        String inputLine

        while ((inputLine = input.readLine()) != null)
            response.append(inputLine)

        input.close()

        return response.toString()
    }

    File newFile(String filepath) {
        File loc = new File(root, filepath)
        FileUtils.forceMkdir(loc.parentFile)
        loc.createNewFile()
        return loc
    }

    void newFile(String childProjectFile, String content) {
        writeFile(childProjectFile, content)
    }

    void writeFile(String childProjectFile, String content) throws IOException {
        def loc = file(root, childProjectFile)
        if (!loc.parentFile.exists()) FileUtils.mkdir(loc.parentFile.absolutePath)
        FileUtils.fileWrite(loc.absolutePath, content)
    }

    void create() {
        FileUtils.forceMkdir(root)
    }

    void forceCreate() {
        if (!root.exists()) create()
        else if (root.deleteDir()) create()
    }

    boolean find(String childProjectFile) {
        return new File(root, childProjectFile).exists()
    }

    ProjectBuilder setRoot(String location) {
        return setRoot(new File(location))
    }
}
