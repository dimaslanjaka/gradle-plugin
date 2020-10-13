package com.dimaslanjaka.webserver.httpserver;

import com.dimaslanjaka.webserver.util.ServerHelper;
import org.jetbrains.annotations.NotNull;

public class HTTPServer {
	private static Config config;

	/**
	 * Constructor; creates (if needed) some directories and starts a ThreadPooledServer
	 */
	public HTTPServer(@NotNull Config config) {
		// Print out some information
		System.out.println("Current version:   " + HTTPServer.class.getPackage().getImplementationVersion());
		System.out.println("Serving at:        " + "http://" + ServerHelper.getServerIp() + ":" + config.getPort());
		System.out.println("Directory:         " + ServerHelper.getCanonicalPath(config.getWebRoot()));
		System.out.println("Directory Listing: " + (config.isDirectoryListingAllowed() ? "yes" : "no"));
		System.out.println("Logfile:           " + (config.getLoggerFile() != null ? config.getLoggerFile().getAbsoluteFile() : "no"));

		// Creates a directory for the content to serve (if needed)
		if (!config.getWebRoot().exists() && !config.getWebRoot().mkdir()) {
			config.getLogger().exception("Unable to create web-root directory.");
			config.getLogger().exception("Exiting...");
			System.exit(1);
		}

		// a new ThreadPooledServer will handle all the requests
		new Thread(new ThreadPooledServer()).start();
	}

	/**
	 * Entry-Point of this application; creates a HTTP-Server-Object
	 * ```txt
	 * $ java -jar HTTP-Server.jar --help
	 * ##############################################
	 * ### a simple Java HTTP-Server              ###
	 * ### github.com/MarvinMenzerath/HTTP-Server ###
	 * ##############################################
	 * --directory (-d) FILE    : web-root directory (default: .)
	 * --directory-listing (-l) : allow directory-listing (default: false)
	 * --help (-h)              : print usage help (default: true)
	 * --logfile (-f) FILE      : path and name of log-file (if wanted)
	 * --no-gui (-g)            : do not show gui (if possible) (default: false)
	 * --port (-p) N            : port to use (default: 8080)
	 * ```
	 *
	 * @param args Passed arguments
	 */
	public static void main(String[] args) {
		// create default config-object
		config = new Config(args);
		//System.out.println(Arrays.toString(args));

		// start
		HTTPServer server = new HTTPServer(config);
	}

	public static Config getConfig() {
		return config;
	}
}