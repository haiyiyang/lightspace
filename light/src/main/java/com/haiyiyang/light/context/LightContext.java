package com.haiyiyang.light.context;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.haiyiyang.light.app.LightAppParam;
import com.haiyiyang.light.app.ShutdownHook;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.service.LightService;
import com.haiyiyang.light.service.annotation.LightRemoteService;

public class LightContext extends AnnotationConfigApplicationContext {
	private static Map<String, Object> OBJECT_MAP;

	private static volatile LightContext LIGHT_CONTEXT;

	private LightContext(LightAppParam cp) {
		lookupLightService(cp);
	}

	public static LightContext buildContext(LightAppParam cp) {
		if (LIGHT_CONTEXT != null) {
			return LIGHT_CONTEXT;
		}
		synchronized (LightContext.class) {
			if (LIGHT_CONTEXT == null) {
				LIGHT_CONTEXT = new LightContext(cp);
			}
		}
		return LIGHT_CONTEXT;
	}

	private void lookupLightService(LightAppParam cp) {
		try {
			if (cp.getCtx() != null) {
				this.refresh();
				OBJECT_MAP = cp.getCtx().getBeansWithAnnotation(LightRemoteService.class);
			} else {
				if (cp.getBasePackages() != null && cp.getBasePackages().length > 0) {
					this.scan(cp.getBasePackages());
				}
				Class<?>[] classes = cp.getComponantClasses();
				if (classes != null && classes.length > 0) {
					this.register(classes);
				}
				OBJECT_MAP = this.getBeansWithAnnotation(LightRemoteService.class);
			}
		} catch (Exception e) {
			throw new LightException(e);
		}
	}

	public Collection<Object> getObjectValues() {
		if (OBJECT_MAP == null) {
			return Collections.emptyList();
		}
		return OBJECT_MAP.values();
	}

	@Override
	protected void onClose() {
		LightService.doUnpublishLightService();
	}

	@Override
	public void registerShutdownHook() {
		ShutdownHook.hook(this);
	}

}
