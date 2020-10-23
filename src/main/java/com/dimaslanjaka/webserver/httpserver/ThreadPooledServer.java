package com.dimaslanjaka.webserver.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPooledServer implements Runnable {
	protected ExecutorService threadPool;

	/**
	 * Start this Thread and open the Pool for 10 Threads
	 */
	public void run() {
		threadPool = Executors.newFixedThreadPool(10);

		ServerSocket socket = null;
		try {
			socket = new ServerSocket(HTTPServer.getConfig().getPort());
		} catch (IOException | IllegalArgumentException e) {
			HTTPServer.getConfig().getLogger().exception(e.getMessage());
			HTTPServer.getConfig().getLogger().exception("Exiting...");
			System.exit(1);
		}

		while (!Thread.currentThread().isInterrupted()) {
			try {
				threadPool.execute(new HTTPThread(socket.accept()));
			} catch (IOException e) {
				HTTPServer.getConfig().getLogger().exception(e.getMessage());
			}
		}
	}
}