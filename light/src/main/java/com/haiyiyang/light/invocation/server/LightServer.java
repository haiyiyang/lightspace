package com.haiyiyang.light.invocation.server;

import org.tinylog.Logger;

import com.haiyiyang.light.__.E;
import com.haiyiyang.light.app.LightApp;
import com.haiyiyang.light.conf.attr.NettyConf;
import com.haiyiyang.light.invocation.server.channel.ServerChannelInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class LightServer {

	private static Thread thread;
	private static final LightServer lightServer = new LightServer();

	public static LightServer singleton() {
		return lightServer;
	}

	private EventLoopGroup bossGroup = null;
	private EventLoopGroup workerGroup = null;

	public synchronized void start() {
		NettyConf nettyConf = LightApp.getServerNettyConf();
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				bossGroup = new NioEventLoopGroup(nettyConf.getBossThreads());
				workerGroup = new NioEventLoopGroup(nettyConf.getWorkerThreads());
				try {
					ServerBootstrap serverBootstrap = new ServerBootstrap();
					serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
							.option(ChannelOption.SO_BACKLOG, nettyConf.getSoBacklog())
//							.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,
//									new WriteBufferWaterMark(nettyConf.getLwbwm(), nettyConf.getHwbwm()))
//							.childOption(ChannelOption.RCVBUF_ALLOCATOR,
//									new FixedRecvByteBufAllocator(nettyConf.getFrbbs()))
							.childHandler(new ServerChannelInitializer()).childOption(ChannelOption.SO_KEEPALIVE, true);
					ChannelFuture cf = serverBootstrap.bind(LightApp.getLocalIp(), LightApp.getLocalPort()).sync();
					cf.channel().closeFuture().sync();
				} catch (Exception e) {
					throw new E(e);
				} finally {
					LightServer.stop();
				}
			}
		});
		thread.start();
	}

	public static synchronized void stop() {
		try {
			if (lightServer.workerGroup != null) {
				lightServer.workerGroup.shutdownGracefully();
			}
			if (lightServer.bossGroup != null) {
				lightServer.bossGroup.shutdownGracefully();
			}
			if (thread != null && thread.isAlive()) {
				thread.interrupt();
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
			if (thread != null && thread.isAlive()) {
				thread.interrupt();
			}
		}
	}

}
