package org.haiyiyang.client;

import org.haiyiyang.server.UserService;
import org.haiyiyang.server.dto.Person;
import org.tinylog.Logger;

import com.haiyiyang.light.service.LightServiceProxy;

public class Client3 {

	static {
		System.setProperty("useLocalConf", "1");
	}
	private static final UserService userService = LightServiceProxy.getService(UserService.class);

	public static void main(String[] args) throws InterruptedException {
		Logger.error("===============000000000");
		for (int i = 0; i < 10; i++) {
			Person p = new Person();
			p.setFirstName("Jack 1111");
			p.setLastName("ma 222");
			userService.hello(p);
		}
		Logger.error("===============111111111");
		for (int i = 0; i < 7; i++) {
			Thread.sleep(500);
			test();
		}
	}

	private static void test() throws InterruptedException {

//		String helloResult = userService.hello("Agent");
//		Logger.info("helloResult >>> " + helloResult);

		int threadNum = 6;
		final int requestNum = 10000;
		Thread[] threads = new Thread[threadNum];

		long startTime = System.currentTimeMillis();
		// benchmark for sync call
		for (int idx = 0; idx < threadNum; ++idx) {
			threads[idx] = new Thread(new Runnable() {
				@Override
				public void run() {
//					String threadName = Thread.currentThread().getName();
					for (int i = 0; i < requestNum; i++) {
						try {
							Person p = new Person();
							p.setFirstName("Jack");
							p.setLastName("ma" + i);
							userService.hello(p);
//							Logger.error(userService.hello(p));
						} catch (Exception ex) {
//							Logger.error("!!!!!!!!!!!!!!!! ThreadName: " + threadName + ", i: " + i);
							Logger.error(ex.getMessage());
							Logger.error(ex.getCause());
						}
					}
				}
			}, "TN-" + idx);
			threads[idx].start();
		}
		for (int x = 0; x < threadNum; x++) {
			threads[x].join();
		}
		long timeCost = (System.currentTimeMillis() - startTime);
		String msg = String.format("Light >>> Sync call total-time-cost:%sms, req/s=%s", timeCost,
				((double) (requestNum * threadNum)) / timeCost * 1000);
		System.out.println(msg);
	}

}
