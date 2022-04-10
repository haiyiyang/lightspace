package com.haiyiyang.light.rpc.server;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.app.LightApp;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.protocol.codec.ProtocolDecoder;
import com.haiyiyang.light.protocol.codec.ProtocolEncoder;
import com.haiyiyang.light.rpc.server.channel.ServerInboundHandler;
import com.haiyiyang.light.service.LightService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

public class LightRpcServer {

	private static final Logger LR = LoggerFactory.getLogger(LightRpcServer.class);

	private static LightRpcServer LIGHT_RPC_SERVER = new LightRpcServer();

	public static LightRpcServer SINGLETON() {
		return LIGHT_RPC_SERVER;
	}

	private EventLoopGroup bossGroup = null;
	private EventLoopGroup workerGroup = null;
	private ServerBootstrap serverBootstrap = null;

	public synchronized void start() {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		try {
			serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true).childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) {
							ch.pipeline().addLast("decoder", new ProtocolDecoder())
									.addLast("encoder", new ProtocolEncoder())
									.addLast(new DefaultEventExecutorGroup(10),
							// TODO
//											lightAppMeta.getLightConf().getServerThreadQuantity()),
											new ServerInboundHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			String serverIp = LightApp.getLocalIp();
			int serverPort = LightApp.getPort();
			ChannelFuture channelFuture = serverBootstrap.bind(serverIp, serverPort);
			channelFuture.awaitUninterruptibly(16, TimeUnit.SECONDS);
			if (channelFuture.isSuccess()) {
				LR.info("Server was completed successfully.");
			}
		} catch (Exception e) {
			LR.error(LightException.SERVER_STARTUP_FAILED);
			LightService.doUnpublishLightService();
			throw new LightException(LightException.SERVER_STARTUP_FAILED);
		}
	}

	public synchronized void stop() {
		if (workerGroup != null) {
			workerGroup.shutdownGracefully();
		}
		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
		}
	}

}
