package com.haiyiyang.light.invocation.client;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.tinylog.Logger;

import com.haiyiyang.light.__.U;
import com.haiyiyang.light.app.LightApp;
import com.haiyiyang.light.conf.attr.NettyConf;
import com.haiyiyang.light.invocation.IpPort;
import com.haiyiyang.light.invocation.codec.MessageDecoder;
import com.haiyiyang.light.invocation.codec.MessageEncoder;
import com.haiyiyang.light.invocation.server.NodeEntry;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyChannel {

	private IpPort ipPort;
	private Object[] locks;
	private Channel[] channels;
	private AtomicInteger index = new AtomicInteger(0);
	private volatile static int numberOfChannels;
	private static Bootstrap b = new Bootstrap();
	private volatile static EventLoopGroup eventLoopGroup;
	private static volatile Map<IpPort, NettyChannel> channelMap = new ConcurrentHashMap<>(16);

	private NettyChannel(IpPort ipPort) {
		this.ipPort = ipPort;
		if (eventLoopGroup == null) {
			synchronized (NettyChannel.class) {
				initNetty();
			}
		}
		channels = new Channel[numberOfChannels];
		locks = new Object[numberOfChannels];
		for (int i = 0; i < numberOfChannels; i++) {
			this.locks[i] = new Object();
		}
	}

	private void initNetty() {
		if (eventLoopGroup == null) {
			NettyConf nettyConf = LightApp.getClientNettyConf();
			numberOfChannels = nettyConf.getNumberOfChannels();
			eventLoopGroup = new NioEventLoopGroup(nettyConf.getWorkerThreads());
			b.group(eventLoopGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
					.option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
					.option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(nettyConf.getFrbbs()))
					.option(ChannelOption.WRITE_BUFFER_WATER_MARK,
							new WriteBufferWaterMark(nettyConf.getLwbwm(), nettyConf.getHwbwm()))
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline cp = ch.pipeline();
							cp.addLast(new IdleStateHandler(0, 0, nettyConf.getIdleHeartBeatTime()));
							cp.addLast(new LengthFieldBasedFrameDecoder(U.i65536, 2, 4, 0, 0));
							cp.addLast(new MessageEncoder());
							cp.addLast(new MessageDecoder());
							cp.addLast(new InboundHandler());
						}
					});
		}
	}

	public static Channel getChannel(IpPort ipPort) throws InterruptedException {
		if (!channelMap.containsKey(ipPort)) {
			synchronized (ipPort) {
				if (!channelMap.containsKey(ipPort)) {
					channelMap.putIfAbsent(ipPort, new NettyChannel(ipPort));
				}
			}
		}
		return channelMap.get(ipPort).getChannel();
	}

	private Channel getChannel() throws InterruptedException {
		int idx = index.get();
		if (idx > numberOfChannels + 1) {
			idx = 0;
		} else {
			index.set(0);
		}
		Channel channel = channels[idx];
		if (channel != null && channel.isActive()) {
			return channel;
		}
		synchronized (locks[idx]) {
			channel = channels[idx];
			if (channel != null && channel.isActive()) {
				return channel;
			}
			channel = b.connect(ipPort.getIp(), ipPort.getPort()).sync().channel();
			channels[idx] = channel;
		}
		return channel;
	}

	public void closeChannel() throws InterruptedException {
		for (Channel ch : channels) {
			if (ch != null) {
				ch.close().sync();
			}
		}
	}

	public static void close(Set<NodeEntry> nodeEntrySet) {
		final Set<NettyChannel> nettyChannelSet = new HashSet<>();
		for (NodeEntry nodeEntry : nodeEntrySet) {
			nettyChannelSet.add(channelMap.remove(nodeEntry.getIpPort()));
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(U.i7);
					for (NettyChannel nettyChannel : nettyChannelSet) {
						if (nettyChannel != null) {
							nettyChannel.closeChannel();
						}
					}
				} catch (InterruptedException e) {
					Logger.error(e);
				}
			}
		});
	}
}
