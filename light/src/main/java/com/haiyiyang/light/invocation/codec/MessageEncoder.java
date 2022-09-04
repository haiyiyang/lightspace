package com.haiyiyang.light.invocation.codec;

import com.haiyiyang.light.invocation.LightMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<LightMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, LightMessage msg, ByteBuf out) throws Exception {
		byte[] data = msg.encode();
		out.writeByte(msg.getType()).writeByte(msg.getSerializer()).writeInt(data.length).writeBytes(msg.encode());
	}
}
