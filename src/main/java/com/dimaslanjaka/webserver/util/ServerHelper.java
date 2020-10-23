package com.dimaslanjaka.webserver.util;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class ServerHelper {

	/**
	 * Returns the computer's IP-address in the local network
	 *
	 * @return Computer's IP-address
	 */
	public static String getServerIp() {
		try {
			return Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException ignored) {
			return "127.0.0.1";
		}
	}

	/**
	 * Returns the canonical path to a directory
	 *
	 * @return Path to directory
	 */
	public static String getCanonicalPath(File file) {
		String canonicalPath = "";
		try {
			canonicalPath = file.getCanonicalPath();
		} catch (IOException ignored) {
		}
		return canonicalPath;
	}
}