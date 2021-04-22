package com.dimaslanjaka.gradle.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static com.dimaslanjaka.gradle.plugin.Utils.println;
import static com.dimaslanjaka.kotlin.Date.*;

@SuppressWarnings("serial")
public class File extends java.io.File implements Serializable {
    private final int serialVersionUID = 1; // Noncompliant; not static & int rather than long
    private java.io.File file = null;
    private boolean isFirstCreated = false;
    private BasicFileAttributes attr;

    public File(@NotNull String pathname) {
        super(pathname);
        resolve(pathname);
        this.file = new java.io.File(pathname);
    }

    public File(String parent, @NotNull String child) {
        super(parent, child);
        this.file = new java.io.File(parent, child);
    }

    public File(java.io.File parent, @NotNull String child) {
        super(parent, child);
        resolveDir(parent.getAbsolutePath());
        //resolve(new java.io.File(parent, child).getAbsolutePath());
        this.file = new java.io.File(parent, child);
    }

    public File(@NotNull URI uri) {
        super(uri);
    }

    public File(java.io.File file) {
        super(String.valueOf(file));
        this.file = file;
    }

    public File(@NotNull String temp, @NotNull File value) {
        super(temp, value.toString());
        this.file = new java.io.File(temp, value.toString());
        resolveDir(this.file.getParent());
    }

    /**
     * Is file first creation
     *
     * @return true for firstly created
     */
    public boolean isFirst() {
        return isFirstCreated;
    }

    public Map<String, Date> getAttributes() throws IOException {
        Path p = Paths.get(this.file.getAbsolutePath());
        BasicFileAttributes view = Files
                .getFileAttributeView(p, BasicFileAttributeView.class)
                .readAttributes();
        long creationTime = view.creationTime().toMillis();
        long modificationTime = view.lastModifiedTime().toMillis();
        Map<String, Date> map = new HashMap<>();
        map.put("create", new Date(creationTime));
        map.put("modification", new Date(modificationTime));
        return map;
    }

    @SuppressWarnings({"unused"})
    public Date getCreationTime() {
        try {
            return getAttributes().get("create");
        } catch (IOException e) {
            return null;
        }
    }

    public void resolve(@NotNull String name) {
        java.io.File file = new java.io.File(name);
        if (!file.exists()) {
            isFirstCreated = true;
            try {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (file.createNewFile()) {
                    println("created new file " + file.getAbsoluteFile());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unused")
    public String read() {
        StringBuilder data = new StringBuilder();
        try {
            Scanner reader = new Scanner(this.file);
            while (reader.hasNextLine()) {
                //String data = myReader.nextLine();
                //System.out.println(data);
                data.append(reader.nextLine());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            //System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return data.toString();
    }

    @SuppressWarnings("unused")
    public List<String> readAsList() {
        List<String> stringList = new ArrayList<>();
        try {
            Scanner reader = new Scanner(this.file);
            while (reader.hasNextLine()) {
                //String data = myReader.nextLine();
                //System.out.println(data);
                stringList.add(reader.nextLine());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            //System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return stringList;
    }

    public void write(String s, boolean append) {
        try {
            FileWriter writer = new FileWriter(file, append);
            writer.write(s);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unused"})
    public void write(String s) {
        write(s, false);
    }

    public void resolveDir(String name) {
        if (!new java.io.File(name).exists()) {
            if (!new java.io.File(name).mkdirs()) {
                println("cannot create parent folder");
            }
            isFirstCreated = true;
        }
    }

    public int getSerialVersionUID() {
        return serialVersionUID;
    }

    @SuppressWarnings({"all"})
    public File file_in_dir(String filename, boolean autocreate) {
        java.io.File parent = this.file;
        this.file = new File(this.file, filename);
        if (parent.isDirectory()) {
            if (autocreate) {
                if (!parent.exists())
                    if (!parent.mkdirs())
                        println("Cannot create parent dir " + parent);
                try {
                    if (!this.file.exists())
                        if (!this.file.createNewFile())
                            println("cannot create file " + filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return new File(this.file);
        }
        return null;
    }

    @SuppressWarnings({"all"})
    public File file_in_dir(String filename) {
        return file_in_dir(filename, false);
    }

    public Date getLastModified() {
        /*
        FileTime fileTime;
        try {
            fileTime = Files.getLastModifiedTime(this.file.toPath());
            //return dateFormat.format(fileTime.toMillis());
            return new Date(fileTime.toMillis());
        } catch (IOException e) {
            System.err.println("Cannot get the last modified time - " + e);
            return null;
        }
         */
        return getDate().modifiedDate;
    }

    public Date getCreateDate() {
        return getDate().creationDate;
    }

    /**
     * Get java.io.File instance
     *
     * @return java.io.File instance
     */
    public java.io.File JavaIO() {
        return new java.io.File(this.file.getAbsolutePath());
    }

    public FileDate getDate() {
        // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        // dateFormat.format(fileTime.toMillis());
        FileDate results = new FileDate(JavaIO());
        try {
            attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            results.creationDate = new Date(attr.creationTime().toMillis());
            results.accessDate = new Date(attr.lastAccessTime().toMillis());
            results.modifiedDate = new Date(attr.lastModifiedTime().toMillis());
            results.isOneHour = com.dimaslanjaka.kotlin.Date.isMoreThanHourAgo(results.modifiedDate, 1);
            results.isOneMinute = com.dimaslanjaka.kotlin.Date.isMoreThanMinuteAgo(results.modifiedDate, 1);
        } catch (IOException e) {
            System.err.println("Cannot get the create date - " + e);
        }
        return results;
    }

    @SuppressWarnings({"unused"})
    public boolean isModifiedMoreThanMinute(int i) {
        if (!this.file.exists()) return false;
        Date lastModified = getLastModified();
        return isMoreThanMinuteAgo(lastModified, i);
    }

    @SuppressWarnings({"unused"})
    public boolean isModifiedLessThanMinute(int i) {
        if (!this.file.exists()) return false;
        Date lastModified = getLastModified();
        return isLessThanMinuteAgo(lastModified, i);
    }

    @SuppressWarnings({"unused"})
    public boolean isModifiedMoreThanHour(int i) {
        if (!this.file.exists()) return false;
        Date lastModified = getLastModified();
        return isMoreThanHourAgo(lastModified, i);
    }

    @SuppressWarnings({"unused"})
    public boolean isModifiedLessThanHour(int i) {
        if (!this.file.exists()) return false;
        Date lastModified = getLastModified();
        return isLessThanHourAgo(lastModified, i);
    }

    public void write(Object any) {
        write(String.valueOf(any));
    }

    static class FileDate {
        Date creationDate;
        Date modifiedDate;
        Date accessDate;
        java.io.File file;
        /**
         * Is file modified more than one hour ago
         */
        boolean isOneHour = false;
        /**
         * Is file modified more than one minute ago
         */
        boolean isOneMinute = false;

        public FileDate(java.io.File targetFile) {
            this.file = targetFile;
        }

        public FileDate() {

        }

        @Override
        public String toString() {
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            return gson.toJson(this);
        }
    }
}
