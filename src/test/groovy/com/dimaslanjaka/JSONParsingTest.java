package com.dimaslanjaka;

import com.dimaslanjaka.gradle.json.CollectionDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class JSONParsingTest {

	List<Object> worlds;

	public void grantThatDeserializerWorksAndParseObjectArrays() {

		String worldAsString = "{\"worlds\": [" +
						"{\"name\":\"name1\",\"id\":1}," +
						"{\"name\":\"name2\",\"id\":2}," +
						"{\"name\":\"name3\",\"id\":3}" +
						"]}";

		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Collection.class, new CollectionDeserializer());
		Gson gson = builder.create();
		JSONParsingTest decoded = gson.fromJson(worldAsString, JSONParsingTest.class);

		assertNotNull(decoded);
		//assertEquals(3, decoded.worlds.size());
		//assertEquals((Long)2L, decodedObject.worlds.get(1).getId());
	}
}