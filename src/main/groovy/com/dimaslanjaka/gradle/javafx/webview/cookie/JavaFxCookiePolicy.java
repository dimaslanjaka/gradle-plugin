package com.dimaslanjaka.gradle.javafx.webview.cookie;

import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;

public class JavaFxCookiePolicy implements CookiePolicy {
	/**
	 * One pre-defined policy which accepts all cookies.
	 */
	public static final CookiePolicy ACCEPT_ALL = (uri, cookie) -> true;
	/**
	 * One pre-defined policy which accepts no cookies.
	 */
	public static final CookiePolicy ACCEPT_NONE = (uri, cookie) -> false;
	/**
	 * One pre-defined policy which only accepts cookies from original server.
	 */
	public static final CookiePolicy ACCEPT_ORIGINAL_SERVER = (uri, cookie) -> {
		if (uri == null || cookie == null)
			return false;
		return HttpCookie.domainMatches(cookie.getDomain(), uri.getHost());
	};

	@Override
	public boolean shouldAccept(URI uri, HttpCookie cookie) {
		JavaFxCookieManager.cookieStore.add(uri, cookie);
		return true;
	}
}
