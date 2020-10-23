package com.dimaslanjaka.webserver.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;

public class FileManager {
	private static final HashMap<String, String> mimeTypes = new HashMap<>();
	private static boolean mimeTypesInitCompleted = false;

	/**
	 * This HashMap contains some file-extensions, which tell the server to use a different mime-type
	 *
	 * @return All configured file-extensions and their mime-type
	 */
	public static HashMap<String, String> getMimeTypes() {
		if (mimeTypesInitCompleted) return mimeTypes;

		// Images
		mimeTypes.put(".gif", "image/gif");
		mimeTypes.put(".jpg", "image/jpeg");
		mimeTypes.put(".jpeg", "image/jpeg");
		mimeTypes.put(".png", "image/png");
		mimeTypes.put(".ico", "image/x-ico");

		// Audio
		mimeTypes.put(".mp3", "audio/mpeg");
		mimeTypes.put(".wav", "audio/wav");
		mimeTypes.put(".flac", "audio/flac");
		mimeTypes.put(".ogg", "audio/x-ogg");
		mimeTypes.put(".mp4", "video/mp4");
		mimeTypes.put(".flv", "video/x-flv");

		// Websites
		mimeTypes.put(".html", "text/html");
		mimeTypes.put(".htm", "text/html");
		mimeTypes.put(".shtml", "text/html");
		mimeTypes.put(".xhtml", "text/html");
		mimeTypes.put(".css", "text/css");
		mimeTypes.put(".js", "application/javascript");

		// Linux
		mimeTypes.put(".deb", "application/x-debian-package");

		// Archives
		mimeTypes.put(".zip", "application/zip");
		mimeTypes.put(".tar", "application/x-tar");
		mimeTypes.put(".gtar", "application/x-gtar");
		mimeTypes.put(".tar.gz", "application/gzip");
		mimeTypes.put(".tgz", "application/gzip");
		mimeTypes.put(".gz", "application/gzip");

		// Other
		mimeTypes.put(".txt", "text/plain");
		mimeTypes.put(".log", "text/plain");
		mimeTypes.put(".md", "text/x-markdown");
		mimeTypes.put(".pdf", "application/pdf");
		mimeTypes.put(".xml", "application/xml");
		mimeTypes.put(".java", "text/plain");

		mimeTypesInitCompleted = true;
		return mimeTypes;
	}

	/**
	 * Converts a file's size into a readable figure and a fitting unit
	 * Source: Mr Ed: Format file size as MB, GB etc (http://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc)
	 *
	 * @param size File size (in Bits)
	 * @return Readable file size
	 */
	public static String getReadableFileSize(long size) {
		if (size <= 0) return "0";

		final String[] units = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	/**
	 * Returns a fitting content-type (mime-type) for a specific file-extension
	 *
	 * @param file File to check
	 * @return File's content-type
	 */
	public static String getContentType(File file) {
		return getMimeTypes().get(getFileExtension(file));
	}

	/**
	 * Returns the file's extension
	 *
	 * @param file File to check
	 * @return File's extension
	 */
	private static String getFileExtension(File file) {
		String filename = file.getName();
		int pos = filename.lastIndexOf(".");
		if (pos >= 0) return filename.substring(pos).toLowerCase();
		return "";
	}
}