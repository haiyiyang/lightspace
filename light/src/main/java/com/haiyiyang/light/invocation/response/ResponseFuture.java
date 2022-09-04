package com.haiyiyang.light.invocation.response;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import org.tinylog.Logger;

import com.haiyiyang.light.__.E;
import com.haiyiyang.light.__.U;
import com.haiyiyang.light.invocation.request.LightRequest;

public class ResponseFuture implements Future<Object> {
	private Aqs aqs;
	private long startTime;
	private LightRequest lightRequest;
	private boolean returnVoid = false;
	private LightResponse lightResponse;
	private long responseTimeThreshold = 5000;

	public ResponseFuture(LightRequest lightRequest, boolean returnVoid) {
		this.aqs = new Aqs();
		this.lightRequest = lightRequest;
		this.returnVoid = returnVoid;
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public boolean isDone() {
		return aqs.isDone();
	}

	@Override
	public Object get() {
		if (returnVoid) {
			return null;
		}
		aqs.acquire(U.i1);
		if (this.lightResponse != null) {
			return this.lightResponse.getResult();
		} else {
			return null;
		}
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException {
		if (returnVoid) {
			return null;
		}
		boolean success = aqs.tryAcquireNanos(U.i1, unit.toNanos(timeout));
		if (success) {
			if (this.lightResponse != null) {
				return this.lightResponse.getResult();
			} else {
				return null;
			}
		} else {
			throw new E("Timeout messageId:" + String.valueOf(lightRequest.getMessageId()));
		}
	}

	@Override
	public boolean isCancelled() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		throw new UnsupportedOperationException();
	}

	public void set(LightResponse lightResponse) {
		this.lightResponse = lightResponse;
		aqs.release(1);
		long responseTime = System.currentTimeMillis() - startTime;
		if (responseTime > this.responseTimeThreshold) {
			Logger.warn(U.bs(64, "slow msg:", lightResponse.getMessageId(), "time cost:", responseTime));
		}
	}

	static class Aqs extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = 1L;

		protected boolean isDone() {
			return getState() == U.i1;
		}

		@Override
		protected boolean tryAcquire(int arg) {
			return getState() == U.i1;
		}

		@Override
		protected boolean tryRelease(int arg) {
			return getState() != U.i0 || compareAndSetState(U.i0, U.i1);
		}

	}
}
