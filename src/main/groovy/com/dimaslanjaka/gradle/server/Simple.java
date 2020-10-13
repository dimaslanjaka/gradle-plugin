package com.dimaslanjaka.gradle.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Simple {
	static File userHome = new File(System.getProperty("user.home"));
	static File mavenDir = new File(userHome, ".m2/repository");

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8500), 0);

		String ROOT = mavenDir.getAbsolutePath();
		Files.walkFileTree(Paths.get(ROOT), new FileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				String dirPath = dir.toString().replace(mavenDir.toString(), "/");
				dirPath = fixPath(dirPath);
				server.createContext(dirPath, new HttpHandler() {
					@Override
					public void handle(HttpExchange httpExchange) throws IOException {
						String response = "This is the response";
						httpExchange.sendResponseHeaders(200, response.length());
						OutputStream os = httpExchange.getResponseBody();
						os.write(response.getBytes());
						os.close();
					}
				});
				//server.setExecutor(null); // creates a default executor
				//System.out.println(dirPath);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}
		});

		server.start();
	}

	static String fixPath(String path) {
		path = path.replace("\\", "/");
		return path.replaceAll("/{1,9}", "/");
	}

	static class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {

		}
	}
}
