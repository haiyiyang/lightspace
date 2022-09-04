package org.haiyiyang.server;

import com.haiyiyang.light.app.LightApp;
import com.haiyiyang.light.app.LightAppParam;

public class Server {

	public static void main(String[] args) {
		System.setProperty("useLocalConf", "1");
//		System.setProperty("zookeeper.sasl.client", "false");
		LightApp.buidLightApp(LightAppParam.buildAppParam(new String[] { "org.haiyiyang" }));
	}
}
