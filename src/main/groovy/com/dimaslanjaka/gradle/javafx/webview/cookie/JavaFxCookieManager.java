package com.dimaslanjaka.gradle.javafx.webview.cookie;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JavaFxCookieManager extends CookieManager {
	public static CookieManager cookieManager = new CookieManager();
	public static CookieStore cookieStore = cookieManager.getCookieStore();
	public static CookiePolicy cookiePolicy = new JavaFxCookiePolicy();

	public JavaFxCookieManager() {
		super();
		cookieManager = this;
		cookieManager.setCookiePolicy(cookiePolicy);
		setDefault(new JavaFxCookieHandler());
	}

	public JavaFxCookieManager(CookieStore store, CookiePolicy policy) {
		super(store, policy);
		if (store != null) cookieStore = store;
		cookiePolicy = policy == null ? cookiePolicy.ACCEPT_ORIGINAL_SERVER : cookiePolicy;
		cookieManager = this;
		cookieManager.setCookiePolicy(cookiePolicy);
	}

	public JavaFxCookieManager(java.net.CookiePolicy policy) {
		super(cookieStore, (policy != null ? policy : cookiePolicy));
		cookiePolicy = policy == null ? cookiePolicy.ACCEPT_ORIGINAL_SERVER : cookiePolicy;
		cookieManager = this;
		cookieManager.setCookiePolicy(cookiePolicy);
	}

	public static void saveCookies() throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException, IOException {
		CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
		Field f = cookieManager.getClass().getDeclaredField("store");
		f.setAccessible(true);
		Object cookieStore = f.get(cookieManager);

		Field bucketsField = Class.forName("com.sun.webkit.network.CookieStore").getDeclaredField("buckets");
		bucketsField.setAccessible(true);
		Map buckets = (Map) bucketsField.get(cookieStore);
		f.setAccessible(true);
		Map<String, Collection> cookiesToSave = new HashMap<>();
		for (Object o : buckets.entrySet()) {
			Map.Entry pair = (Map.Entry) o;
			String domain = (String) pair.getKey();
			Map cookies = (Map) pair.getValue();
			cookiesToSave.put(domain, cookies.values());
		}

		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(cookiesToSave);
		System.out.println(json);

		Files.write(Paths.get("cookies.json"), json.getBytes());
	}

	public CookieManager setCookieManager(CookieManager cookieManager1) {
		cookieManager = cookieManager1;
		return cookieManager;
	}

	public CookieManager getCookieManager() {
		return cookieManager;
	}
}
