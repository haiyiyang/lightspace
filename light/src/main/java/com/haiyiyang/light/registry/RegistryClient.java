package com.haiyiyang.light.registry;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import com.haiyiyang.light.__.U;

public class RegistryClient {

	private CuratorFramework cf;
	private static final ExponentialBackoffRetry ebr = new ExponentialBackoffRetry(1000, 10);

	public RegistryClient(String c) {
		cf = CuratorFrameworkFactory.builder().connectString(c).connectionTimeoutMs(U.i6000).sessionTimeoutMs(U.i6000)
				.retryPolicy(ebr).build();
		cf.start();
	}

	public String createPath(String path, byte[] data) throws Exception {
		return cf.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, data);
	}

	public void addConnectionStateListener(ConnectionStateListener connectionStateListener) {
		cf.getConnectionStateListenable().addListener(connectionStateListener);
	}

	public void deletePath(String path) throws Exception {
		cf.delete().guaranteed().forPath(path);
	}

	public List<String> getChildren(String path) throws Exception {
		return cf.getChildren().forPath(path);
	}

	public byte[] getData(String path) throws Exception {
		return cf.getData().forPath(path);
	}

	public void watchPathChildrenNode(String path, CuratorCacheListener listener) throws Exception {
		CuratorCache curatorCache = CuratorCache.build(cf, path);
		curatorCache.listenable().addListener(listener);
		curatorCache.start();
	}

	public void close() {
		cf.close();
	}
}
