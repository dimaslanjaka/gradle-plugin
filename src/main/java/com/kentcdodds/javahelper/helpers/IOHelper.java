package com.kentcdodds.javahelper.helpers;

import com.kentcdodds.javahelper.model.HelperFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Kent
 */
public class IOHelper {

  public static final String homeDir = System.getProperty("user.home");
  public static final String osName = System.getProperty("os.name");
  public static final String[] badFileNameCharacters = new String[]{"\\", "/", ":", "*", "?", "\"", "<", ">", "|"};

  //<editor-fold defaultstate="collapsed" desc="Filename Methods">

  /**
   * Removes unsafe filename characters from the given string.
   *
   * @param string  the string to make filename safe
   * @param replace what you want bad characters to be replaced with (note, if you give an invalid character for this
   *                parameter, it will be automatically replaced with an empty string, the same applies if null is given).
   * @return the string with characters replaced
   */
  public static String makeFilenameSafe(String string, String replace) {
    if (replace == null) {
      replace = "";
    } else {
      replace = StringHelper.replaceInString(replace, "", badFileNameCharacters);
    }
    return StringHelper.replaceInString(string, replace, badFileNameCharacters);
  }

  /**
   * Checks whether the given string contains any bad filename characters.
   *
   * @param string the string to check
   * @return whether the given string has bad filename characters
   */
  public static boolean hasBadFileNameCharacters(String string) {
    for (String badChar : badFileNameCharacters) {
      if (string.contains(badChar)) {
        return true;
      }
    }
    return false;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="To String Methods">

  /**
   * Takes the file and returns it in a string. Uses UTF-8 encoding
   *
   * @param fileLocation
   * @return the file in String form
   * @throws IOException when trying to read from the file
   */
  public static String fileToString(String fileLocation) throws IOException {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileLocation), StandardCharsets.UTF_8));
    return readerToString(bufferedReader);
  }

  /**
   * Takes the given resource (based on the given class) and returns that as a string. Uses UTF-8 encoding
   *
   * @param resourceLocation of the file
   * @param klass            class to get the resource from
   * @return the file as a string
   * @throws IOException when trying to read from the file
   */
  public static String resourceToString(Class klass, String resourceLocation) throws IOException {
    InputStream inputStream = klass.getResourceAsStream(resourceLocation);
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    return readerToString(bufferedReader);
  }

  /**
   * Opens a stream to the given URL and reads everything into the returned String. Uses UTF-8 encoding
   *
   * @param urlString
   * @return
   * @throws MalformedURLException
   * @throws IOException
   */
  public static String webpageToString(String urlString) throws MalformedURLException, IOException {
    return readerToString(getBufferedReader(urlString));
  }

  /**
   * Returns all the lines in the Reader's stream as a String
   *
   * @param reader
   * @return
   * @throws IOException when trying to read from the file
   */
  public static String readerToString(BufferedReader reader) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    String readLine;
    while ((readLine = reader.readLine()) != null) {
      stringBuilder.append(readLine).append(StringHelper.newline);
    }
    reader.close();
    return stringBuilder.toString();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="File copy methods">

  /**
   * Copies the given resource file to the given destination. Does not check whether the destination exists.
   *
   * @param klass
   * @param resourceLocation
   * @param outputFile
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void copyResource(Class klass, String resourceLocation, File outputFile) throws FileNotFoundException, IOException {
    InputStream inputStream = klass.getResourceAsStream(resourceLocation);
    saveInputStream(inputStream, outputFile);
  }

  /**
   * Copies the given source to the given destination. Does not check whether the destination exists.
   *
   * @param outputFile move to
   * @throws FileNotFoundException file not found
   * @throws IOException           error
   */
  public static void copyFile(File sourceFile, File outputFile) throws FileNotFoundException, IOException {
    InputStream inputStream = new FileInputStream(sourceFile);
    saveInputStream(inputStream, outputFile);
  }

  /**
   * Downloads the given url location to the given output file destination.
   *
   * @param urlString  the source of the file
   * @param outputFile the destination of the file
   * @throws MalformedURLException
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void downloadFile(String urlString, File outputFile) throws MalformedURLException, FileNotFoundException, IOException {
    URL url = new URL(urlString);
    ReadableByteChannel rbc = Channels.newChannel(url.openStream());
    FileOutputStream fos = new FileOutputStream(outputFile);
    fos.getChannel().transferFrom(rbc, 0, 1 << 24);
  }

  /**
   * Downloads the file represented by the given urlString into the returned byte array.
   *
   * @param urlString the source of the file
   * @return the bytes of the downloaded file
   * @throws MalformedURLException
   * @throws IOException
   */
  public static byte[] downloadFile(String urlString) throws MalformedURLException, IOException {
    URL url = new URL(urlString);
    return downloadFile(url);
  }

  /**
   * Downloads the file represented by the given urlString into the returned byte array.
   *
   * @param url the source of the file
   * @return the bytes of the downloaded file
   * @throws MalformedURLException
   * @throws IOException
   */
  public static byte[] downloadFile(URL url) throws MalformedURLException, IOException {
    InputStream inputStream = url.openStream();
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    int byteRead;
    byte[] buf = new byte[1024];
    while ((byteRead = inputStream.read(buf)) != -1) {
      outStream.write(buf, 0, byteRead);
    }
    return outStream.toByteArray();
  }

  /**
   * Saves the given InputStream to a file at the destination. Does not check whether the destination exists.
   *
   * @param inputStream input stream to save
   * @param outputFile  output destination file
   * @throws FileNotFoundException file not found exception
   * @throws IOException           error
   */
  public static void saveInputStream(InputStream inputStream, File outputFile) throws FileNotFoundException, IOException {
    int size = 4096;
    OutputStream out = new FileOutputStream(outputFile);
    byte[] buffer = new byte[size];
    int length;
    while ((length = inputStream.read(buffer)) > 0) {
      out.write(buffer, 0, length);
    }
    out.close();
    inputStream.close();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="File Bytes methods">

  /**
   * Saves the given bytes to the output file. Creates a HelperFile with the given bytes and location then calls
   * copyBytes() on it.
   *
   * @param bytes    data bytes
   * @param location save location
   * @throws FileNotFoundException file not found
   * @throws IOException           error
   */
  public static void saveBytesToFile(byte[] bytes, String location) throws FileNotFoundException, IOException {
    HelperFile helperFile = new HelperFile(bytes, location);
    helperFile.saveBytes();
  }

  /**
   * Reads the given bytes into an array. The bytes must not exceed the array size limit of Integer.MAX_VALUE (which is
   * 2^31 -1). This means you can only use this method with files under 2 gigabytes
   *
   * @param file
   * @return
   * @throws FileNotFoundException when trying to get the file
   * @throws IOException           when trying to read the file
   * @throws Exception             when trying to read a file too big for a byte[]
   */
  public static byte[] getFileBytes(File file) throws FileNotFoundException, IOException, Exception {
    HelperFile helperFile = new HelperFile(file);
    return helperFile.getBytes();
  }

  /**
   * Reads the given bytes into an array. The bytes must not exceed the array size limit of Integer.MAX_VALUE (which is
   * 2^31 -1). This means you can only use this method with files under 2 gigabytes
   *
   * @param location resource location
   * @return bytes
   * @throws FileNotFoundException when trying to get the file
   * @throws IOException           when trying to read the file
   * @throws Exception             when trying to read a file too big for a byte[]
   */
  public static byte[] getResourceBytes(Class klass, String location) throws IOException, Exception {
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    InputStream inputStream = klass.getResourceAsStream(location);
    byte[] buffer = new byte[1024];
    int length;
    while ((length = inputStream.read(buffer)) > 0) {
      byteOut.write(buffer, 0, length);
    }
    byteOut.close();
    return byteOut.toByteArray();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Zipping methods">

  /**
   * Zips the directory to the given destination.
   *
   * @param destination destination directory
   * @param directory   directory to be zipped to destination
   * @throws FileNotFoundException file not found exception
   * @throws IOException           error exception
   */
  public static void zipFolder(File destination, File directory) throws FileNotFoundException, IOException {
    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destination));
    addDirectoryToZip(directory, directory.getName(), out);
  }

  /**
   * Zips the directory and returns the byte array of the resulting zipped file.
   *
   * @param directory directory to be zipped
   * @throws FileNotFoundException file not found
   * @throws IOException           error exception
   */
  public static byte[] zipFolder(File directory) throws FileNotFoundException, IOException {
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ZipOutputStream out = new ZipOutputStream(byteOut);
    addDirectoryToZip(directory, directory.getName(), out);
    byteOut.close();
    return byteOut.toByteArray();
  }

  /**
   * Gets the files from the given directory and adds them to the output stream based on the pathToTop variable.
   *
   * @param directory the directory to get files from to add to the zip
   * @param pathToTop the path to the parent of this zip file
   * @param out       the ZipOutputStream to put the files into
   * @throws IOException
   */
  private static void addDirectoryToZip(File directory, String pathToTop, ZipOutputStream out) throws IOException {
    File[] files = directory.listFiles();
    byte[] buffer = new byte[1024];

    for (int i = 0; i < files.length; i++) {
      if (files[i].isDirectory()) {
        addDirectoryToZip(files[i], pathToTop + "\\" + files[i].getName(), out);
        continue;
      }
      FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
      out.putNextEntry(new ZipEntry(pathToTop + "\\" + files[i].getName()));
      int len;
      while ((len = in.read(buffer)) > 0) {
        out.write(buffer, 0, len);
      }
      out.closeEntry();
      in.close();
    }
  }

  /**
   * This uses the java.util.zip library to zip the given files to the given destination. It creates a FileOutputStream
   * for the given destination, then calls writeZipInToOut(fileOutput, files); The benchmark for this method has proven
   * to be slightly slower than the zipFiles(HelperFile...) method, but this one can handle any file size while the
   * other is limited to Integer.MAX_VALUE (2 GB sized files)
   *
   * @param destination
   * @param files
   * @throws FileNotFoundException
   * @throws IOException
   * @throws Exception             if the files are bigger than Integer.MAX_VALUE (2 GB)
   */
  public static void zipFiles(File destination, File... files) throws FileNotFoundException, IOException, Exception {
    FileOutputStream fileOutput = new FileOutputStream(destination);
    writeZipInToOut(fileOutput, files);
  }

  /**
   * This uses the java.util.zip library to zip the given HelperFiles and return a byte array of the zip file. Creates a
   * ByteArrayOutputStream, calls writeZipInToOut(byteOutput, files); and returns byteOutput.toByteArray()
   *
   * @param files
   * @return byte[] of the zip file of the given files
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static byte[] zipFiles(HelperFile... files) throws FileNotFoundException, IOException {
    ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
    writeZipInToOut(byteOutput, files);
    return byteOutput.toByteArray();
  }

  public static void writeZipInToOut(OutputStream outStream, File... files) throws IOException {
    ZipOutputStream out = new ZipOutputStream(outStream);
    for (int i = 0; i < files.length; i++) {
      out.putNextEntry(new ZipEntry(files[i].getName()));

      if (files[i] instanceof HelperFile) {
        //Writes the HelperFile's bytes to the zipped file
        out.write(((HelperFile) files[i]).getBytes());
      } else {
        FileInputStream fileIn = new FileInputStream(files[i]);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileIn.read(buffer)) != -1) {
          out.write(buffer, 0, length);
        }
        fileIn.close();
      }
      // Complete the entry
      out.closeEntry();
    }
  }

  /**
   * Takes a zipped file and unzips it to the given destination directory
   *
   * @param zippedFile
   * @param destination
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void unzipFiles(File zippedFile, File destination) throws FileNotFoundException, IOException {
    ZipInputStream zipIn;
    FileInputStream fileInputStream = new FileInputStream(zippedFile);
    zipIn = new ZipInputStream(fileInputStream);
    ZipEntry nextEntry;
    while ((nextEntry = zipIn.getNextEntry()) != null) {
      FileOutputStream fileOut = new FileOutputStream(destination.getPath() + "\\" + nextEntry.getName());
      byte[] buffer = new byte[1024];
      int length;
      while ((length = zipIn.read(buffer)) != -1) {
        fileOut.write(buffer, 0, length);
      }
      fileOut.close();
    }
    zipIn.close();
  }

  /**
   * Takes a zipped file and unzips it to the given destination directory
   *
   * @param zippedFile zip file location
   * @throws FileNotFoundException file not found
   * @throws IOException           error occurred
   */
  public static java.util.List<HelperFile> unzipFiles(HelperFile zippedFile) throws FileNotFoundException, IOException {
    java.util.List<HelperFile> helperFiles = new java.util.ArrayList<HelperFile>();
    ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(zippedFile.getBytes()));
    ZipEntry nextEntry;
    while ((nextEntry = zipIn.getNextEntry()) != null) {
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int length;
      while ((length = zipIn.read(buffer)) != -1) {
        byteOut.write(buffer, 0, length);
      }
      helperFiles.add(new HelperFile(byteOut.toByteArray(), nextEntry.getName()));
      byteOut.close();
    }
    return helperFiles;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Find and replace methods">

  /**
   * Replaces the given find with the given replace in the given file. Reads the file to a string, replaces the find
   * with the replace in the string and prints the new string on top of the original file.
   *
   * @param file    the file to conduct the find/replace on
   * @param find    the find to replace
   * @param replace the text to replace the find with
   * @return whether anything was different in the replace call. It does not print over the original file if nothing is
   * different
   * @throws IOException
   */
  public static boolean replaceInFile(File file, String find, String replace) throws IOException {
    String fileString = fileToString(file.getPath());
    String newString = fileString.replace(find, replace);
    if (fileString.equals(newString)) {
      return false;
    }
    print(file, newString);
    return true;
  }

  /**
   * Replaces the given find with the given replace in all the files under the given directory of the given file
   * extension(s). If the file extension is null (or omitted), it will ignore the file extension and apply to all files
   * regardless of their extension. Calls replaceInFile(file, find, replace) on each of the files. Skips any files which
   * throw IO exceptions and adds it to the list which is returned. You can call
   * <pre>{@code sendReplaceInAllFilesToPrinter(List<File>[]) }</pre> on the returned array and it will send information to the Printer
   *
   * @param topDirectory the topDirectory to find the files in
   * @param sublevels    determines how many subdirectories to go before it stops adding files. Give 0 to get files only in
   *                     the given directory
   * @param find         the text to be replaced by the given replace
   * @param replace      the text to replace the given find with
   * @param extension    the any-number of extensions to apply the find/replace to. Omit for any type
   * @return An array of Lists of Files: arry[0] = files which applied and were successful in the find/replace
   * operation; arry[1] = files which may have been successful, but there would have been no change to the file during
   * the find/replace operation so the file was not overwritten; arry[2] = files which threw an IOException for any
   * reason.
   */
  public static java.util.List<File>[] replaceInAllFiles(File topDirectory, int sublevels, String find, String replace, String... extension) {
    java.util.List<File> files = getAllFiles(topDirectory, sublevels, extension);
    java.util.List<File> appliedFiles = new java.util.ArrayList<File>();
    java.util.List<File> errorFiles = new java.util.ArrayList<File>();
    java.util.List<File> unappliedFiles = new java.util.ArrayList<File>();
    for (File file : files) {
      try {
        boolean applied = replaceInFile(file, find, replace);
        if (applied) {
          appliedFiles.add(file);
        } else {
          unappliedFiles.add(file);
        }
      } catch (IOException ex) {
        errorFiles.add(file);
        Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return new java.util.List[]{appliedFiles, unappliedFiles, errorFiles};
  }

  /**
   * This is helpful just for seeing a report of a call to replaceInAllFiles().
   *
   * @param replaced an array of lists of files from replaceInAllFiles()
   * @return String representation of the replaced lists
   * @see #replaceInAllFiles(File, int, String, String, String...) ()
   * @see #sendReplaceInAllFilesToPrinter(List[]) ()
   */
  public static String getReplaceInAllFilesAsString(java.util.List<File>[] replaced) {
    StringBuilder sb = new StringBuilder();
    java.util.List<File> appliedFiles = replaced[0];
    java.util.List<File> unappliedFiles = replaced[1];
    java.util.List<File> errorFiles = replaced[2];

    sb.append("Applied files");
    sb.append(StringHelper.newline);
    sb.append("\t").append(StringHelper.splitBy(StringHelper.newline + "\t", appliedFiles));
    sb.append("Total Applied Files: ").append(appliedFiles.size());

    sb.append(StringHelper.newline);

    sb.append("Unapplied files");
    sb.append(StringHelper.newline);
    sb.append("\t").append(StringHelper.splitBy(StringHelper.newline + "\t", unappliedFiles));
    sb.append("Total Unapplied Files: ").append(unappliedFiles.size());

    sb.append(StringHelper.newline);

    sb.append("Error files");
    sb.append(StringHelper.newline);
    sb.append("\t").append(StringHelper.splitBy(StringHelper.newline + "\t", errorFiles));
    sb.append("Total Error Files: ").append(errorFiles.size());

    sb.append(StringHelper.newline);

    sb.append("Total files: ").append(appliedFiles.size() + unappliedFiles.size() + errorFiles.size());
    return sb.toString();
  }

  /**
   * Prints the given list array of files. It's pretty much only useful for the return on the replaceInAllFiles method.
   * NOTE: Uses PrinterHelper
   *
   * @param replaced
   * @see #replaceInAllFiles(File, int, String, String, String...) ()
   * @see #getReplaceInAllFilesAsString(List[]) ()
   */
  public static void sendReplaceInAllFilesToPrinter(java.util.List<File>[] replaced) {
    PrinterHelper.println(getReplaceInAllFilesAsString(replaced));
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Get files and directories">

  /**
   * Gets all the files under the given file (not including the given file).
   *
   * @param file           the file to get files under
   * @param subdirectories determines how many subdirectories to go before it stops adding files. Give 0 to get files
   *                       only in the given directory (or just call getDirectoryFiles(file, extension) instead). If you want to get all files
   *                       in all subdirectories, give anything less than -2. Be careful though, if it's a high parent directory, this might
   *                       take a while to return :)
   * @param extension      the extension(s) to add to the returned list. If null or omitted will add all files to the
   *                       returned list (not including directories)
   * @return
   */
  public static java.util.List<File> getAllFiles(File file, int subdirectories, String... extension) {
    java.util.List<File> filesList = new java.util.ArrayList<File>();
    if (subdirectories == -2) {
      //This is -2 because if a user gives a directory and the subdirectories as 0, it should skip this twice.
      //It's not a < -1 because if they want to get all files in the directory without care for subdirectories.
      return filesList; //Important it doesn't return null.
    }
    if (file.isDirectory()) {
      File[] listFiles = file.listFiles();
      if (listFiles != null) {
        for (File file1 : listFiles) {
          filesList.addAll(getAllFiles(file1, subdirectories - 1, extension));
        }
      }
    } else if (file.isFile()) {
      if (extension == null || extension.length == 0) {
        filesList.add(file);
      } else {
        for (String string : extension) {
          if (file.getName().endsWith(string)) {
            filesList.add(file);
          }
        }
      }
    }
    return filesList;
  }

  /**
   * Gets files in the given directory.
   *
   * @param directory the directory to search for files in.
   * @param extension the text the file should end with to be added to the returned list
   * @return a list of the files in the directory
   */
  public static java.util.List<File> getDirectoryFiles(File directory, String... extension) {
    File[] listFiles = directory.listFiles();
    java.util.List<File> filesList = new java.util.ArrayList<File>();
    for (File file : listFiles) {
      if (file.isFile()) {
        if (extension == null || extension.length == 0) {
          filesList.add(file);
        } else {
          for (String ext : extension) {
            if (file.getName().endsWith(ext)) {
              filesList.add(file);
            }
          }
        }
      }
    }
    return filesList;
  }

  /**
   * Gets directories in the given directory.
   *
   * @param directory the directory to search for files in.
   * @return a list of the files in the directory
   */
  public static java.util.List<File> getDirectoryDirectories(File directory) {
    File[] listFiles = directory.listFiles();
    java.util.List<File> filesList = new java.util.ArrayList<File>();
    for (File file : listFiles) {
      if (file.isDirectory()) {
        filesList.add(file);
      }
    }
    return filesList;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Object save and load methods">

  /**
   * Saves the given object to the given destination. The object and all it's variables must implement
   * java.io.Serializable or the variables must have the keyword "transient" in front of it.
   *
   * @param object
   * @param savePath
   * @throws IOException
   */
  public static void saveObject(Object object, String savePath) throws IOException {
    FileOutputStream f_out = new FileOutputStream(savePath);
    ObjectOutputStream o_out = new ObjectOutputStream(f_out);
    o_out.writeObject(object);
    o_out.close();
  }

  /**
   * Opens an object from the given openPath and returns it
   *
   * @param openPath
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public static Object loadObject(String openPath) throws IOException, ClassNotFoundException {
    FileInputStream f_in = new FileInputStream(openPath);
    ObjectInputStream o_in = new ObjectInputStream(f_in);
    o_in.close();
    return o_in.readObject();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Other IO methods">

  /**
   * Very simple convenience method. Creates a ProcessBuilder and
   *
   * @param file
   * @throws IOException
   */
  public static void showInExplorerOrFinder(File file) throws IOException {
    if (osName.contains("Windows")) {
      new ProcessBuilder("explorer.exe", "/select," + file.getPath()).start();
    } else if (osName.contains("Mac")) {
      Runtime.getRuntime().exec("open -R " + file.getPath()); //TODO: See if this works.
    } else if (osName.contains("Linux")) {
      //TODO: Handle this
    }
  }

  /**
   * Makes sure that the given file's parent directory exists. Creates it if not.
   *
   * @param file the file to check
   */
  public static void checkDirectory(File file) {
    if (!new File(file.getParent()).exists()) {
      new File(file.getParent()).mkdir();
    }
  }

  /**
   * Prints the given file string to the file destination. This same operation could be done by calling
   * saveBytes(string.getBytes(), destination). When benchmarking, this method was found to be only a few milliseconds
   * faster.
   *
   * @param destination
   * @param string
   * @throws FileNotFoundException
   */
  public static void print(File destination, String string) throws FileNotFoundException {
    PrintWriter pw = null;
    try {
      pw = new PrintWriter(destination, "UTF-8");
      pw.print(string);
    } catch (UnsupportedEncodingException ex) {
      Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      pw.close();
    }
  }

  /**
   * Using the given urlString, this method creates and returns the buffered reader for that URL. Specifies UTF-8 format
   *
   * @param urlString
   * @return
   * @throws MalformedURLException
   * @throws IOException
   */
  public static BufferedReader getBufferedReader(String urlString) throws MalformedURLException, IOException {
    URL url = new URL(urlString);
    InputStream is = url.openStream();
    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    return br;
  }
  //</editor-fold>
}
