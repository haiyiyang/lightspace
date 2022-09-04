package com.haiyiyang.light.invocation.client;

import org.tinylog.Logger;

import com.haiyiyang.light.invocation.response.LightResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class InboundHandler extends SimpleChannelInboundHandler<LightResponse> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LightResponse msg) throws Exception {
		LightClient.handleServerResponse(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Logger.error(cause);
		ctx.close();
	}

}
