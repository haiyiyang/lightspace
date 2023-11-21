package com.haiyiyang.light.app;

import com.haiyiyang.light.__.U;
import com.haiyiyang.light.conf.AppConf;
import com.haiyiyang.light.conf.LightConf;
import com.haiyiyang.light.conf.attr.NettyConf;
import com.haiyiyang.light.conf.attr.ThreadPool;
import com.haiyiyang.light.context.LightContext;
import com.haiyiyang.light.invocation.server.LightServer;
import com.haiyiyang.light.service.LightService;

public final class LightApp {
	private static String appName;
	private static String localIp;
	private static int localPort;
	private static String localIpPort;
	private static String serviceNode;

	private static byte group01;
	private static long longIpPort;

	private static AppConf appConf;
	private static LightConf lightConf;
	private static boolean enableLocalConf = false;

	public synchronized static void buidLightApp(LightAppParam p) {
		if (appName == null) {
			initializeConf(p);
			initializeApp(p);
			provideRemoteService(p);
		}
	}

	private static void initializeConf(LightAppParam p) {
		enableLocalConf = U.S1.equals(System.getProperty("enableLocalConf"));
		lightConf = LightConf.singleton();
		appConf = AppConf.singleton(appName = LightService.resolveServicePath(p.getCallerName()));
	}

	private static void initializeApp(LightAppParam p) {
		localIp = U.getLocalIp(lightConf.getIpSegments());
		localPort = appConf.getAppPort();
		localIpPort = U.bs(23, localIp, U.COLON, localPort);
		longIpPort = U.longIpPort(localIp, localPort);
		group01 = Byte.parseByte(localIp.substring(localIp.length() - 1, localIp.length()));
		serviceNode = U.bs(27, localIpPort, U.COLON, group01, U.COLON, appConf.getNodeWeight(localIp));
	}

	private static void provideRemoteService(LightAppParam p) {
		if (p.isServiceProvider()) {
			if (!appConf.isDisablePublish(localIp)) {
				LightService.publishLightService(serviceNode, LightContext.buildContext(p).getObjectValues());
			}
			LightServer.singleton().start();
		}
	}

	public static NettyConf getServerNettyConf() {
		if (appConf.getServerNettyConf() != null) {
			return appConf.getServerNettyConf();
		} else if (lightConf.getServerNettyConf() != null) {
			return lightConf.getServerNettyConf();
		}
		return NettyConf.defaultNettyConf;
	}

	public static NettyConf getClientNettyConf() {
		if (appConf.getClientNettyConf() != null) {
			return appConf.getClientNettyConf();
		} else if (lightConf.getClientNettyConf() != null) {
			return lightConf.getClientNettyConf();
		}
		return NettyConf.defaultNettyConf;
	}

	public static ThreadPool getClientThreadPool() {
		if (appConf.getClientThreadPool() != null) {
			return appConf.getClientThreadPool();
		} else if (lightConf.getClientThreadPool() != null) {
			return lightConf.getClientThreadPool();
		}
		return ThreadPool.defaultThreadPool;
	}

	public static ThreadPool getServerThreadPool() {
		if (appConf.getServerThreadPool() != null) {
			return appConf.getServerThreadPool();
		} else if (lightConf.getServerThreadPool() != null) {
			return lightConf.getServerThreadPool();
		}
		return ThreadPool.defaultThreadPool;
	}

	public static String getAppName() {
		return appName;
	}

	public static AppConf getAppConf() {
		return appConf;
	}

	public static LightConf getLightConf() {
		return lightConf;
	}

	public static String getLocalIp() {
		return localIp;
	}

	public static int getLocalPort() {
		return localPort;
	}

	public static byte getGroup01() {
		return group01;
	}

	public static long getLongIpPort() {
		return longIpPort;
	}

	public static String getServiceNode() {
		return serviceNode;
	}

	public static boolean isEnableLocalConf() {
		return enableLocalConf;
	}

}
