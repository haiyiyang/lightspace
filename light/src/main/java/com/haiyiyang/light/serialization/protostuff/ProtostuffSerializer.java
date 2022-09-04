package com.haiyiyang.light.serialization.protostuff;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import com.haiyiyang.light.serialization.Serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class ProtostuffSerializer implements Serializer {

	private Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();
	private final static ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();

	private ProtostuffSerializer() {
	}

	public static Serializer singleton() {
		return protostuffSerializer;
	}

	private Objenesis objenesis = new ObjenesisStd(true);

	@SuppressWarnings("unchecked")
	private <T> Schema<T> getSchema(Class<T> cls) {
		return (Schema<T>) cachedSchema.computeIfAbsent(cls, RuntimeSchema::createFrom);
	}

	@Override
	public <T> byte[] serialize(T obj) {
		@SuppressWarnings("unchecked")
		Class<T> cls = (Class<T>) obj.getClass();
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		try {
			Schema<T> schema = getSchema(cls);
			return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			buffer.clear();
		}
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		try {
			T message = (T) objenesis.newInstance(clazz);
			Schema<T> schema = getSchema(clazz);
			ProtostuffIOUtil.mergeFrom(data, message, schema);
			return message;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
