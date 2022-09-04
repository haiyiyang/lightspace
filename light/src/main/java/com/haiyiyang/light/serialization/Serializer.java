package com.haiyiyang.light.serialization;

import com.haiyiyang.light.__.U;

public interface Serializer {

	public final static byte JSON = U.bJ;

	public final static byte PROTOSTUFF = U.bP;

	public <T> byte[] serialize(T obj);

	public <T> T deserialize(byte[] data, Class<T> clazz);

}
