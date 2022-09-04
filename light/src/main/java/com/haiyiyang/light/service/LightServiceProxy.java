package com.haiyiyang.light.service;

import java.util.concurrent.atomic.AtomicBoolean;

import com.haiyiyang.light.__.E;
import com.haiyiyang.light.app.LightApp;
import com.haiyiyang.light.app.LightAppParam;

public class LightServiceProxy {

	private static final AtomicBoolean ab = new AtomicBoolean(false);

	@SuppressWarnings("unchecked")
	public static <T> T getService(Class<T> clazz) throws E {
		if (!ab.get() && ab.compareAndSet(false, true)) {
			LightApp.buidLightApp(LightAppParam.buildClientAppParam());
		}
		return (T) LightService.getServiceProxy(clazz);
	}

}
