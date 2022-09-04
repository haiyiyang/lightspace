package com.haiyiyang.light.invocation.server.channel;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.LockSupport;

import org.tinylog.Logger;

import com.haiyiyang.light.__.E;
import com.haiyiyang.light.invocation.request.LightRequest;
import com.haiyiyang.light.invocation.response.LightResponse;
import com.haiyiyang.light.service.LightService;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class InboundHandler extends SimpleChannelInboundHandler<LightRequest> {

	private static ThreadPoolExecutor threadPoolExecutor;
	private static final Object[] emptyArgs = new Object[] {};

	public InboundHandler(ThreadPoolExecutor threadPoolExecutor) {
		InboundHandler.threadPoolExecutor = threadPoolExecutor;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, LightRequest req) throws Exception {
		threadPoolExecutor.submit(new Runnable() {
			@Override
			public void run() {
				LightResponse response = new LightResponse(req.getMessageId(), req.getSerializer());
				try {
					Object result = handle(req);
					response.setResult(result);
				} catch (Throwable t) {
					response.setError(t.toString());
					Logger.error("RPC Server handle request error", t);
				}
				if (ctx.channel().isActive()) {
					while (!ctx.channel().isWritable()) {
						Logger.warn("ctx.channel().isWritable() is false");
						LockSupport.parkNanos(1);
					}
					ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
						@Override
						public void operationComplete(ChannelFuture cf) throws Exception {
							if (!cf.isSuccess()) {
								Logger.error(cf.cause().getMessage());
							}
						}
					});
				} else {
					Logger.error("ctx.channel().isActive() is false.");
				}
			}
		});
	}

	private Object handle(LightRequest req) throws Throwable {
		Logger.info("receive:{}", req.id());
		Object service = LightService.getLocalBean(req.getServiceName());
		if (service == null) {
			throw new E("No Such Service, " + req.getServiceName());
		}
		Method method = LightService.getMethod(req.getServiceName(), req.getMethodId());
		if (method == null) {
			throw new E("No Such Method, " + req.getMethodId());
		}

		if (req.getMethodArgs() == null) {
			return method.invoke(service, emptyArgs);
		} else {
			return method.invoke(service, req.getMethodArgs());
		}
	}

}
