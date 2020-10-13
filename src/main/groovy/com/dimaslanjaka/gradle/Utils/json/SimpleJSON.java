package com.dimaslanjaka.gradle.Utils.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Super Simple JSON Helper Java
 */
public class SimpleJSON {
	/**
	 * Convert Map to JSON and write to a file
	 */
	public static void toJson(File saveFile, Map<String, Object> stringObjectMap) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			mapper.writeValue(saveFile, stringObjectMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Convert List to JSON and write to a file
	 *
	 * @usage SimpleJSON.toJson(file, Arrays.asList ( list.toArray ()));
	 */
	public static void toJson(File saveFile, List<Object> mapList) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			mapper.writeValue(saveFile, mapList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read JSON from a file into a Map
	 */
	public static Map<String, Object> fromJson(File loadFile) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			TypeReference<Map<String, Object>> type = new TypeReference<Map<String, Object>>() {
			};
			return mapper.readValue(loadFile, type);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
