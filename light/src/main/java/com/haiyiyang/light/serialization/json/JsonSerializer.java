package com.haiyiyang.light.serialization.json;

import java.nio.ByteBuffer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.serialization.Serializer;

public class JsonSerializer implements Serializer {

	private static final JsonSerializer JSON_SERIALIZER = new JsonSerializer();

	private JsonSerializer() {
	}

	public static JsonSerializer SINGLETON() {
		return JSON_SERIALIZER;
	}

	public ByteBuffer serialize(Object obj, Object classType) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return ByteBuffer.wrap(mapper.writeValueAsBytes(obj));
		} catch (JsonProcessingException e) {
			throw new LightException(LightException.Code.INVOKE_ERROR, "serialize error");
		}
	}

	public Object deserialize(ByteBuffer buffer, Object classType) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(buffer.array(), (Class<?>) classType);
		} catch (Exception e) {
			throw new LightException(LightException.Code.INVOKE_ERROR, "deserialize error");
		}
	}

}