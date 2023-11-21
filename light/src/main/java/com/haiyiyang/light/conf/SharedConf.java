package com.haiyiyang.light.conf;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.tinylog.Logger;

import com.haiyiyang.light.__.E;
import com.haiyiyang.light.__.U;
import com.haiyiyang.light.app.LightApp;
import com.haiyiyang.light.conf.subscribe.ConfSubscriber;
import com.haiyiyang.light.conf.subscribe.ConfSubscription;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SharedConf implements ConfSubscriber {

	public static final String SHARED_PATH = "/light/shared/";
	public static final String SHARED_LOCAL_PATH = U.getLocalPath(SHARED_PATH);
	private static final Map<String, SharedConf> sharedConfMap = new ConcurrentHashMap<String, SharedConf>();

	private String path;
	private Config config;

	private SharedConf(String path) {
		this.path = path;
	}

	private void initialize() {
		if (LightApp.isEnableLocalConf()) {
			config = ConfigFactory.load(path);
		} else {
			doSubscribeSharedConf();
		}
	}

	public static void subscribeShared(List<String> sharedList) {
		sharedConfMap.clear();
		if (sharedList != null && !sharedList.isEmpty()) {
			StringBuilder fullPath = new StringBuilder(64);
			fullPath.append(LightApp.isEnableLocalConf() ? SHARED_LOCAL_PATH : SHARED_PATH);
			int len = fullPath.length();
			for (String shared : sharedList) {
				if (!sharedConfMap.containsKey(shared)) {
					SharedConf sharedConf = new SharedConf(
							fullPath.delete(len, fullPath.length()).append(shared).toString());
					sharedConfMap.put(sharedConf.path, sharedConf);
					sharedConf.initialize();
				}
			}
		}
	}

	private void doSubscribeSharedConf() {
		byte[] data = null;
		try {
			data = ConfSubscription.getSubscription(this).getData(this.path);
		} catch (Exception e) {
			Logger.error(e);
		}
		if (data == null || data.length == 0) {
			throw new E(U.ZK_NO_DATA + this.path);
		}
		config = ConfigFactory.parseString(new String(data, U.utf8Charset));
	}

	public Config getValue(String shared) {
		SharedConf sharedConf = sharedConfMap.get(shared);
		if (sharedConf != null) {
			return sharedConf.config;
		}
		return null;
	}

	@Override
	public String registry() {
		return LightConf.lightConfServer;
	}

	@Override
	public String path() {
		return path;
	}

	@Override
	public void subscribe() {
		doSubscribeSharedConf();
		Logger.info("Reloaded file {}.", path());
	}

	public Config getConfig() {
		return config;
	}

}
