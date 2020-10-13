package com.dimaslanjaka.gradle.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Recursive listing with SimpleFileVisitor in JDK 7.
 */
public final class FileListingVisitor {

	public static void main(String[] args) throws IOException {
		File userHome = new File(System.getProperty("user.home"));
		File mavenDir = new File(userHome, ".m2/repository");

		String ROOT = mavenDir.getAbsolutePath();
		FileVisitor<Path> fileProcessor = new ProcessFile();
		Files.walkFileTree(Paths.get(ROOT), fileProcessor);
	}

	static final class ProcessFile extends SimpleFileVisitor<Path> {
		@Override
		public FileVisitResult visitFile(
						Path file, BasicFileAttributes attrs
		) throws IOException {
			//System.out.println("Processing file:" + file);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(
						Path dir, BasicFileAttributes attrs
		) throws IOException {
			//System.out.println("Processing directory:" + dir);
			return FileVisitResult.CONTINUE;
		}
	}
}