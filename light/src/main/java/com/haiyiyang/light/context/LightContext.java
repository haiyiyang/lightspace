package com.haiyiyang.light.context;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.tinylog.Logger;

import com.haiyiyang.light.__.E;
import com.haiyiyang.light.app.LightAppParam;
import com.haiyiyang.light.invocation.server.LightServer;
import com.haiyiyang.light.service.LightService;
import com.haiyiyang.light.service.annotation.LightRemoteService;

public class LightContext extends AnnotationConfigApplicationContext implements ApplicationContextAware {
	private static Map<String, Object> objectMap;

	private static volatile LightContext lightContext;

	private static ApplicationContext applicationContext;

	private LightContext(LightAppParam cp) {
		lookupLightService(cp);
	}

	public static LightContext buildContext(LightAppParam cp) {
		if (lightContext != null) {
			return lightContext;
		}
		synchronized (LightContext.class) {
			if (lightContext == null) {
				lightContext = new LightContext(cp);
			}
		}
		return lightContext;
	}

	private void lookupLightService(LightAppParam cp) {
		try {
			if (applicationContext != null) {
				objectMap = applicationContext.getBeansWithAnnotation(LightRemoteService.class);
			} else {
				this.refresh();
				if (cp.getBasePackages() != null && cp.getBasePackages().length > 0) {
					this.scan(cp.getBasePackages());
				}
				Class<?>[] classes = cp.getComponantClasses();
				if (classes != null && classes.length > 0) {
					this.register(classes);
				}
				objectMap = this.getBeansWithAnnotation(LightRemoteService.class);
			}
		} catch (Exception e) {
			throw new E(e);
		}
	}

	public Collection<Object> getObjectValues() {
		if (objectMap == null) {
			return Collections.emptyList();
		}
		return objectMap.values();
	}

	@Override
	protected void onClose() {
		try {
			LightService.doUnpublishLightService();
			Logger.info("THe thread sleeps 5 secondes.");
			Thread.sleep(5 * 1000);
			LightServer.stop();
			Logger.info("The netty server has been shut down.");
		} catch (Throwable e) {
			Logger.error("The Light App shut down failed, exception: {}", e.getMessage());
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		LightContext.applicationContext = applicationContext;
	}

}
