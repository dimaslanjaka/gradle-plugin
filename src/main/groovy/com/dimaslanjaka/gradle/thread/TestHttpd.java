package com.dimaslanjaka.gradle.thread;

import com.dimaslanjaka.webserver.StartHttpd;

public class TestHttpd {
	public static void main(String[] args) throws InterruptedException {
		StartHttpd.run();
		StartHttpd.loopStart();
	}
}
