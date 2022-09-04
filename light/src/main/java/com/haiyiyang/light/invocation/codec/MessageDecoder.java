package com.haiyiyang.light.invocation.codec;

import java.util.List;

import org.tinylog.Logger;

import com.haiyiyang.light.__.U;
import com.haiyiyang.light.invocation.request.LightRequest;
import com.haiyiyang.light.invocation.response.LightResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class MessageDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < U.i6) {
			return;
		}
		in.markReaderIndex();
		byte messageType = in.readByte();
		byte serializer = in.readByte();
		int dataLength = in.readInt();
		if (in.readableBytes() < dataLength) {
			in.resetReaderIndex();
			Logger.error("The length of readable bytes is less than specified length.");
			return;
		}
		byte[] data = new byte[dataLength];
		in.readBytes(data);
		if (messageType == U.bL) {
			out.add(LightRequest.decode(serializer, data));
		} else if ((messageType == U.bR)) {
			out.add(LightResponse.decode(serializer, data));
		}
	}
}
