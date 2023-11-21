package com.haiyiyang.light.conf;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.tinylog.Logger;

import com.google.common.collect.Lists;
import com.haiyiyang.light.__.E;
import com.haiyiyang.light.__.U;
import com.haiyiyang.light.app.LightApp;
import com.haiyiyang.light.conf.attr.AppAddress;
import com.haiyiyang.light.conf.attr.NettyConf;
import com.haiyiyang.light.conf.attr.ThreadPool;
import com.haiyiyang.light.conf.root.LightConfRoot;
import com.haiyiyang.light.conf.subscribe.ConfSubscriber;
import com.haiyiyang.light.conf.subscribe.ConfSubscription;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;

public class LightConf implements ConfSubscriber {

	private static volatile LightConf LIGHT_CONF;

	static final String lightConfServer = "conf.light:2181";

	private static final String LIGHT_CONF_URL = "/light/light.conf";
	private static final String LIGHT_CONF_LOCAL_URL = U.getLocalPath(LIGHT_CONF_URL);

	private static final Map<String, String> appAddressMap = new ConcurrentHashMap<String, String>();

	private static List<String> domainPackages;
	private static LightConfRoot lightConfRoot;

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
		return domainPackages;
	}

	public void initializeDomainPackages() {
		if (lightConfRoot.getDomainPackages() == null || lightConfRoot.getDomainPackages().isEmpty()) {
			domainPackages = Collections.emptyList();
		} else {
			domainPackages = Lists.newArrayList(lightConfRoot.getDomainPackages().split(U.COMMA));
			domainPackages.sort((a, b) -> (a.length() > b.length()) ? -1 : 1);
		}
	}

	private void initialize() {
		if (LightApp.isEnableLocalConf()) {
			setLightConfRoot(ConfigBeanFactory.create(ConfigFactory.parseFile(new File(LIGHT_CONF_LOCAL_URL)),
					LightConfRoot.class));
		} else {
			doSubscribeLightConf();
		}
	}

	private void doSubscribeLightConf() {
		byte[] data = null;
		try {
			data = ConfSubscription.getSubscription(this).getData(LIGHT_CONF_URL);
		} catch (Exception e) {
			Logger.error(e);
		}
		if (data == null || data.length == 0) {
			throw new E(U.ZK_NO_DATA + LIGHT_CONF_URL);
		}
		setLightConfRoot(ConfigBeanFactory.create(ConfigFactory.parseString(new String(data, U.utf8Charset)),
				LightConfRoot.class));

	}

	private void setLightConfRoot(LightConfRoot lightConfRoot) {
		LightConf.lightConfRoot = lightConfRoot;
		initializeDomainPackages();
		initializeAppAddress();
	}

	private void initializeAppAddress() {
		appAddressMap.clear();
		List<AppAddress> appAddressList = lightConfRoot.getAppAddress();
		if (appAddressList != null && !appAddressList.isEmpty()) {
			for (AppAddress appAddress : appAddressList) {
				if (appAddress != null) {
					appAddressMap.put(appAddress.getAppName(), appAddress.getAddress());
				}
			}
		}
	}

	@Override
	public String registry() {
		return lightConfServer;
	}

	@Override
	public String path() {
		return LIGHT_CONF_URL;
	}

	@Override
	public void subscribe() {
		doSubscribeLightConf();
		Logger.info("Reloaded file {}.", path());
	}

	public List<String> getIpSegments() {
		if (lightConfRoot.getIpSegments() == null) {
			return Collections.emptyList();
		}
		return Lists.newArrayList(lightConfRoot.getIpSegments().split(U.COMMA));
	}

	public static boolean isDisableGrouping() {
		return lightConfRoot.isDisableGrouping();
	}

	public NettyConf getServerNettyConf() {
		return lightConfRoot.getServerNettyConf();
	}

	public NettyConf getClientNettyConf() {
		return lightConfRoot.getClientNettyConf();
	}

	public ThreadPool getServerThreadPool() {
		return lightConfRoot.getServerThreadPool();
	}

	public ThreadPool getClientThreadPool() {
		return lightConfRoot.getClientThreadPool();
	}

	public static String getRegistry() {
		return lightConfRoot.getRegistry();
	}

	public static byte getSerializer() {
		return (byte) lightConfRoot.getSerializer();
	}

	public static String getAppAddress(String appName) {
		return appAddressMap.get(appName);
	}

}
