package com.haiyiyang.light.invocation.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.Preconditions;
import com.haiyiyang.light.app.LightApp;
import com.haiyiyang.light.conf.LightConf;
import com.haiyiyang.light.invocation.IpPort;
import com.haiyiyang.light.service.LightService;
import com.haiyiyang.light.service.ServiceKey;

public class NodeSelector {

	private static final Map<String, NodeEntry> lastNodeEntry = new ConcurrentHashMap<>();

	private static LoadingCache<ServiceKey, List<NodeEntry>> cache = Caffeine.newBuilder().maximumSize(10000)
			.build(key -> getNodeEntryList(key));

	public static NodeEntry getNodeEntry(ServiceKey serviceKey) {
		if (LightApp.isEnableLocalConf()) {
			NodeEntry nodeEntry = getNodeInLightConf(serviceKey.getServiceName());
			if (nodeEntry != null) {
				return nodeEntry;
			}
		}
		List<NodeEntry> cacheList = cache.get(serviceKey);
		return getNextNodeEntry(serviceKey.getServiceName(), cacheList);
	}

	private static NodeEntry getNodeInLightConf(String serviceName) {
		String address = LightConf.getAppAddress(LightService.resolveServicePath(serviceName));
		return address == null ? null : new NodeEntry(new IpPort(address));
	}

	private static List<NodeEntry> getNodeEntryList(ServiceKey serviceKey) {
		Set<NodeEntry> list = LightService.subscribeLightService(serviceKey.getServiceName());
		if (list == null || list.isEmpty()) {
			return null;
		}
		List<NodeEntry> nodeEntryList = new ArrayList<>(list.size());
		byte group01 = LightConf.isDisableGrouping() ? -1 : LightApp.getGroup01();
		for (NodeEntry NodeEntry : list) {
			if (group01 == -1 || NodeEntry.getGroup() == group01) {
				nodeEntryList.add(NodeEntry);
			}
		}
		return nodeEntryList;
	}

	private static int GCD(int m, int n) {
		int r;
		while (m % n != 0) {
			r = n;
			n = m % n;
			m = r;
		}
		return n;
	}

	private static int GCD(List<NodeEntry> NodeEntry) {
		int r = 0, weight = 0;
		Iterator<NodeEntry> iter = NodeEntry.iterator();
		while (iter.hasNext()) {
			weight = iter.next().getWeight();
			if (weight != 0) {
				r = GCD(r, weight);
			}
		}
		return r;
	}

	private static byte maxWeight(List<NodeEntry> entryList) {
		byte maxWeight = 0;
		Iterator<NodeEntry> iter = entryList.iterator();
		while (iter.hasNext()) {
			byte curWeight = iter.next().getWeight();
			if (curWeight > maxWeight)
				maxWeight = curWeight;
		}
		return maxWeight;
	}

	private static NodeEntry getNextNodeEntry(String serviceName, List<NodeEntry> entrieList) {
		if (entrieList.size() == 1) {
			return entrieList.get(0);
		}
		NodeEntry lse = lastNodeEntry.get(serviceName);
		int index = -1;
		byte currentWeight = 0;
		if (lse != null) {
			index = entrieList.indexOf(lse);
			currentWeight = lse.getWeight();
		}
		int gcd = GCD(entrieList);
		byte maxWeight = maxWeight(entrieList);
		int size = entrieList.size();
		while (true) {
			index = (index + 1) % size;
			if (index == 0) {
				currentWeight -= gcd;
				if (currentWeight <= 0) {
					currentWeight = maxWeight;
					if (currentWeight == 0)
						return null;
				}
			}
			NodeEntry NodeEntry = entrieList.get(index);
			if (NodeEntry.getWeight() >= currentWeight) {
				lastNodeEntry.put(serviceName,
						new NodeEntry(NodeEntry.getIpPort(), NodeEntry.getGroup(), currentWeight));
				return NodeEntry;
			}
		}
	}

	public static void refreshCache(String key) {
		cache.refresh(new ServiceKey(key, LightConf.isDisableGrouping()));
	}
}
