package com.haiyiyang.light.service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.tinylog.Logger;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.haiyiyang.light.__.E;
import com.haiyiyang.light.__.U;
import com.haiyiyang.light.app.LightApp;
import com.haiyiyang.light.conf.LightConf;
import com.haiyiyang.light.invocation.MethodHandler;
import com.haiyiyang.light.invocation.client.NettyChannel;
import com.haiyiyang.light.invocation.server.NodeEntry;
import com.haiyiyang.light.invocation.server.NodeSelector;
import com.haiyiyang.light.registry.RegistryClient;
import com.haiyiyang.light.service.annotation.LightRemoteMethod;

public final class LightService {

	private static final String LIGHT_SERVICE_PATH = "/light/services/";

	private static final Map<String, Object> beanMap = new HashMap<>(8);
	private static final Map<String, String> servicePathMap = new HashMap<>(8);

	private static final Set<String> psnSet = new HashSet<>(8);
	private static final Map<String, CopyOnWriteArraySet<NodeEntry>> ssnMap = new ConcurrentHashMap<>();

	private static final Table<String, Byte, Method> serviceMethodTable = HashBasedTable.create(8, 32);

	private static volatile String serviceNode;
	private static volatile RegistryClient registryClient;

	private synchronized static void initRegistryClient() {
		if (registryClient == null) {
			serviceNode = LightApp.getServiceNode();
			registryClient = new RegistryClient(LightConf.getAppRegistry());
		}
	}

	public static Object getServiceProxy(Class<?> clazz) {
		if (beanMap.containsKey(clazz.getName())) {
			return beanMap.get(clazz.getName());
		}
		return MethodHandler.getProxyService(clazz);
	}

	public static Object getLocalBean(String serviceName) {
		return beanMap.get(serviceName);
	}

	public static String resolveServicePath(String serviceName) {
		if (servicePathMap.containsKey(serviceName)) {
			return servicePathMap.get(serviceName);
		}
		List<String> domainPackageList = LightConf.getDomainPackages();
		if (domainPackageList == null || domainPackageList.isEmpty()) {
			servicePathMap.put(serviceName, serviceName);
			return serviceName;
		}
		for (String domainPackage : domainPackageList) {
			if (serviceName.indexOf(domainPackage) == 0) {
				int index = serviceName.indexOf(U.DOT, domainPackage.length() + 1);
				servicePathMap.put(serviceName,
						serviceName.substring(domainPackage.length() + 1, index == -1 ? serviceName.length() : index));
				return servicePathMap.get(serviceName);
			}
		}
		servicePathMap.put(serviceName, serviceName);
		return serviceName;
	}

	private static String buildNodePath(String serviceName) {
		return U.bs(U.i127, LIGHT_SERVICE_PATH, serviceName, U.SLASH, serviceNode);
	}

	public static void publishLightService(String serviceNode, Collection<Object> objects) {
		if (objects == null || objects.isEmpty()) {
			return;
		}
		if (registryClient == null) {
			initRegistryClient();
		}
		Set<String> servicePathSet = new HashSet<>(objects.size());
		for (Object object : objects) {
			Class<?> interfaceClass = getInterfaceName(object);
			String className = interfaceClass.getName();
			String simpleClassName = interfaceClass.getSimpleName();
			beanMap.put(className, object);
			beanMap.put(simpleClassName, object);
			buildMethodCache(interfaceClass, object);
			servicePathSet.add(resolveServicePath(className));
		}
		for (String serviceName : servicePathSet) {
			psnSet.add(buildNodePath(serviceName));
		}
		doPublishLightService();
	}

