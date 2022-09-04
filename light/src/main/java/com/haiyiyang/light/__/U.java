package com.haiyiyang.light.__;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.haiyiyang.light.app.LightApp;
import com.haiyiyang.light.conf.attr.ThreadPool;

public final class U {

	public static final String COLON = ":";
	public static final String COMMA = ",";
	public static final String DOT = ".";
	public static final String MINUS = "-";
	public static final String SEMICOLON = ";";
	public static final String SLASH = "/";

	public static final int i0 = 0;
	public static final int i1 = 1;
	public static final int i2 = 2;
	public static final int i3 = 3;
	public static final int i4 = 4;
	public static final int i5 = 5;
	public static final int i6 = 6;
	public static final int i7 = 7;
	public static final int i16 = 16;
	public static final int i32 = 32;
	public static final int i60 = 60;
	public static final int i127 = 127;
	public static final int i1024 = 1024;
	public static final int i6000 = 6000;
	public static final int i65536 = 65536;
	public static final int i100000 = 100000;
	public static final int i524288 = 512 * 1024;
	public static final int i1048576 = 1024 * 1024;
	public static final byte b3 = 3, bJ = 10, bL = 12, bP = 16, bR = 18;

	public static final char cSLASH = '/';
	public static final char cFS = File.separatorChar;

	public static final Integer I6531 = 6531;

	public static final String S1 = "1";
	public static final String S127001 = "127.0.0.1";
	public static final String S12700012181 = "127.0.0.1:2181";

	public static final String light = "light";
	public static final String dotConf = ".conf";
	public static final String backslashDot = "\\.";
	public static final String useLocalConf = "useLocalConf";
	public static final String userHome = System.getProperty("user.home");

	public static final String ZK_NO_DATA = "ZK No Data, Path: ";
	public static final String NO_NETWORK_PERMISSION = "No Network Permission.";

	public static final Charset utf8Charset = Charset.forName("UTF-8");
	public static final String[] specialIpPrefixs = { "127.", "255.", "169.254." };

	public static boolean useLocalConf() {
		return S1.equals(System.getProperty(useLocalConf));
	}

	public static String getLocalPath(String path) {
		return userHome + path.replace(cSLASH, cFS);
	}

	public static String bs(int len, Object... objs) {
		StringBuilder strb = new StringBuilder(len);
		for (Object obj : objs) {
			strb.append(obj);
		}
		return strb.toString();
	}

	public static ThreadPoolExecutor buildThreadPoolExecutor(ThreadPool threadPool, String threadNamePrefix) {
		new StringBuilder();
		return new ThreadPoolExecutor(threadPool.getCorePoolSize(), threadPool.getMaximumPoolSize(),
				threadPool.getKeepAliveTime(), TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(threadPool.getWorkQueueSize()), new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r, new StringBuilder(64).append(threadNamePrefix).append(MINUS)
								.append(LightApp.getAppName()).append(MINUS).append(r.hashCode()).toString());
					}
				}, new ThreadPoolExecutor.AbortPolicy());

	}

	public static String getLocalIp(List<String> ipSegments) {
		Set<InetAddress> inetAddressSet = getInetAddresses();
		if (ipSegments == null || ipSegments.isEmpty()) {
			return inetAddressSet.iterator().next().getHostAddress();
		}
		Iterator<InetAddress> it = inetAddressSet.iterator();
		String ip;
		while (it.hasNext()) {
			ip = it.next().getHostAddress();
			for (String ipSegment : ipSegments) {
				if (ip.startsWith(ipSegment)) {
					return ip;
				}
			}
		}
		throw new E(NO_NETWORK_PERMISSION);
	}

	private static Set<InetAddress> getInetAddresses() {
		Set<InetAddress> inetAddressSet = new HashSet<InetAddress>();
		Enumeration<NetworkInterface> ns = null;
		try {
			ns = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
		}
		while (ns != null && ns.hasMoreElements()) {
			NetworkInterface n = ns.nextElement();
			Enumeration<InetAddress> inetAddresses = n.getInetAddresses();
			while (inetAddresses.hasMoreElements()) {
				InetAddress inetAddress = inetAddresses.nextElement();
				if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
						&& !inetAddress.isMulticastAddress() && !isSpecialIp(inetAddress.getHostAddress()))
					inetAddressSet.add(inetAddress);
			}
		}
		return inetAddressSet;
	}

	private static boolean isSpecialIp(String ip) {
		if (ip.contains(COLON) || ip.startsWith(specialIpPrefixs[0]) || ip.startsWith(specialIpPrefixs[1])
				|| ip.startsWith(specialIpPrefixs[2])) {
			return true;
		}
		return false;
	}

	public static long longIpPort(String ip, int port) {
		String[] items = ip.split(backslashDot);
		return Long.valueOf(items[0]) << 40 | Long.valueOf(items[1]) << 32 | Long.valueOf(items[2]) << 24
				| Long.valueOf(items[3]) << 16 | Long.valueOf(port);
	}

	public static String stringIpPort(long longIpPort) {
		StringBuilder sb = new StringBuilder(24);
		for (int i = 0; i < 4; i++) {
			if (i > 0) {
				sb.append('.');
			}
			sb.append((longIpPort >> (8 * (5 - i))) & 0xff);
		}
		return sb.append(COLON).append(longIpPort & 0xffff).toString();
	}

}
