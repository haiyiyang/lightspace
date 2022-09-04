package com.haiyiyang.light.invocation.server.channel;

import com.haiyiyang.light.__.U;
import com.haiyiyang.light.app.LightApp;
import com.haiyiyang.light.conf.attr.ThreadPool;
import com.haiyiyang.light.invocation.codec.MessageDecoder;
import com.haiyiyang.light.invocation.codec.MessageEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
	private static InboundHandler serverInboundHandler;
	

	public ServerChannelInitializer() {
		ThreadPool threadPool = LightApp.getServerThreadPool();
		serverInboundHandler = new InboundHandler(U.buildThreadPoolExecutor(threadPool, "LS"));
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline cp = ch.pipeline();
		cp.addLast(new IdleStateHandler(0, 0, LightApp.getServerNettyConf().getIdleHeartBeatTime()));
		cp.addLast(new LengthFieldBasedFrameDecoder(U.i65536, 2, 4, 0, 0));
		cp.addLast(new MessageDecoder());
		cp.addLast(new MessageEncoder());
		cp.addLast(serverInboundHandler);
	}
}