	public static void doPublishLightService() {
		try {
			for (String serviceNode : psnSet) {
				registryClient.createPath(serviceNode, null);
			}
			Logger.info("Published Light services.");
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	public static void doUnpublishLightService() {
		try {
			for (String serviceNode : psnSet) {
				registryClient.deletePath(serviceNode);
			}
			Logger.info("Unpublished Light services.");
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	public static CopyOnWriteArraySet<NodeEntry> subscribeLightService(String serviceName) {
		String servicePath = resolveServicePath(serviceName);
		if (ssnMap.containsKey(servicePath)) {
			return ssnMap.get(servicePath);
		}
		String serviceFullPath = U.bs(U.i127, LIGHT_SERVICE_PATH, servicePath);
		try {
			if (registryClient == null) {
				initRegistryClient();
			}
			registryClient.watchPathChildrenNode(serviceFullPath, newListener());
			ssnMap.put(servicePath, getNodeEntrySet(serviceFullPath));
		} catch (Exception e) {
			Logger.error(e);
		}
		return ssnMap.get(servicePath);
	}

	private static CopyOnWriteArraySet<NodeEntry> getNodeEntrySet(String appName) throws Exception {
		List<String> children = registryClient.getChildren(appName);
		CopyOnWriteArraySet<NodeEntry> set = new CopyOnWriteArraySet<>();
		if (children != null && !children.isEmpty()) {
			for (String nodePath : children) {
				set.add(new NodeEntry(nodePath));
			}
		}
		return set;
	}

	private static CuratorCacheListener newListener() {
		return new CuratorCacheListener() {
			@Override
			public void event(Type type, ChildData childData, ChildData childData1) {
				Logger.info(type);
				try {
					Logger.info(childData);
					if (childData != null && childData.getPath() != null) {
						updateService(childData.getPath());
					}
				} catch (Exception e) {
					Logger.error(e);
				}
			}
		};
	}

	private static void updateService(String path) throws Exception {
		String serverPath = path.substring(U.i16, path.lastIndexOf("/"));
		Set<NodeEntry> csnSet = getNodeEntrySet(serverPath);
		Set<NodeEntry> ssnSet = ssnMap.get(serverPath);
		Set<NodeEntry> middleSet = new HashSet<NodeEntry>(ssnSet);
		middleSet.removeAll(csnSet);
		if (!middleSet.isEmpty()) {
			ssnSet.removeAll(middleSet);
			NettyChannel.close(middleSet);
		}
		middleSet.clear();
		middleSet.addAll(csnSet);
		middleSet.removeAll(ssnSet);
		ssnSet.addAll(middleSet);
		middleSet.clear();
		NodeSelector.refreshCache(serverPath);
	}

	public static Class<?> getInterfaceName(Object serviceImpl) {
		Class<?>[] classes = serviceImpl.getClass().getInterfaces();
		if (classes == null || classes.length == 0) {
			Logger.error("The service Class {} must implements an interface.", serviceImpl.getClass().getName());
			throw new E("As a service Class must implements an interface.");
		}
		String sampleName = serviceImpl.getClass().getSimpleName();
		for (Class<?> clazz : classes) {
			if (sampleName.indexOf(clazz.getSimpleName()) == 0) {
				return clazz;
			}
		}
		Logger.error("Class {} name must be prefixed with its interface simple name.",
				serviceImpl.getClass().getName());
		throw new E("Class name must be prefixed with its interface simple name.");
	}

	private static void buildMethodCache(Class<?> clazz, Object object) {
		Method[] interfaceMethods = clazz.getDeclaredMethods();
		LightRemoteMethod annotation = null;
		try {
			for (Method method : interfaceMethods) {
				annotation = method.getAnnotation(LightRemoteMethod.class);
				if (annotation != null) {
					Method m = object.getClass().getMethod(method.getName(), method.getParameterTypes());
					serviceMethodTable.put(clazz.getName(), annotation.methodId(), m);
				}
			}
		} catch (NoSuchMethodException e) {
			throw new E(e.getMessage());
		} catch (SecurityException e) {
			throw new E(e.getMessage());
		}
	}

	public static Method getMethod(String serviceName, Byte methodId) {
		return serviceMethodTable.get(serviceName, methodId);
	}

	public static void main(String args[]) {
		String a = LIGHT_SERVICE_PATH + "prod/192.168.1.2:8080:1:2";
		System.out.println(a.substring(U.i16, a.lastIndexOf("/")));
	}

}
