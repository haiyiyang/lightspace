package com.haiyiyang.light.service;

public class ServiceKey {
	private String serviceName;
	private boolean disableGrouping;

	public ServiceKey() {

	}

	public ServiceKey(String serviceName) {
		this.disableGrouping = false;
		this.serviceName = serviceName;
	}

	public ServiceKey(String serviceName, boolean disableGrouping) {

		this.serviceName = serviceName;
		this.disableGrouping = disableGrouping;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public boolean isDisableGrouping() {
		return disableGrouping;
	}

	public void setDisableGrouping(boolean disableGrouping) {
		this.disableGrouping = disableGrouping;
	}

}
