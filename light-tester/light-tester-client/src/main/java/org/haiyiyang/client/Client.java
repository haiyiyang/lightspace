package org.haiyiyang.client;

import org.haiyiyang.server.UserService;
import org.haiyiyang.server.dto.UserDto;
import org.tinylog.Logger;

import com.haiyiyang.light.service.LightServiceProxy;

public class Client {

	public static void main(String[] args) {

		System.setProperty("enableLocalConf", "1");
		UserService userService = LightServiceProxy.getService(UserService.class);
		String helloResult = userService.hello("Agent");
		Logger.info("helloResult >>> " + helloResult);

		String welcomeResult = userService.welcome("Jack", "Ma");
		Logger.info("welcomeResult >>> " + welcomeResult);
		long start = System.currentTimeMillis();
		for (int i = 0; i < 2000; i++) {
			UserDto dto = new UserDto();
			dto.setAge((byte) 1);
			dto.setLastName("Liu");
			dto.setFirstName("Dehua");
			userService.saveUser(dto);
//			userService.findUserListByGender(false);
		}
		long end = System.currentTimeMillis();
		System.out.println("start:" + start);
		System.out.println("end:" + end);
		System.out.println("end - start" + (end - start));
	}
}