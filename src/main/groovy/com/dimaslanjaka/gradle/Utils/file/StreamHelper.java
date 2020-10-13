package com.dimaslanjaka.gradle.Utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class StreamHelper {
	InputStream inputStream;
	String fileName;
	long fileSize;

	public StreamHelper(String fileName) throws FileNotFoundException {
		this.fileName = fileName;
		File file = new File(fileName);
		inputStream = new FileInputStream(file);
		this.fileSize = file.length();
	}
}
