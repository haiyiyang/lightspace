package com.haiyiyang.light.context;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.haiyiyang.light.app.LightApp;

public class LightTheadContext {

	private Long sourceId;
	private Long requestId;

	private static final int i1000 = 1000;

	private static final AtomicInteger atomicMessageId = new AtomicInteger(0);
	private static final AtomicLong atomicRequestId = new AtomicLong(System.currentTimeMillis() * i1000);

	private LightTheadContext() {
	}

	private static ThreadLocal<LightTheadContext> threadLocal = new ThreadLocal<LightTheadContext>() {
		@Override
		protected LightTheadContext initialValue() {
			return new LightTheadContext();
		}
	};

	public static LightTheadContext get() {
		return threadLocal.get();
	}

	public static int getMessageId() {
		return atomicMessageId.incrementAndGet();
	}

	public Long getSourceId() {
		if (sourceId == null) {
			this.sourceId = LightApp.getLongIpPort();
		}
		return sourceId;
	}

	public Long getRequestId() {
		if (requestId == null) {
			this.requestId = atomicRequestId.incrementAndGet();
			if (this.requestId < 0) {
				atomicRequestId.set(System.currentTimeMillis() * i1000);
				this.requestId = atomicRequestId.get();
			}
		}
		return requestId;
	}

}
