package com.haiyiyang.light.invocation.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.tinylog.Logger;

import com.haiyiyang.light.__.U;
import com.haiyiyang.light.invocation.IpPort;
import com.haiyiyang.light.invocation.request.LightRequest;
import com.haiyiyang.light.invocation.response.LightResponse;
import com.haiyiyang.light.invocation.response.ResponseFuture;

import io.netty.channel.Channel;

public class LightClient {

	private static Map<Integer, ResponseFuture> pendingFuture = new ConcurrentHashMap<>(U.i1024);

	public static ResponseFuture sendRequest(IpPort ipPort, LightRequest lightRequest, boolean returnVoid)
			throws InterruptedException {
		ResponseFuture responseFuture = new ResponseFuture(lightRequest, returnVoid);
		pendingFuture.put(lightRequest.getMessageId(), responseFuture);
		sendRequest(NettyChannel.getChannel(ipPort), lightRequest);
		return responseFuture;
	}

	static void handleServerResponse(LightResponse msg) {
		try {
			Integer messageId = msg.getMessageId();
			ResponseFuture responseFuture = pendingFuture.get(messageId);
			if (responseFuture != null) {
				pendingFuture.remove(messageId);
				responseFuture.set(msg);
			} else {
				Logger.error("LightClientHandler no messageId: " + messageId);
			}
		} catch (Exception ex) {
			Logger.error(ex);
		}
	}

	public static void sendRequest(Channel channel, LightRequest lightRequest) throws InterruptedException {
		channel.writeAndFlush(lightRequest).sync();
		Logger.info("Send:{}", lightRequest.id());
	}
}
