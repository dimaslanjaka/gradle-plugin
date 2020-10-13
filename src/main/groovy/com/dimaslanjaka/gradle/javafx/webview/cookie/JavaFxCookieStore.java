package com.dimaslanjaka.gradle.javafx.webview.cookie;

import com.dimaslanjaka.gradle.Utils.file.FileHelper;
import com.dimaslanjaka.gradle.Utils.json.SimpleJSON;
import org.json.simple.JSONObject;

import java.io.File;
import java.net.HttpCookie;
import java.net.URI;
import java.util.*;

public class JavaFxCookieStore implements java.net.CookieStore {
	List<Map<URI, HttpCookie>> list = new ArrayList<>();
	File saveLocation = new File("build/cookies/cookies.txt");

	public JavaFxCookieStore() {
	}

	public JavaFxCookieStore(Object saveFile) {
		if (saveFile instanceof File) {
			saveLocation = (File) saveFile;
		} else if (saveFile instanceof String) {
			File test = new File((String) saveFile);
			FileHelper.createNew(test, new JSONObject());
			saveLocation = test;
		}
	}

	public void saveToFile(File file) {
		SimpleJSON.toJson(file, Arrays.asList(list.toArray()));
	}

	public void saveToFile() {
		SimpleJSON.toJson(saveLocation, Arrays.asList(list.toArray()));
	}

	@Override
	public void add(URI uri, HttpCookie cookie) {
		Map<URI, HttpCookie> add = new HashMap<>();
		add.put(uri, cookie);
		list.add(add);
	}

	@Override
	public List<HttpCookie> get(URI uri) {
		List<HttpCookie> httpCookieList = new ArrayList<>();
		for (Map<URI, HttpCookie> uriHttpCookieMap : list) {
			if (uriHttpCookieMap.containsKey(uri)) {
				httpCookieList.addAll(uriHttpCookieMap.values());
			}
		}
		return httpCookieList;
	}

	@Override
	public List<HttpCookie> getCookies() {
		List<HttpCookie> httpCookieList = new ArrayList<>();
		for (Map<URI, HttpCookie> uriHttpCookieMap : list) {
			httpCookieList.addAll(uriHttpCookieMap.values());
		}
		return httpCookieList;
	}

	@Override
	public List<URI> getURIs() {
		List<URI> uriList = new ArrayList<>();
		for (Map<URI, HttpCookie> uriHttpCookieMap : list) {
			uriList.addAll(uriHttpCookieMap.keySet());
		}
		return uriList;
	}

	@Override
	public boolean remove(URI uri, HttpCookie cookie) {
		boolean removed = false;
		for (Map<URI, HttpCookie> uriHttpCookieMap : list) {
			if (uriHttpCookieMap.containsKey(uri)) {
				list.remove(uriHttpCookieMap);
				removed = true;
			}
		}
		return removed;
	}

	@Override
	public boolean removeAll() {
		if (list.size() > 0) {
			list.clear();
			return true;
		}
		return false;
	}
}
