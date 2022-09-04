package com.haiyiyang.light.serialization.json;

import com.haiyiyang.light.serialization.Serializer;

public class Json implements Serializer {
	private final static Json json = new Json();

	private Json() {

	}

	public static Serializer singleton() {
		return json;
	}

	@Override
	public <T> byte[] serialize(T obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

}
