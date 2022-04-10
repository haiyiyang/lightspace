package com.haiyiyang.light.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.context.LightContext;
import com.haiyiyang.light.rpc.server.LightRpcServer;
import com.haiyiyang.light.service.LightService;

public class ShutdownHook extends Thread {

	private static final Logger LR = LoggerFactory.getLogger(ShutdownHook.class);

	private LightContext lightContext;
	private static ShutdownHook SHUTDOWN_HOOK;

	private ShutdownHook(LightContext lightContext) {
		this.lightContext = lightContext;
		SHUTDOWN_HOOK = this;
	}

	public synchronized static void hook(LightContext lightContext) {
		if (SHUTDOWN_HOOK == null) {
			Runtime.getRuntime().addShutdownHook(new ShutdownHook(lightContext));
		}
	}

	public void run() {
		try {
			LR.info("The App start shutting down.");
			LightService.doUnpublishLightService();
			LR.info("THe thread sleeps 30 secondes.");
			Thread.sleep(30 * 1000);
			LightRpcServer.SINGLETON().stop();
			LR.info("The netty server has been shut down.");
		} catch (Throwable e) {
			LR.error("The Light App shut down failed, exception: {}", e.getMessage());
		} finally {
			if (lightContext != null) {
				lightContext.close();
				LR.info("The Light Context closed.");
			}
		}
	}
}
