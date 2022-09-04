package com.haiyiyang.light.conf.attr;

import com.haiyiyang.light.__.U;

public class ThreadPool {
	private int corePoolSize = U.i16;
	private int maximumPoolSize = U.i32;
	private long keepAliveTime = U.i16;
	private int workQueueSize = U.i127;
	public static final ThreadPool defaultThreadPool = new ThreadPool();

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	public long getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public int getWorkQueueSize() {
		return workQueueSize;
	}

	public void setWorkQueueSize(int workQueueSize) {
		this.workQueueSize = workQueueSize;
	}

}
