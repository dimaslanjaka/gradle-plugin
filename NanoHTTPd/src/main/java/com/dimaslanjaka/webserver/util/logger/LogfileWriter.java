package com.dimaslanjaka.webserver.util.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

public class LogfileWriter implements Runnable {
	private final BlockingQueue<String> queue;
	private final File logfile;

	/**
	 * Constructor; saves the used BlockingQueue and logfile
	 *
	 * @param queue   Queue, which contains all the new entries
	 * @param logfile File, in which new entries have to be written into
	 */
	public LogfileWriter(BlockingQueue<String> queue, File logfile) {
		this.queue = queue;
		this.logfile = logfile;
	}

	/**
	 * Writes new entries into the logfile
	 */
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(logfile, true))) {
				printWriter.append(queue.take()).append("\r\n");
				printWriter.close();
			} catch (IOException | InterruptedException ignored) {
			}
		}
	}
}