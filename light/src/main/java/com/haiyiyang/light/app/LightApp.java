package com.haiyiyang.light.app;

import java.util.List;
import java.util.Set;

import com.haiyiyang.light.conf.AppConf;
import com.haiyiyang.light.conf.LightConf;
import com.haiyiyang.light.context.LightContext;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.service.LightService;
import com.haiyiyang.light.utils.NetworkUtils;

public class LightApp {

	private AppConf appConf;

	private LightConf lightConf;

	private LightContext lightContext;

	private static String APP_NAME;

	private static String LOCAL_IP;

	private static byte GROUP_0_1;

	private static volatile LightApp LIGHT_APP;

	private LightApp(LightAppParam p) {
		APP_NAME = p.getAppName();
		this.lightConf = LightConf.singleton();
		this.setMachineIPAndGroup01();
		this.lightContext = LightContext.buildContext(p);
		if (!lightContext.getObjectValues().isEmpty()) {
			if (APP_NAME == null) {
				APP_NAME = LightService.resolveServicePath(lightContext.getObjectValues().iterator().next());
			}
			LightService.publishLightService(lightContext.getObjectValues());
		}
		this.appConf = AppConf.singleton(APP_NAME);
	}

	public static LightApp buidLightApp(LightAppParam p) {
		if (LIGHT_APP != null) {
			return LIGHT_APP;
		}
		synchronized (AppConf.class) {
			if (LIGHT_APP == null) {
				LIGHT_APP = new LightApp(p);
			}
		}
		return LIGHT_APP;
	}

	private void setMachineIPAndGroup01() {
		Set<String> ips = NetworkUtils.getLocalIps();
		List<String> ipSegments = LightConf.getIpSegments();
		for (String ip : ips) {
			if (ipSegments == null || ipSegments.isEmpty()) {
				LOCAL_IP = ip;
				break;
			}
			for (String ipSegment : ipSegments) {
				if (ip.startsWith(ipSegment)) {
					LOCAL_IP = ip;
					break;
				}
			}
		}

		if (ipSegments != null && !ipSegments.isEmpty() && LOCAL_IP == null) {
			throw new LightException(LightException.Code.PERMISSION_ERROR, LightException.NO_NETWORK_PERMISSION);
		}
		GROUP_0_1 = Byte.parseByte(LOCAL_IP.substring(LOCAL_IP.length() - 1, LOCAL_IP.length()));
	}

	public AppConf getAppConf() {
		return appConf;
	}

	public LightConf getLightConf() {
		return lightConf;
	}

	public LightContext getLightContext() {
		return lightContext;
	}

	public static String getAppName() {
		return APP_NAME;
	}

	public static byte getGroup01() {
		return GROUP_0_1;
	}

	public static String getLocalIp() {
		return LOCAL_IP;
	}

	public static int getPort() {
		return 0;
	}

}
