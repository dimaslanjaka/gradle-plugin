package com.dimaslanjaka.webserver.httpserver;

import com.dimaslanjaka.webserver.util.logger.Logger;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.Arrays;

public class Config {
	HelpFormatter formatter = new HelpFormatter();
	//@Option(name = "--help", aliases = "-h", usage = "print usage help")
	//@Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
	private boolean printHelp = false;
	//@Option(name = "--port", aliases = {"-p"}, usage = "port to use")
	//@Option(names = {"-p", "--port"}, description = "Port Web Server")
	private int port = 8080;
	//@Option(name = "--directory", aliases = {"-d"}, usage = "web-root directory")
	//@Option(names = {"-d", "--directory"}, paramLabel = "WEBROOT", description = "Root Directory")
	private File webRoot = new File("./");
	//@Option(name = "--directory-listing", aliases = {"-l"}, usage = "allow directory-listing")
	//@Option(names = {"-l", "--directory-listing"}, description = "Directory Listing")
	private boolean directoryListingAllowed = false;
	//@Option(name = "--logfile", aliases = {"-f"}, usage = "path and name of log-file (if wanted)")
	//@Option(names = {"-f", "--logfile"}, paramLabel = "LOGGERFILE", description = "Root Directory")
	private File loggerFile = new File("build/log/webserver.log");
	private Logger logger = new Logger(this.loggerFile);

	public Config(String[] args) {
		System.out.println(Arrays.toString(args));

		// create Options object
		Options options = new Options();
		options.addOption(new Option("p", "port", true, "Port"));
		options.addOption(new Option("d", "directory", true, "Root Directory"));
		options.addOption(new Option("l", "directory-listing", true, "Listing Directory"));
		options.addOption(new Option("f", "logfile", true, "Log File"));
		options.addOption(new Option("h", "help", false, "Show Help"));

		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);
			///System.out.println(cmd.getOptionValue("p"));
			if (cmd.hasOption("p")) {
				port = Integer.parseInt(cmd.getOptionValue("p", "8500"));
			}
			if (cmd.hasOption("d")) {
				webRoot = new File(cmd.getOptionValue("d", "."));
			}
			if (cmd.hasOption("l")) {
				directoryListingAllowed = cmd.getOptionValue("l").equals("true");
			}
			if (cmd.hasOption("f")) {
				loggerFile = new File(cmd.getOptionValue("f", "./build/log/webserver.log"));
				logger = new Logger(this.loggerFile);
			}
			if (cmd.hasOption("h")) {
				formatter = new HelpFormatter();
				formatter.printHelp("java", options);
				printHelp = true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// parse arguments and adjust default config-object
		/*
		CmdLineParser parser = new CmdLineParser(args);
		try {
			parser.parseArgument(args);
			System.out.println(parser);
			if (args.getPrintHelp()) {
				parser.printUsage(System.out);
				System.exit(0);
			}
		} catch (CmdLineException e) {
			System.out.println("Error parsing arguments: " + e.getMessage());
			parser.printUsage(System.out);
			System.exit(1);
		}
		 */
	}

	public boolean getPrintHelp() {
		return printHelp;
	}

	public int getPort() {
		return port;
	}

	public File getWebRoot() {
		return webRoot;
	}

	public boolean isDirectoryListingAllowed() {
		return directoryListingAllowed;
	}

	public File getLoggerFile() {
		return loggerFile;
	}

	public Logger getLogger() {
		return logger;
	}
}