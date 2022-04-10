package com.haiyiyang.light.app;

import org.springframework.context.support.AbstractApplicationContext;

public class LightAppParam {
	private String appName;
	private String[] basePackages;
	private String[] componantClasses;
	AbstractApplicationContext ctx;

	public LightAppParam() {
	}

	public LightAppParam(AbstractApplicationContext ctx) {
		this.ctx = ctx;
	}

	public LightAppParam(String[] basePackages, String[] componantClasses) {
		this.basePackages = basePackages;
		this.componantClasses = componantClasses;
	}

	public LightAppParam(AbstractApplicationContext ctx, String[] basePackages, String[] componantClasses) {
		this.ctx = ctx;
		this.basePackages = basePackages;
		this.componantClasses = componantClasses;
	}

	public LightAppParam(String appName, AbstractApplicationContext ctx, String[] basePackages,
			String[] componantClasses) {
		this.appName = appName;
		this.ctx = ctx;
		this.basePackages = basePackages;
		this.componantClasses = componantClasses;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String[] getBasePackages() {
		return basePackages;
	}

	public void setBasePackages(String[] basePackages) {
		this.basePackages = basePackages;
	}

	public AbstractApplicationContext getCtx() {
		return ctx;
	}

	public void setCtx(AbstractApplicationContext ctx) {
		this.ctx = ctx;
	}

	public void setComponantClasses(String[] componantClasses) {
		this.componantClasses = componantClasses;
	}

	public Class<?>[] getComponantClasses() throws ClassNotFoundException {
		if (componantClasses == null || componantClasses.length == 0) {
			return null;
		}
		Class<?>[] classes = new Class<?>[componantClasses.length];
		for (int i = 0; i < componantClasses.length; i++) {
			classes[i] = Class.forName(componantClasses[i]);
		}
		return classes;
	}

}
