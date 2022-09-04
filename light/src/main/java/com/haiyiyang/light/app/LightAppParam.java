package com.haiyiyang.light.app;

public class LightAppParam {

	private String callerName;
	private String[] basePackages;
	private String[] componantClasses;
	private boolean serviceProvider = true;

	private LightAppParam(String callerName, boolean serviceProvider) {
		this.callerName = callerName;
		this.serviceProvider = serviceProvider;
	}

	private LightAppParam(String callerName, String[] basePackages, String[] componantClasses) {
		this.callerName = callerName;
		this.basePackages = basePackages;
		this.componantClasses = componantClasses;
	}

	public static LightAppParam buildClientAppParam() {
		String callerName = Thread.currentThread().getStackTrace()[3].getClassName();
		return new LightAppParam(callerName, false);
	}

	public static LightAppParam buildAppParam(String[] basePackages) {
		String callerName = Thread.currentThread().getStackTrace()[2].getClassName();
		return new LightAppParam(callerName, basePackages, null);
	}

	public static LightAppParam buildAppParam(String[] basePackages, String[] componantClasses) {
		String callerName = Thread.currentThread().getStackTrace()[2].getClassName();
		return new LightAppParam(callerName, basePackages, componantClasses);
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

	public String getCallerName() {
		return callerName;
	}

	public String[] getBasePackages() {
		return basePackages;
	}

	public boolean isServiceProvider() {
		return serviceProvider;
	}

}
