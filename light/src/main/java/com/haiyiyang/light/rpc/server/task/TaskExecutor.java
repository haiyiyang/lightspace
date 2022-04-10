package com.haiyiyang.light.rpc.server.task;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskExecutor {

	private ThreadPoolExecutor threadPool;

	private static volatile TaskExecutor TASK_EXECUTOR;

	private TaskExecutor() {
		// TODO
		threadPool = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
				new ThreadPoolExecutor.AbortPolicy());
	}

	public static TaskExecutor singleton() {
		if (TASK_EXECUTOR != null) {
			return TASK_EXECUTOR;
		}
		synchronized (TaskExecutor.class) {
			if (TASK_EXECUTOR == null) {
				TASK_EXECUTOR = new TaskExecutor();
			}
		}
		return TASK_EXECUTOR;
	}

	public boolean execute(Runnable runnable) {
		boolean result = true;
		if (runnable != null) {
			try {
				threadPool.execute(runnable);
			} catch (RejectedExecutionException e) {
				result = false;
			}
		}
		return result;
	}
}
