package com.haiyiyang.light.invocation.response;

import com.haiyiyang.light.__.U;
import com.haiyiyang.light.invocation.LightMessage;
import com.haiyiyang.light.serialization.SerializerFactory;

public class LightResponse implements LightMessage {

	private static final long serialVersionUID = 1L;

	private int messageId;
	private byte serializer;
	private String error;
	private Object result;

	public LightResponse() {

	}

	public LightResponse(int messageId, byte serializer) {
		super();
		this.messageId = messageId;
		this.serializer = serializer;
	}

	@Override
	public byte getType() {
		return U.bR;
	}

	@Override
	public byte[] encode() {
		return SerializerFactory.getSerializer(serializer).serialize(this);
	}

	public static LightResponse decode(byte serializer, byte[] data) {
		return SerializerFactory.getSerializer(serializer).deserialize(data, LightResponse.class);
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public byte getSerializer() {
		return serializer;
	}

	public void setSerializer(byte serializer) {
		this.serializer = serializer;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
