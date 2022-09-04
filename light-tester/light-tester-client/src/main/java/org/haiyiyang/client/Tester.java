package org.haiyiyang.client;

import com.haiyiyang.light.__.U;

public class Tester {

	public static void main(String[] args) {

		long ipPort = U.longIpPort("10.10.198.243", 65536);
		System.out.println(String.valueOf(ipPort).length());

		long currentTime = System.currentTimeMillis() * 1000;
		System.out.println(String.valueOf(currentTime).length());

	}

}
