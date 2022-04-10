package com.haiyiyang.light.conf.root;

import java.util.List;

import com.haiyiyang.light.conf.attr.AppAddress;
import com.haiyiyang.light.conf.attr.AppRegistry;

public class LightConfRoot implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String domainPackages;

	private String registry;

	private boolean disableGrouping;

	private List<AppRegistry> appRegistry;

	private List<AppAddress> appAddress;

	private int timeout = 5;

	public String getDomainPackages() {
		return domainPackages;
	}

	public void setDomainPackages(String domainPackages) {
		this.domainPackages = domainPackages;
	}

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
	}

	public boolean isDisableGrouping() {
		return disableGrouping;
	}

	public void setDisableGrouping(boolean disableGrouping) {
		this.disableGrouping = disableGrouping;
	}

	public List<AppRegistry> getAppRegistry() {
		return appRegistry;
	}

	public void setAppRegistry(List<AppRegistry> appRegistry) {
		this.appRegistry = appRegistry;
	}

	public List<AppAddress> getAppAddress() {
		return appAddress;
	}

	public void setAppAddress(List<AppAddress> appAddress) {
		this.appAddress = appAddress;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
