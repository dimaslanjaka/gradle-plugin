package com.dimaslanjaka.gradle.Utils.file;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class FileHelper {
	public static final String ALL_HACKERS_FILE = "allhackers.csv";
	public static final String ALL_HACKERS_HEADER = "NOME, SOBRENOME, EMAIL";
	private static final String FILE_NAME = "hackers_%s.csv";
	private static final String FILE_NAME_EXTENSION = ".jpg";
	private static final String NEW_LINE = "\n";
	public static Logger log = LoggerFactory.getLogger(FileHelper.class);

	public static String getName() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();

		return String.format(getFilePath().concat(FILE_NAME), dateFormat.format(date));
	}

	public static String getFilePath() {
		return System.getenv().get(EnvironmentVariable.FILE_PATH).concat("/");
	}

	public static void copy(File source, File target) throws IOException {
		FileUtils.copyFile(source, target);
	}

	/**
	 * Check is a file is writable.
	 * Detects write issues on external SD card.
	 *
	 * @param file The file.
	 * @return true if the file is writable.
	 */
	public static boolean isWritable(final File file) {
		boolean isExisting = file.exists();

		try {
			FileOutputStream output = new FileOutputStream(file, true);
			try {
				output.close();
			} catch (IOException e) {
				// do nothing.
			}
		} catch (FileNotFoundException e) {
			if (!file.isDirectory()) {
				return false;
			}
		}
		boolean result = file.canWrite();

		// Ensure that file is not created during this process.
		if (!isExisting) {
			//noinspection ResultOfMethodCallIgnored
			file.delete();
		}

		return result;
	}

	/**
	 * Checks if file could be read or created
	 *
	 * @param file - The file (as a String).
	 * @return true if file's is writable.
	 */
	public static boolean isReadable(final String file) {
		return isReadable(new File(file));
	}

	/**
	 * Checks if file could be read or created
	 *
	 * @param file - The file.
	 * @return true if file's is writable.
	 */
	public static boolean isReadable(final File file) {
		if (!file.isFile()) {
			log.debug("isReadable(): Not a File");

			return false;
		}

		return file.exists() && file.canRead();
	}

	public static List<Boolean> delete(File path, String... exclude) {
		List<String> excludedPaths = Arrays.asList(exclude);
		List<Boolean> result = new ArrayList<>();
		File[] files = path.listFiles();
		if (files != null) {
			for (File file : files) {
				if (!excludedPaths.contains(file.getName())) {
					if (file.isDirectory()) {
						delete(file);
					}
					result.add(file.delete());
				}
			}
		}
		return result;
	}

	public static File findFile(File folder, Pattern regex) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (regex.matcher(file.getName()).find()) {
					return file;
				}
			}
		}
		return null;
	}

	public static boolean createFolderIfDoesNotExistsFor(String folderName) {
		File folderFile = new File(folderName);
		if (!folderFile.exists()) {
			return folderFile.mkdir();
		}
		return false;
	}

	public static boolean createFileIfDoesNotExistsFor(String fileName) throws IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			return file.createNewFile();
		}
		return false;
	}

	public static boolean createNew(Object path, Object data) {
		File filepath = null;
		String filedata = "";
		if (path instanceof String) {
			filepath = new File((String) path);
		} else if (path instanceof File){
			filepath = (File) path;
		}
		if (data instanceof String) {
			filedata = (String) data;
		} else if (data instanceof JSONObject || data instanceof JSONArray || data instanceof Gson){
			filedata = data.toString();
		} else {
			filedata = String.valueOf(data);
		}
		if (filepath != null) {
			try {
				createFileIfDoesNotExistsFor(filepath.getAbsolutePath());
				if (filepath.canWrite()){
					FileWriter myWriter = new FileWriter(filepath);
					myWriter.write(filedata);
					myWriter.close();
					return true;
				} else {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void createFor(byte[] image, String fileName) throws IOException {
		File file = new File(formatPath(fileName, FILE_NAME_EXTENSION));
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(image);
		fileOutputStream.close();
	}

	public void getFileWriter(String line) throws IOException {
		FileWriter fileWriter = new FileWriter(getName(), true);
		fileWriter.write(line);
		fileWriter.close();
	}

	public List<String> readFile() throws IOException {
		File file = new File(getName());
		List<String> lines = new ArrayList<>();

		if (file.exists()) {
			try (BufferedReader fileReader = new BufferedReader(new FileReader(getName()))) {
				String line = fileReader.readLine();

				while (line != null) {
					lines.add(line);
					line = fileReader.readLine();
				}
			}
		}

		return lines;
	}

	public String formatPath(String fileName, String extension) {
		createFolderIfDoesNotExistsFor(getPhotoPath());
		return String.format("%s%s%s", getPhotoPath(), fileName, extension);
	}

	public boolean delete(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			return file.delete();
		}
		return false;
	}

	public List<File> getFilesFrom(String filePath) {
		File folder = new File(filePath);

		List<File> files = new ArrayList<>();

		for (File file : folder.listFiles()) {
			if (file.isFile() && isCsv(file)) {
				files.add(file);
			}
		}

		return files;
	}

	public void merge(List<File> files) throws IOException {
		String header = ALL_HACKERS_HEADER.concat(NEW_LINE);
		addLine(header);
		for (File file : files) {
			FileInputStream stream = new FileInputStream(file.getAbsolutePath());
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = bufferedReader.readLine();

			while (line != null) {
				addLine(line);
				line = bufferedReader.readLine();
			}
		}
	}

	public StreamHelper getStreamFor(String fileName) throws FileNotFoundException {
		return new StreamHelper(fileName);
	}

	private String getPhotoPath() {
		return System.getenv().get(EnvironmentVariable.PHOTO_PATH).concat("/");
	}

	private boolean isCsv(File file) {
		return file.getName().endsWith(".csv");
	}

	private void addLine(String csvLine) throws IOException {
		FileWriter fileWriter = new FileWriter(getFilePath().concat(ALL_HACKERS_FILE), true);
		fileWriter.write(csvLine + NEW_LINE);
		fileWriter.close();
	}
}
