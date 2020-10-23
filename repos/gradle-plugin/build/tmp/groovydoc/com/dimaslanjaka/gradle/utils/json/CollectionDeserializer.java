package com.dimaslanjaka.gradle.utils.json;

import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CollectionDeserializer implements JsonDeserializer<Collection<?>> {

	@Override
	public Collection<?> deserialize(JsonElement json, Type typeOfT,
	                                 JsonDeserializationContext context) throws JsonParseException {
		Type realType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];

		return parseAsArrayList(json, realType);
	}

	/**
	 * Parse json as array list
	 * @param json json element to be parsed
	 * @param type type document of json
	 * @return ArrayList or type json document
	 * @param <T> Global Type
	 */
	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> parseAsArrayList(JsonElement json, T type) {
		ArrayList<T> newArray = new ArrayList<T>();
		Gson gson = new Gson();

		JsonArray array = json.getAsJsonArray();

		for (JsonElement json2 : array) {
			T object = (T) gson.fromJson(json2, (Class<?>) type);
			newArray.add(object);
		}

		return newArray;
	}

}
