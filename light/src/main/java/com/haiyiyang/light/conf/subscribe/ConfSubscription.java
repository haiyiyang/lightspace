package com.haiyiyang.light.conf.subscribe;

import java.util.Map;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.tinylog.Logger;

import com.google.common.collect.Maps;
import com.haiyiyang.light.registry.RegistryClient;

public class ConfSubscription {

	private RegistryClient registryClient;
	private ConfSubscriber lightSubscriber;
	private static final Map<ConfSubscriber, ConfSubscription> subscriptions = Maps.newConcurrentMap();

	private ConfSubscription(ConfSubscriber subscriber) {
		this.lightSubscriber = subscriber;
		registryClient = new RegistryClient(subscriber.getRegistry());
		try {
			registryClient.watchPathChildrenNode(subscriber.getPath(), new CuratorCacheListener() {
				@Override
				public void event(Type type, ChildData childData, ChildData childData1) {
					Logger.info(type);
					try {
						Logger.info(childData);
						if (childData != null && childData.getPath() != null) {
							lightSubscriber.subscribe();
						}
					} catch (Exception e) {
						Logger.error(e);
					}
				}
			});
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	public static ConfSubscription getSubscription(ConfSubscriber subscriber) {
		if (subscriptions.containsKey(subscriber)) {
			return subscriptions.get(subscriber);
		}
		synchronized (subscriber) {
			if (!subscriptions.containsKey(subscriber)) {
				subscriptions.put(subscriber, new ConfSubscription(subscriber));
			}
		}
		return subscriptions.get(subscriber);
	}

	public byte[] getData(String path) throws Exception {
		return registryClient.getData(path);
	}

}
