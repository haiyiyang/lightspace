package com.haiyiyang.light.conf.root;

import java.util.List;

import com.haiyiyang.light.conf.attr.AppNode;

public class AppConfRoot {
	private int port;
	private int timeout;
	private List<AppNode> appNodes;
	private List<String> sharedConf;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public List<AppNode> getAppNodes() {
		return appNodes;
	}

	public void setAppNodes(List<AppNode> appNodes) {
		this.appNodes = appNodes;
	}

	public List<String> getSharedConf() {
		return sharedConf;
	}

	public void setSharedConf(List<String> sharedConf) {
		this.sharedConf = sharedConf;
	}

}
