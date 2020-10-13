package com.dimaslanjaka.webserver.util.logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Logger {
	private final boolean writeToFile;
	private BlockingQueue<String> queue;

	/**
	 * Constructor; creates (if needed) a new Thread, which will write new log-entries into the logfile using a queue
	 *
	 * @param logfile log-file to use
	 */
	public Logger(File logfile) {
		if (logfile != null) {
			queue = new ArrayBlockingQueue<>(4096);
			new Thread(new LogfileWriter(queue, logfile)).start();
			writeToFile = true;
		} else {
			writeToFile = false;
		}
	}

	/**
	 * Log an access (200, 304, ...)
	 *
	 * @param code HTTP-status-code
	 * @param file Accessed file
	 * @param ip   Client's IP-address
	 */
	public void access(int code, String file, String ip) {
		write("[" + code + "] [" + ip.replace("/", "") + "] " + file);
	}

	/**
	 * Log an error (403, 404, ...)
	 *
	 * @param code HTTP-status-code
	 * @param file Accessed file
	 * @param ip   Client's IP-address
	 */
	public void error(int code, String file, String ip) {
		write("[" + code + "] [" + ip.replace("/", "") + "] " + file);
	}

	/**
	 * Internal error / exception
	 *
	 * @param message Exception's message
	 */
	public void exception(String message) {
		write("[EXC] " + message);
	}

	/**
	 * Prints the log-entry on the console (virtual console) an puts it into the LogfileWriter's queue
	 *
	 * @param message Entry to log
	 */
	private void write(String message) {
		// Build up log-entry
		String out = "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()) + "] " + message;

		// Print on console
		System.out.println(out);

		// Write into file
		if (writeToFile) {
			try {
				queue.offer(out, 1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}