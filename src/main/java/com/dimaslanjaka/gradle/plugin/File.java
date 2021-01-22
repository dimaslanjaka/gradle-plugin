package com.dimaslanjaka.gradle.plugin;

import com.dimaslanjaka.gradle.plugin.date.SimpleDateFormat;
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
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.util.*;

import static com.dimaslanjaka.gradle.plugin.Utils.println;
import static com.dimaslanjaka.kotlin.Date.*;

@SuppressWarnings("serial")
public class File extends java.io.File implements Serializable {
    private final int serialVersionUID = 1; // Noncompliant; not static & int rather than long
    private java.io.File file = null;
    private boolean isFirstCreated = false;

    /**
     * Creates a new <code>File</code> instance by converting the given
     * pathname string into an abstract pathname.  If the given string is
     * the empty string, then the result is the empty abstract pathname.
     *
     * @param pathname A pathname string
     * @throws NullPointerException If the <code>pathname</code> argument is <code>null</code>
     */
    public File(@NotNull String pathname) {
        super(pathname);
        resolve(pathname);
        this.file = new java.io.File(pathname);
    }

    /**
     * Creates a new <code>File</code> instance from a parent pathname string
     * and a child pathname string.
     *
     * <p> If <code>parent</code> is <code>null</code> then the new
     * <code>File</code> instance is created as if by invoking the
     * single-argument <code>File</code> constructor on the given
     * <code>child</code> pathname string.
     *
     * <p> Otherwise the <code>parent</code> pathname string is taken to denote
     * a directory, and the <code>child</code> pathname string is taken to
     * denote either a directory or a file.  If the <code>child</code> pathname
     * string is absolute then it is converted into a relative pathname in a
     * system-dependent way.  If <code>parent</code> is the empty string then
     * the new <code>File</code> instance is created by converting
     * <code>child</code> into an abstract pathname and resolving the result
     * against a system-dependent default directory.  Otherwise each pathname
     * string is converted into an abstract pathname and the child abstract
     * pathname is resolved against the parent.
     *
     * @param parent The parent pathname string
     * @param child  The child pathname string
     * @throws NullPointerException If <code>child</code> is <code>null</code>
     */
    public File(String parent, @NotNull String child) {
        super(parent, child);
        this.file = new java.io.File(parent, child);
    }

    /**
     * Creates a new <code>File</code> instance from a parent abstract
     * pathname and a child pathname string.
     *
     * <p> If <code>parent</code> is <code>null</code> then the new
     * <code>File</code> instance is created as if by invoking the
     * single-argument <code>File</code> constructor on the given
     * <code>child</code> pathname string.
     *
     * <p> Otherwise the <code>parent</code> abstract pathname is taken to
     * denote a directory, and the <code>child</code> pathname string is taken
     * to denote either a directory or a file.  If the <code>child</code>
     * pathname string is absolute then it is converted into a relative
     * pathname in a system-dependent way.  If <code>parent</code> is the empty
     * abstract pathname then the new <code>File</code> instance is created by
     * converting <code>child</code> into an abstract pathname and resolving
     * the result against a system-dependent default directory.  Otherwise each
     * pathname string is converted into an abstract pathname and the child
     * abstract pathname is resolved against the parent.
     *
     * @param parent The parent abstract pathname
     * @param child  The child pathname string
     * @throws NullPointerException If <code>child</code> is <code>null</code>
     */
    public File(java.io.File parent, @NotNull String child) {
        super(parent, child);
        resolveDir(parent.getAbsolutePath());
        resolve(new java.io.File(parent, child).getAbsolutePath());
        this.file = new java.io.File(parent, child);
    }

    /**
     * Creates a new {@code File} instance by converting the given
     * {@code file:} URI into an abstract pathname.
     *
     * <p> The exact form of a {@code file:} URI is system-dependent, hence
     * the transformation performed by this constructor is also
     * system-dependent.
     *
     * <p> For a given abstract pathname <i>f</i> it is guaranteed that
     *
     * <blockquote><code>
     * new File(</code><i>&nbsp;f</i><code>.{@link #toURI()
     * toURI}()).equals(</code><i>&nbsp;f</i><code>.{@link #getAbsoluteFile() getAbsoluteFile}())
     * </code></blockquote>
     * <p>
     * so long as the original abstract pathname, the URI, and the new abstract
     * pathname are all created in (possibly different invocations of) the same
     * Java virtual machine.  This relationship typically does not hold,
     * however, when a {@code file:} URI that is created in a virtual machine
     * on one operating system is converted into an abstract pathname in a
     * virtual machine on a different operating system.
     *
     * @param uri An absolute, hierarchical URI with a scheme equal to
     *            {@code "file"}, a non-empty path component, and undefined
     *            authority, query, and fragment components
     * @throws NullPointerException     If {@code uri} is {@code null}
     * @throws IllegalArgumentException If the preconditions on the parameter do not hold
     * @see #toURI()
     * @see URI
     * @since 1.4
     */
    public File(@NotNull URI uri) {
        super(uri);
    }

    public File(java.io.File file) {
        super(String.valueOf(file));
        this.file = file;
    }

    /**
     * Is file first creation
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

    public void resolve(String name) {
        java.io.File file = new java.io.File(name);
        if (!file.exists()) {
            isFirstCreated = true;
            try {
                resolveDir(file.getParentFile().getAbsolutePath());
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
        FileTime fileTime;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            fileTime = Files.getLastModifiedTime(this.file.toPath());
            //return dateFormat.format(fileTime.toMillis());
            return new Date(fileTime.toMillis());
        } catch (IOException e) {
            System.err.println("Cannot get the last modified time - " + e);
            return null;
        }
    }

    @SuppressWarnings({"unused"})
    public boolean isModifiedMoreThanMinute(int i) {
        Date lastModified = getLastModified();
        return isMoreThanMinutesAgo(lastModified, i);
    }

    @SuppressWarnings({"unused"})
    public boolean isModifiedLessThanMinute(int i) {
        Date lastModified = getLastModified();
        return isLessThanMinutesAgo(lastModified, i);
    }

    @SuppressWarnings({"unused"})
    public boolean isModifiedMoreThanHour(int i) {
        Date lastModified = getLastModified();
        return isMoreThanHourAgo(lastModified, i);
    }

    @SuppressWarnings({"unused"})
    public boolean isModifiedLessThanHour(int i) {
        Date lastModified = getLastModified();
        return isLessThanHourAgo(lastModified, i);
    }

    public void write(Object any) {
        write(String.valueOf(any));
    }
}
