package com.haiyiyang.light.conf;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.conf.root.AppConfRoot;
import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.resource.subscription.ResourceSubscriber;
import com.haiyiyang.light.resource.subscription.ResourceSubscription;
import com.haiyiyang.light.utils.LightUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;

public class AppConf implements ResourceSubscriber {

	private static final Logger LR = LoggerFactory.getLogger(AppConf.class);

	private static final String APP_CONF_PATH = "/light/app/";
	private static final String APP_CONF_LOCAL_PATH = LightUtil.getLocalPath(APP_CONF_PATH);

	private String appConfPath;
	private String appConfLocalPath;

	private LightConf lightConf;
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
		synchronized (AppConf.class) {
			if (APP_CONF == null) {
				APP_CONF = new AppConf(appName);
			}
		}
		return APP_CONF;
	}

	private void initialize(String appName) {
		StringBuilder strb = new StringBuilder(64);
		appConfPath = strb.append(APP_CONF_PATH).append(appName).append(LightConstants.DOT_CONF).toString();
		appConfLocalPath = strb.delete(0, strb.length()).append(APP_CONF_LOCAL_PATH).append(appName)
				.append(LightConstants.DOT_CONF).toString();
		if (LightUtil.useLocalConf()) {
			appConfRoot = ConfigBeanFactory.create(ConfigFactory.parseFile(new File(appConfLocalPath)),
					AppConfRoot.class);
		} else {
			doSubscribeAppConf();
		}
		this.lightConf = LightConf.singleton();
		SharedConf.subscribeShared(appConfRoot.getSharedConf());
	}

	private void doSubscribeAppConf() {
		byte[] data = ResourceSubscription.getSubscription(this).getData(appConfPath);
		if (data == null || data.length == 0) {
			LR.error("The file [{}] does not exists, or is empty.", appConfPath);
			throw new LightException(LightException.FILE_NOT_FOUND_OR_EMPTY);
		}
		appConfRoot = ConfigBeanFactory.create(ConfigFactory.parseString(new String(data, LightConstants.CHARSET_UTF8)),
				AppConfRoot.class);
	}

	public AppConfRoot getConf() {
		return appConfRoot;
	}

	public String getMatchedDomainPackage(String serviceName) {
		List<String> domainPackageList = lightConf.getDomainPackages();
		if (!domainPackageList.isEmpty()) {
			for (String domainPackage : domainPackageList) {
				if (serviceName.indexOf(domainPackage) == 0) {
					return domainPackage;
				}
			}
		}
		return null;
	}

	@Override
	public String getRegistry() {
		return LightConf.CONF_SERVER;
	}

	@Override
	public String getPath() {
		return appConfPath;
	}

	@Override
	public void subscribe() {
		doSubscribeAppConf();
		LR.info("Reloaded file [{}].", getPath());
	}

	public Config getSharedConf() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getDomainPackages() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDisableGrouping() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getServiceRegistry() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAppName() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getAppAddresses() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSerializerMode() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMachineIP() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getAppPort() {
		// TODO Auto-generated method stub
		return 0;
	}
}
