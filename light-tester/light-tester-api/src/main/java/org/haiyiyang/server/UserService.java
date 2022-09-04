package org.haiyiyang.server;

import java.util.List;

import org.haiyiyang.server.dto.Person;
import org.haiyiyang.server.dto.UserDto;

import com.haiyiyang.light.service.annotation.LightRemoteMethod;

public interface UserService {

	@LightRemoteMethod(methodId = -1)
	void ping();

	@LightRemoteMethod(methodId = -127)
	String hello(String name);

	@LightRemoteMethod(methodId = -126)
	String welcome(String firstName, String lastName);

	@LightRemoteMethod(methodId = -125)
	UserDto findUser(int userId);

	@LightRemoteMethod(methodId = -124)
	void saveUser(UserDto userDto);

	@LightRemoteMethod(methodId = -123)
	List<UserDto> findUserListByGender(boolean gender);

	@LightRemoteMethod(methodId = -122)
	String hello(Person person);

}
