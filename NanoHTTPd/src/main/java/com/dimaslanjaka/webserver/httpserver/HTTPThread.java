package com.dimaslanjaka.webserver.httpserver;

import com.dimaslanjaka.webserver.util.FileManager;
import com.dimaslanjaka.webserver.util.ServerHelper;
import com.dimaslanjaka.webserver.util.logger.Logger;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HTTPThread implements Runnable {
	private Socket socket;
	private File webRoot;
	private boolean allowDirectoryListing;
	private Logger logger;
	private SimpleDateFormat dateFormat;

	/**
	 * Constructor; saves passed arguments
	 *
	 * @param socket used Socket
	 */
	public HTTPThread(Socket socket) {
		this.socket = socket;
		this.webRoot = HTTPServer.getConfig().getWebRoot();
		this.allowDirectoryListing = HTTPServer.getConfig().isDirectoryListingAllowed();
		this.logger = HTTPServer.getConfig().getLogger();

		dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * "Heart of the server": Handles the client's request and sends a response
	 */
	public void run() {
		// Try-With-Block frames the whole request
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
		     BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())) {

			// Timeout of 5 seconds to clean up dead connections
			socket.setSoTimeout(5000);

			// Parse request
			String line;
			ArrayList<String> request = new ArrayList<>();
			try {
				while ((line = in.readLine()) != null && line.length() != 0) {
					request.add(line);
				}
			} catch (IOException e) {
				// Unable to parse request
				sendError(out, 400, "Bad Request");
				logger.exception(e.getMessage());
				return;
			}

			// Request is empty
			if (request.isEmpty()) return;

			// Only accept HTTP 1.0 / 1.1 requests
			if (!request.get(0).endsWith(" HTTP/1.0") && !request.get(0).endsWith(" HTTP/1.1")) {
				sendError(out, 400, "Bad Request");
				logger.error(400, "Bad Request: " + request.get(0), socket.getInetAddress().toString());
				return;
			}

			// Only accept GET or POST or HEAD requests
			boolean isPostRequest = false;
			boolean isHeadRequest = false;
			if (!request.get(0).startsWith("GET ") && !request.get(0).startsWith("POST ") && !request.get(0).startsWith("HEAD ")) {
				// Method is not implemented
				sendError(out, 501, "Not Implemented");
				logger.error(501, "Not Implemented: " + request.get(0), socket.getInetAddress().toString());
				return;
			} else if (request.get(0).startsWith("POST ")) {
				isPostRequest = true;
			} else if (request.get(0).startsWith("HEAD ")) {
				isHeadRequest = true;
			}

			// Specify the requested file / path
			String wantedFile;
			String path;
			File file;

			// GET-request anticipated
			wantedFile = request.get(0).substring(4, request.get(0).length() - 9);
			if (isPostRequest || isHeadRequest) wantedFile = request.get(0).substring(5, request.get(0).length() - 9);

			// GET-request's arguments have to be removed
			if (!isPostRequest && request.get(0).contains("?")) {
				path = wantedFile.substring(0, wantedFile.indexOf("?"));
			} else {
				path = wantedFile;
			}

			// Which file or directory has been requested?
			try {
				file = new File(webRoot, URLDecoder.decode(path, "UTF-8")).getCanonicalFile();
			} catch (IOException e) {
				logger.exception(e.getMessage());
				return;
			}

			// Directory contains an index-file? Send this file instead of directory-listing
			if (file.isDirectory()) {
				File indexFile = new File(file, "index.html");
				if (indexFile.exists() && !indexFile.isDirectory()) {
					file = indexFile;

					// add "/index.html" to path
					if (wantedFile.contains("?")) {
						wantedFile = wantedFile.substring(0, wantedFile.indexOf("?")) + "/index.html" + wantedFile.substring(wantedFile.indexOf("?"));
					}
				}
			}

			if (!file.toString().startsWith(ServerHelper.getCanonicalPath(webRoot))) {
				// Requested file is _not_ inside the webRoot --> 403
				if (isHeadRequest) {
					sendLightHeader(out, 403, "Forbidden");
				} else {
					sendError(out, 403, "Forbidden");
				}
				logger.error(403, wantedFile, socket.getInetAddress().toString());
			} else if (!file.exists()) {
				// Requested file / directory does _not_ exist --> 404
				if (isHeadRequest) {
					sendLightHeader(out, 404, "Not Found");
				} else {
					sendError(out, 404, "Not Found");
				}
				logger.error(404, wantedFile, socket.getInetAddress().toString());
			} else if (file.isDirectory()) {
				// Requested "file" is a directory --> Directory Listing

				// Check if directory-listing is allowed
				if (!allowDirectoryListing) {
					if (isHeadRequest) {
						sendLightHeader(out, 403, "Forbidden");
					} else {
						sendError(out, 403, "Forbidden");
					}
					logger.error(403, wantedFile, socket.getInetAddress().toString());
					return;
				}

				// Replace every "%20"-whitespace with a real whitespace
				path = path.replace("%20", " ");

				File[] files = file.listFiles();

				// Directory is empty
				if (files != null) {
					if (files.length == 0) {
						if (isHeadRequest) {
							sendLightHeader(out, 404, "Not Found");
						} else {
							sendError(out, 404, "Not Found");
						}
						logger.error(404, wantedFile, socket.getInetAddress().toString());
						return;
					}
				} else {
					// Windows-error-handling (e.g. requesting "Documents and Settings" instead of "Users")
					if (isHeadRequest) {
						sendLightHeader(out, 403, "Forbidden");
					} else {
						sendError(out, 403, "Forbidden");
					}
					logger.error(403, wantedFile, socket.getInetAddress().toString());
					return;
				}

				String output = WebResources.getDirectoryTemplate("Index of " + path, buildDirectoryListing(path, files));

				// Add a closing slash
				if (!wantedFile.endsWith("/")) wantedFile += "/";

				// Send headers and content
				sendHeader(out, 200, "OK", "text/html", -1, System.currentTimeMillis());
				if (!isHeadRequest) {
					out.write(output.getBytes());
				}
				logger.access(200, wantedFile, socket.getInetAddress().toString());
			} else {
				// Single file requested: send it

				// Check for "If-Modified-Since"-header
				Date lastModifiedHeader = null;
				for (String s : request) {
					if (s.toLowerCase().startsWith("if-modified-since: ")) {
						try {
							lastModifiedHeader = dateFormat.parse(s.substring(19));
						} catch (ParseException ignored) {
						}
					}
				}

				if (lastModifiedHeader != null && (new Date(file.lastModified()).equals(lastModifiedHeader) || new Date(file.lastModified()).before(lastModifiedHeader) || new Date(file.lastModified()).toString().equals(lastModifiedHeader.toString()))) {
					// File was not modified after time in header
					logger.access(304, wantedFile, socket.getInetAddress().toString());
					sendLightHeader(out, 304, "Not Modified");
				} else {
					// File was modified after time in header / no time in header
					logger.access(200, wantedFile, socket.getInetAddress().toString());
					if (isHeadRequest) {
						sendHeader(out, 200, "OK", FileManager.getContentType(file), file.length(), file.lastModified());
					} else {
						sendFile(file, out);
					}
				}
			}
		} catch (IOException e) {
			logger.exception(e.getMessage());
		}

		try {
			socket.close();
		} catch (IOException e) {
			logger.exception(e.getMessage());
		}
	}

	/**
	 * Sends a single file though a passed BufferedOutputStream
	 *
	 * @param file File to send
	 * @param out  BufferedOutputStream to send this file to
	 */
	private void sendFile(File file, BufferedOutputStream out) {
		// Prepare InputStream
		try (InputStream reader = new BufferedInputStream(new FileInputStream(file))) {
			// No ContentType found? Start a simple download
			String contentType = FileManager.getContentType(file);
			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			// Send headers and content
			sendHeader(out, 200, "OK", contentType, file.length(), file.lastModified());
			try {
				byte[] buffer = new byte[8192];
				int bytesRead;
				while ((bytesRead = reader.read(buffer)) > 0) {
					out.write(buffer, 0, bytesRead);
				}
				reader.close();
			} catch (NullPointerException | IOException e) {
				// Throws a "Broken Pipe" or "Socket Write Error" Exception if the client closes the connection before file-transfer completed
				logger.exception(e.getMessage());
			}
		} catch (IOException e) {
			logger.exception(e.getMessage());
		}
	}

	/**
	 * Creates a directory-file-listing using the passed arguments
	 *
	 * @param path  Path in which the files are
	 * @param files Files in the given path
	 * @return a HTML-file-list
	 */
	private String buildDirectoryListing(String path, File[] files) {
		// Sort all files: first directories, then files
		Arrays.sort(files, (f1, f2) -> {
			if (f1.isDirectory() && !f2.isDirectory()) {
				return -1;
			} else if (!f1.isDirectory() && f2.isDirectory()) {
				return 1;
			} else {
				return f1.toString().compareToIgnoreCase(f2.toString());
			}
		});

		// Prepare table-output
		StringBuilder content = new StringBuilder("<table><tr><th></th><th>Name</th><th>Last modified</th><th>Size</th></tr>");

		// Add "Parent Directory"-entry (if not in webRoot)
		if (!path.equals("/")) {
			String parentDirectory = path.substring(0, path.length() - 1);
			int lastSlash = parentDirectory.lastIndexOf("/");
			if (lastSlash > 1) {
				parentDirectory = parentDirectory.substring(0, lastSlash);
			} else {
				parentDirectory = "/";
			}

			content.append("<tr><td class=\"center\"><div class=\"back\">&nbsp;</div></td>" + "<td><a href=\"").append(parentDirectory.replace(" ", "%20")).append("\">Parent Directory</a></td>").append("<td></td>").append("<td></td></tr>");
		}

		if (path.equals("/")) path = ""; // small adjustment

		// Add every file to the output-table
		for (File myFile : files) {
			// Get some meta-data
			String filename = myFile.getName();
			String img;
			String fileSize = FileManager.getReadableFileSize(myFile.length());
			if (myFile.isDirectory()) {
				img = "<div class=\"directory\">&nbsp;</div>";
				fileSize = "-";
			} else {
				img = "<div class=\"file\">&nbsp;</div>";
			}

			// Put all the data into a column
			content.append("<tr><td class=\"center\">").append(img).append("</td>").append("<td><a href=\"").append(path.replace(" ", "%20")).append("/").append(filename.replace(" ", "%20")).append("\">").append(filename).append("</a></td>").append("<td>").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myFile.lastModified())).append("</td>").append("<td>").append(fileSize).append("</td></tr>");
		}

		// Finish table and return the result
		content.append("</table>");

		return content.toString();
	}

	/**
	 * Send a HTTP 1.1 Header to the client
	 *
	 * @param out           Used BufferedOutputStream
	 * @param code          HTTP-status-code
	 * @param codeMessage   HTTP-status-code-message
	 * @param contentType   ContentType of the data to send
	 * @param contentLength Size of the data to send
	 * @param lastModified  Time when the accessed file was changed (e.g. for caching)
	 */
	private void sendHeader(BufferedOutputStream out, int code, String codeMessage, String contentType, long contentLength, long lastModified) {
		try {
			out.write(("HTTP/1.1 " + code + " " + codeMessage + "\r\n" +
							"Date: " + dateFormat.format(new Date()) + "\r\n" +
							"Content-Type: " + contentType + "; charset=utf-8\r\n" +
							((contentLength != -1) ? "Content-Length: " + contentLength + "\r\n" : "") +
							"Last-Modified: " + dateFormat.format(new Date(lastModified)) + "\r\n" +
							"X-Powered-By: a simple Java HTTP-Server\r\n" +
							"\r\n").getBytes());
		} catch (IOException e) {
			logger.exception(e.getMessage());
		}
	}

	/**
	 * Send a "light" HTTP 1.1 Header to the client and closes the connection
	 *
	 * @param out         Used BufferedOutputStream
	 * @param code        HTTP-status-code
	 * @param codeMessage HTTP-status-code-message
	 */
	private void sendLightHeader(BufferedOutputStream out, int code, String codeMessage) {
		try {
			out.write(("HTTP/1.1 " + code + " " + codeMessage + "\r\n" +
							"Date: " + dateFormat.format(new Date()) + "\r\n" +
							"X-Powered-By: a simple Java HTTP-Server\r\n" +
							"Connection: close\r\n" +
							"\r\n").getBytes());
		} catch (IOException e) {
			logger.exception(e.getMessage());
		}
	}

	/**
	 * Send an error-page to the client
	 *
	 * @param out     Used BufferedOutputStream
	 * @param code    HTTP-status-code (e.g. 404)
	 * @param message Additional text (e.g. Not Found)
	 */
	private void sendError(BufferedOutputStream out, int code, String message) {
		// Prepare the output
		String output = WebResources.getErrorTemplate("Error " + code + ": " + message);

		// Send headers
		sendHeader(out, code, message, "text/html", output.length(), System.currentTimeMillis());

		try {
			// Send content
			out.write(output.getBytes());
		} catch (IOException e) {
			logger.exception(e.getMessage());
		}
	}
}