package com.haiyiyang.light.invocation;

import java.util.Objects;

import com.haiyiyang.light.__.U;

public class IpPort {
	private String ip;
	private int port;
	private String ipPort;

	public IpPort(String ip, int port) {
		this.ip = ip;
		this.port = port;
		ipPort = new StringBuilder(23).append(ip).append(U.COLON).append(port).toString();
	}

	public IpPort(String ipPort) {
		this.ipPort = ipPort;
		int colonIdx = ipPort.indexOf(U.COLON);
		this.ip = ipPort.substring(0, colonIdx);
		this.port = Integer.parseInt(ipPort.substring(colonIdx + 1));
	}

	public String getIpPort() {
		return this.ipPort;
	}

	@Override
	public int hashCode() {
		return Objects.hash(ipPort);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IpPort other = (IpPort) obj;
		return Objects.equals(ipPort, other.ipPort);
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setIpPort(String ipPort) {
		this.ipPort = ipPort;
	}

}
