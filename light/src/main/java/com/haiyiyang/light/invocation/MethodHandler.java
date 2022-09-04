package com.haiyiyang.light.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cglib.proxy.MethodProxy;
import org.tinylog.Logger;

import com.google.common.base.Preconditions;
import com.haiyiyang.light.__.E;
import com.haiyiyang.light.app.LightApp;
import com.haiyiyang.light.conf.LightConf;
import com.haiyiyang.light.context.LightTheadContext;
import com.haiyiyang.light.invocation.client.LightClient;
import com.haiyiyang.light.invocation.request.LightRequest;
import com.haiyiyang.light.invocation.response.ResponseFuture;
import com.haiyiyang.light.invocation.server.NodeEntry;
import com.haiyiyang.light.invocation.server.NodeSelector;
import com.haiyiyang.light.service.ServiceKey;
import com.haiyiyang.light.service.annotation.LightRemoteMethod;

public class MethodHandler implements InvocationHandler {

	private static final Map<Class<?>, MethodHandler> methodHandler = new ConcurrentHashMap<>();

	private Class<?> clazz;
	private Object objectProxy;
	private ServiceKey serviceKey;

	private static final String TO_STRING = "toString";

	private MethodHandler(Class<?> clazz) {
		this.clazz = clazz;
		serviceKey = new ServiceKey(clazz.getName(), LightConf.isDisableGrouping());
		this.objectProxy = Proxy.newProxyInstance(MethodHandler.class.getClassLoader(), new Class[] { clazz }, this);
	}

	public static Object getProxyService(Class<?> clazz) {
		if (!methodHandler.containsKey(clazz)) {
			methodHandler.putIfAbsent(clazz, new MethodHandler(clazz));
		}
		return methodHandler.get(clazz).objectProxy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return doInvoke(proxy, method, args, null);
	}

	private Object doInvoke(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		if (TO_STRING.equals(method.getName())) {
			return proxy.getClass().getName();
		}
		LightRemoteMethod lrm = method.getAnnotation(LightRemoteMethod.class);
		Preconditions.checkNotNull(lrm, "Not @LightRemoteMethod");

		serviceKey.setDisableGrouping(LightConf.isDisableGrouping());
		LightRequest req = buildLightRequest(lrm.methodId(), lrm.serializer(), args);

		int retryCount = 0;
		NodeEntry nodeEntry;
		while (retryCount < 3) {
			try {
				nodeEntry = NodeSelector.getNodeEntry(serviceKey);
				Preconditions.checkNotNull(nodeEntry, "No NodeEntry");
				ResponseFuture response = LightClient.sendRequest(nodeEntry.getIpPort(), req,
						method.getReturnType() == Void.class);
				return response.get();
			} catch (Exception e) {
				retryCount++;
				Logger.error(e);
			}
		}
		throw new E("Getting Channel failed");
	}

	private LightRequest buildLightRequest(byte methodId, byte serializer, Object[] args) {
		LightRequest lightRequest = new LightRequest();
		lightRequest.setSourceId(LightTheadContext.get().getSourceId());
		lightRequest.setRequestId(LightTheadContext.get().getRequestId());
		lightRequest.setCurrentTime(System.currentTimeMillis());
		lightRequest.setClientId(LightApp.getLongIpPort());
		lightRequest.setMessageId(LightTheadContext.getMessageId());
		lightRequest.setMethodId(methodId);
		lightRequest.setSerializer(serializer != -1 ? serializer : LightConf.getSerializer());
		lightRequest.setServiceName(clazz.getName());
		if (args != null && args.length > 0) {
			lightRequest.setMethodArgs(args);
		}
		return lightRequest;
	}

}
