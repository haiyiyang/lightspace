package com.haiyiyang.light.conf;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.tinylog.Logger;

import com.haiyiyang.light.__.E;
import com.haiyiyang.light.__.U;
import com.haiyiyang.light.conf.attr.AppNode;
import com.haiyiyang.light.conf.attr.NettyConf;
import com.haiyiyang.light.conf.attr.ThreadPool;
import com.haiyiyang.light.conf.root.AppConfRoot;
import com.haiyiyang.light.conf.subscribe.ConfSubscriber;
import com.haiyiyang.light.conf.subscribe.ConfSubscription;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;

public class AppConf implements ConfSubscriber {

	private static final String APP_CONF_PATH = "/light/app/";
	private static final String APP_CONF_LOCAL_PATH = U.getLocalPath(APP_CONF_PATH);
	private static final Map<String, AppNode> appNodeMap = new ConcurrentHashMap<String, AppNode>();

	private String appConfPath;
	private String appConfLocalPath;

	private SharedConf sharedConf;

	private AppConfRoot appConfRoot;
	private static volatile AppConf APP_CONF;

	private AppConf(String appName) {
		initialize(appName);
	}

	public static AppConf singleton(String appName) {
		if (APP_CONF != null) {
			return APP_CONF;
		}
		if (appName == null) {
			throw new E("No appName");
		}
		synchronized (AppConf.class) {
			if (APP_CONF == null) {
				APP_CONF = new AppConf(appName);
			}
		}
		return APP_CONF;
	}

	private void initialize(String appName) {
		StringBuilder strb = new StringBuilder(64);
		appConfPath = strb.append(APP_CONF_PATH).append(appName).append(U.dotConf).toString();
		appConfLocalPath = strb.delete(0, strb.length()).append(APP_CONF_LOCAL_PATH).append(appName).append(U.dotConf)
				.toString();
		if (U.useLocalConf()) {
			setAppConfRoot(
					ConfigBeanFactory.create(ConfigFactory.parseFile(new File(appConfLocalPath)), AppConfRoot.class));
		} else {
			doSubscribeAppConf();
		}
		SharedConf.subscribeShared(appConfRoot.getSharedConf());
	}

	private void doSubscribeAppConf() {
		byte[] data = null;
		try {
			data = ConfSubscription.getSubscription(this).getData(appConfPath);
		} catch (Exception e) {
			Logger.error(e);
		}
		if (data == null || data.length == 0) {
			throw new E(U.ZK_NO_DATA + appConfPath);
		}
		setAppConfRoot(ConfigBeanFactory.create(ConfigFactory.parseString(new String(data, U.utf8Charset)),
				AppConfRoot.class));
	}

	private void setAppConfRoot(AppConfRoot appConfRoot) {
		this.appConfRoot = appConfRoot;
		initializeAppNodes();
	}

	private void initializeAppNodes() {
		appNodeMap.clear();
		List<AppNode> appNodeList = appConfRoot.getAppNodes();
		if (appNodeList != null && !appNodeList.isEmpty()) {
			for (AppNode appNode : appNodeList) {
				if (appNode != null) {
					appNodeMap.put(appNode.getIp(), appNode);
				}
			}
		}
	}

	@Override
	public String getRegistry() {
		return LightConf.lightConfServer;
	}

	@Override
	public String getPath() {
		return appConfPath;
	}

	@Override
	public void subscribe() {
		doSubscribeAppConf();
		Logger.info("Reloaded file {}.", getPath());
	}

	public Config getSharedConf() {
		return sharedConf.getConfig();
	}

	public boolean isDisablePublish(String ip) {
		return appNodeMap != null && appNodeMap.containsKey(ip) && appNodeMap.get(ip).isDisablePublish();
	}

	public byte getNodeWeight(String ip) {
		AppNode appNode = appNodeMap.get(ip);
		return appNode == null ? U.b3 : appNode.getLoadWeight();
	}

	public int getAppPort() {
		return appConfRoot.getPort();
	}

	public NettyConf getServerNettyConf() {
		return appConfRoot.getServerNettyConf();
	}

	public NettyConf getClientNettyConf() {
		return appConfRoot.getClientNettyConf();
	}

	public ThreadPool getServerThreadPool() {
		return appConfRoot.getServerThreadPool();
	}

	public ThreadPool getClientThreadPool() {
		return appConfRoot.getClientThreadPool();
	}
}
