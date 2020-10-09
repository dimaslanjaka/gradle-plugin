package com.dimaslanjaka.gradle.curl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CurlTest {
	public static void main(String[] args) throws IOException, InterruptedException {

		Map<String, String> map = new HashMap<>();
		map.put("Content-Type", "application/json");
		String data = "{\\\"nlquery\\\":\\\"Who is the president of Russia?\\\"}";

		String result = new Curl.Builder("http://sda.tech/earl/api/processQuery")
						.method(Curl.HttpMethod.POST)
						.headers(map)
						.data(data)
						.create()
						.call();

		System.out.println(result);
	}
}
