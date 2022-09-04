package com.haiyiyang.light.serialization;

import com.haiyiyang.light.serialization.json.Json;
import com.haiyiyang.light.serialization.protostuff.ProtostuffSerializer;

public class SerializerFactory {

	public static Serializer getSerializer(byte serializer) {
		return serializer == 11 ? Json.singleton() : ProtostuffSerializer.singleton();
	}

}