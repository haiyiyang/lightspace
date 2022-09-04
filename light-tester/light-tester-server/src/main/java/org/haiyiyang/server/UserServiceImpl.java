package org.haiyiyang.server;

import java.util.ArrayList;
import java.util.List;

import org.haiyiyang.server.dto.Person;
import org.haiyiyang.server.dto.UserDto;

import com.haiyiyang.light.service.annotation.LightRemoteService;

@LightRemoteService
public class UserServiceImpl implements UserService {

	public String hello(String name) {
		return "hello " + name;
	}

	public String welcome(String firstName, String lastName) {
		String abc = "123";
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		abc.substring(1, 2);
		return "welcome >>> " + firstName + " " + lastName;
	}

	public UserDto findUser(int userId) {
		return new UserDto(userId);
	}

	public void saveUser(UserDto userDto) {
//		Logger.info(userDto.getLastName());
	}

	public List<UserDto> findUserListByGender(boolean gender) {
		List<UserDto> list = new ArrayList<UserDto>();
		for (int i = 0; i < 3; i++) {
			list.add(new UserDto(i, "dehua", "liu", false, (byte) 2));
		}
		return list;
	}

	@Override
	public void ping() {

	}

	@Override
	public String hello(Person person) {
		return "Hello " + person.getFirstName() + " " + person.getLastName();
	}

}
