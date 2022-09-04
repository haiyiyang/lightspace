package com.haiyiyang.light.conf.root;

import java.util.List;

import com.haiyiyang.light.__.U;
import com.haiyiyang.light.conf.attr.AppNode;
import com.haiyiyang.light.conf.attr.NettyConf;
import com.haiyiyang.light.conf.attr.ThreadPool;
import com.typesafe.config.Optional;

public class AppConfRoot {

	@Optional
	private Integer port = U.I6531;

	@Optional
	private List<AppNode> appNodes;

	@Optional
	private List<String> sharedConf;

	@Optional
	private ThreadPool serverThreadPool;

	@Optional
	private ThreadPool clientThreadPool;

	@Optional
	private NettyConf serverNettyConf;

	@Optional
	private NettyConf clientNettyConf;

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
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

	public ThreadPool getServerThreadPool() {
		return serverThreadPool;
	}

	public void setServerThreadPool(ThreadPool serverThreadPool) {
		this.serverThreadPool = serverThreadPool;
	}

	public ThreadPool getClientThreadPool() {
		return clientThreadPool;
	}

	public void setClientThreadPool(ThreadPool clientThreadPool) {
		this.clientThreadPool = clientThreadPool;
	}

	public NettyConf getServerNettyConf() {
		return serverNettyConf;
	}

	public void setServerNettyConf(NettyConf serverNettyConf) {
		this.serverNettyConf = serverNettyConf;
	}

	public NettyConf getClientNettyConf() {
		return clientNettyConf;
	}

	public void setClientNettyConf(NettyConf clientNettyConf) {
		this.clientNettyConf = clientNettyConf;
	}

}
