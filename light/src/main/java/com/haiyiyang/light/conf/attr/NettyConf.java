package com.haiyiyang.light.conf.attr;

import com.haiyiyang.light.__.U;

public class NettyConf {

	private int bossThreads = U.i0;
	private int workerThreads = U.i0;

	private int soTimeout = U.i7;
	private int soBacklog = U.i127;

	// fixed Receive ByteBuf size
	private int frbbs = U.i1048576;
	// low water mark for write buffer
	private int lwbwm = U.i524288;
	// high water mark for write buffer
	private int hwbwm = U.i1048576;

	private int idleHeartBeatTime = U.i60;

	private int numberOfChannels = U.i16;

	public static final NettyConf defaultNettyConf = new NettyConf();

	public int getBossThreads() {
		return bossThreads;
	}

	public void setBossThreads(int bossThreads) {
		this.bossThreads = bossThreads;
	}

	public int getWorkerThreads() {
		return workerThreads;
	}

	public void setWorkerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public int getSoBacklog() {
		return soBacklog;
	}

	public void setSoBacklog(int soBacklog) {
		this.soBacklog = soBacklog;
	}

	public int getFrbbs() {
		return frbbs;
	}

	public void setFrbbs(int frbbs) {
		this.frbbs = frbbs;
	}

	public int getLwbwm() {
		return lwbwm;
	}

	public void setLwbwm(int lwbwm) {
		this.lwbwm = lwbwm;
	}

	public int getHwbwm() {
		return hwbwm;
	}

	public void setHwbwm(int hwbwm) {
		this.hwbwm = hwbwm;
	}

	public int getIdleHeartBeatTime() {
		return idleHeartBeatTime;
	}

	public void setIdleHeartBeatTime(int idleHeartBeatTime) {
		this.idleHeartBeatTime = idleHeartBeatTime;
	}

	public int getNumberOfChannels() {
		return numberOfChannels;
	}

	public void setNumberOfChannels(int numberOfChannels) {
		this.numberOfChannels = numberOfChannels;
	}

}
