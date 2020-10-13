package com.dimaslanjaka.gradle.javafx.webview.cookie;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.util.*;

public class JavaFxCookieHandler extends CookieHandler {
	public static final Set<URI> uris = new HashSet<>();
	public static CookieManager cookieManager = new JavaFxCookieManager();

	public JavaFxCookieHandler(){
		super();
		setDefault(cookieManager);
	}

	@Override
	public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
		uris.add(uri);
		return cookieManager.get(uri, requestHeaders);
	}

	@Override
	public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
		uris.add(uri);
		cookieManager.put(uri, responseHeaders);
	}

	void save() throws IOException {
		System.out.println("Saving cookies");
		System.out.println(uris);
		for (URI uri : uris) {
			System.out.println(uri);
			System.out.println(cookieManager.get(uri, new HashMap<>()));
		}
	}
}
