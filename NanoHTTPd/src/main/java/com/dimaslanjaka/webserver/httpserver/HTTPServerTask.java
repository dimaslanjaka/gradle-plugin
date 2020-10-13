package com.dimaslanjaka.webserver.httpserver;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

public class HTTPServerTask extends DefaultTask {
	@TaskAction
	public void run() {
		File userHome = new File(System.getProperty("user.home").toString());
		File reposDir = new File(userHome, ".m2/repository");
		String[] args = new String[]{"-p", "8500", "-d", reposDir.getAbsolutePath().replace("\\", "/"), "-l", "true"};
		HTTPServer server = new HTTPServer(new Config(args));
	}
}
