package com.haiyiyang.light.conf;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.haiyiyang.light.conf.root.LightConfRoot;
import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.resource.subscription.ResourceSubscriber;
import com.haiyiyang.light.resource.subscription.ResourceSubscription;
import com.haiyiyang.light.utils.LightUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;

public class LightConf implements ResourceSubscriber {

	private static final Logger LR = LoggerFactory.getLogger(LightConf.class);

	static final String CONF_SERVER = "lightConfServer:2181";
	private static final String LIGHT_CONF_URL = "/light/light.conf";
	private static final String LIGHT_CONF_LOCAL_URL = LightUtil.getLocalPath(LIGHT_CONF_URL);

	private static LightConfRoot LIGHT_CONF_ROOT;
	private static volatile LightConf LIGHT_CONF;

	private List<String> domainPackages;

	public final static byte timeout = 5;

	private LightConf() {
		initialize();
	}

	public static LightConf singleton() {
		if (LIGHT_CONF != null) {
			return LIGHT_CONF;
		}
		synchronized (LightConf.class) {
			if (LIGHT_CONF == null) {
				LIGHT_CONF = new LightConf();
			}
		}
		return LIGHT_CONF;
	}

	public static List<String> getDomainPackages() {
		return singleton().domainPackages;
	}

	public void setDomainPackages() {
		if (LIGHT_CONF_ROOT.getDomainPackages() == null || LIGHT_CONF_ROOT.getDomainPackages().isEmpty()) {
			this.domainPackages = Collections.emptyList();
		} else {
			this.domainPackages = Lists.newArrayList(LIGHT_CONF_ROOT.getDomainPackages().split(LightConstants.COMMA));
			this.domainPackages.sort((a, b) -> (a.length() > b.length()) ? -1 : 1);
		}
	}

	private void initialize() {
		if (LightUtil.useLocalConf()) {
			Config config = ConfigFactory.parseFile(new File(LIGHT_CONF_LOCAL_URL));
			LIGHT_CONF_ROOT = ConfigBeanFactory.create(config, LightConfRoot.class);
			setDomainPackages();
		} else {
			doSubscribeLightConf();
		}
	}

	private void doSubscribeLightConf() {
		byte[] data = ResourceSubscription.getSubscription(this).getData(LIGHT_CONF_URL);
		if (data == null || data.length == 0) {
			LR.error("The file [{}] does not exists, or is empty.", LIGHT_CONF_URL);
			throw new LightException(LightException.FILE_NOT_FOUND_OR_EMPTY);
		}
		LIGHT_CONF_ROOT = ConfigBeanFactory
				.create(ConfigFactory.parseString(new String(data, LightConstants.CHARSET_UTF8)), LightConfRoot.class);
		setDomainPackages();
	}

	public static boolean isDisableGrouping() {
		return LIGHT_CONF_ROOT.isDisableGrouping();
	}

	@Override
	public String getRegistry() {
		return CONF_SERVER;
	}

	@Override
	public String getPath() {
		return LIGHT_CONF_URL;
	}

	@Override
	public void subscribe() {
		doSubscribeLightConf();
		LR.info("Reloaded file [{}].", getPath());
	}

	public static List<String> getIpSegments() {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getServiceRegistry() {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getServiceRegistry(String appName) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getAppPort(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getSerializerMode() {
		// TODO Auto-generated method stub
		return null;
	}

	public static byte getServerLoadWeight(String serviceName, String localIp) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(String[] args) {
		System.setProperty("useLocalConf", "1");
		LightConf.singleton();
	}
}
