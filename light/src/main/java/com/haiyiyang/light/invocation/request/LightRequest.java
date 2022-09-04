package com.haiyiyang.light.invocation.request;

import java.util.Arrays;

import com.haiyiyang.light.__.U;
import com.haiyiyang.light.invocation.LightMessage;
import com.haiyiyang.light.serialization.SerializerFactory;

public class LightRequest implements LightMessage {

	private static final long serialVersionUID = 1L;

	private long sourceId;
	private long requestId;
	private long currentTime;
	private long clientId;
	private int messageId;
	private byte methodId;
	private byte serializer;
	private String serviceName;
	private Object[] methodArgs;

	@Override
	public byte getType() {
		return U.bL;
	}

	@Override
	public byte[] encode() {
		return SerializerFactory.getSerializer(serializer).serialize(this);
	}

	public static LightRequest decode(byte serializer, byte[] data) {
		return SerializerFactory.getSerializer(serializer).deserialize(data, LightRequest.class);
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public byte getMethodId() {
		return methodId;
	}

	public void setMethodId(byte methodId) {
		this.methodId = methodId;
	}

	public byte getSerializer() {
		return serializer;
	}

	public void setSerializer(byte serializer) {
		this.serializer = serializer;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Object[] getMethodArgs() {
		return methodArgs;
	}

	public void setMethodArgs(Object[] methodArgs) {
		this.methodArgs = methodArgs;
	}

	public String id() {
		return Long.toString(this.sourceId, 36) + "_" + Long.toString(this.requestId, 36);
	}

	@Override
	public String toString() {
		return "LightRequest [sourceId=" + sourceId + ", requestId=" + requestId + ", currentTime=" + currentTime
				+ ", clientId=" + clientId + ", messageId=" + messageId + ", methodId=" + methodId + ", serializer="
				+ serializer + ", serviceName=" + serviceName + ", methodArgs=" + Arrays.toString(methodArgs) + "]";
	}

}
