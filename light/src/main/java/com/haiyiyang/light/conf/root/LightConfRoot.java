package com.haiyiyang.light.conf.root;

import java.util.List;

import com.haiyiyang.light.__.U;
import com.haiyiyang.light.conf.attr.AppAddress;
import com.haiyiyang.light.conf.attr.NettyConf;
import com.haiyiyang.light.conf.attr.ThreadPool;
import com.typesafe.config.Optional;

public class LightConfRoot {

	@Optional
	private String domainPackages;

	@Optional
	private String registry = U.S12700012181;

	@Optional
	private int timeout = U.i7;

	@Optional
	private String ipSegments;

	@Optional
	private boolean disableGrouping;

	@Optional
	private int serializer = U.i16;

	@Optional
	private List<AppAddress> appAddress;

	@Optional
	private ThreadPool serverThreadPool;

	@Optional
	private ThreadPool clientThreadPool;

	@Optional
	private NettyConf serverNettyConf;

	@Optional
	private NettyConf clientNettyConf;

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

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getIpSegments() {
		return ipSegments;
	}

	public void setIpSegments(String ipSegments) {
		this.ipSegments = ipSegments;
	}

	public boolean isDisableGrouping() {
		return disableGrouping;
	}

	public void setDisableGrouping(boolean disableGrouping) {
		this.disableGrouping = disableGrouping;
	}

	public int getSerializer() {
		return serializer;
	}

	public void setSerializer(int serializer) {
		this.serializer = serializer;
	}

	public List<AppAddress> getAppAddress() {
		return appAddress;
	}

	public void setAppAddress(List<AppAddress> appAddress) {
		this.appAddress = appAddress;
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
