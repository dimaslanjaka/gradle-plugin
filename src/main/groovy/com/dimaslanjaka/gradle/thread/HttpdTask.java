package com.dimaslanjaka.gradle.thread;

import com.dimaslanjaka.webserver.StartHttpd;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpdTask extends DefaultTask {
	Logger logger = LoggerFactory.getLogger(getClass());

	@TaskAction
	void greet() {
		try {
			StartHttpd.run();
			StartHttpd.loopStart();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
