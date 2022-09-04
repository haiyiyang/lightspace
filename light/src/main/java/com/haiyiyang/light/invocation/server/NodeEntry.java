package com.haiyiyang.light.invocation.server;

import java.util.Objects;

import com.haiyiyang.light.__.U;
import com.haiyiyang.light.invocation.IpPort;

public class NodeEntry {
	private IpPort ipPort;
	private byte group;
	private byte weight = U.b3;

	public String buildPath() {
		StringBuilder strb = new StringBuilder(27);
		return strb.append(ipPort.getIpPort()).append(U.COLON).append(group).append(U.COLON).append(weight).toString();
	}

	public NodeEntry(String nodePath) {
		int len = nodePath.length();
		this.weight = Byte.parseByte(nodePath.substring(len - 1));
		this.group = Byte.parseByte(nodePath.substring(len - 3, len - 2));
		this.ipPort = new IpPort(nodePath.substring(0, len - 4));
	}

	public NodeEntry(IpPort ipPort) {
		this.ipPort = ipPort;
	}

	public NodeEntry(IpPort ipPort, byte weight) {
		this.ipPort = ipPort;
		this.weight = weight;
	}

	public NodeEntry(IpPort ipPort, byte group, byte weight) {
		this.ipPort = ipPort;
		this.group = group;
		this.weight = weight;
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
		NodeEntry other = (NodeEntry) obj;
		return Objects.equals(ipPort, other.ipPort);
	}

	@Override
	public String toString() {
		return "NodeEntry [ipPort=" + ipPort + ", group=" + group + ", weight=" + weight + "]";
	}

	public IpPort getIpPort() {
		return ipPort;
	}

	public void setIpPort(IpPort ipPort) {
		this.ipPort = ipPort;
	}

	public byte getGroup() {
		return group;
	}

	public void setGroup(byte group) {
		this.group = group;
	}

	public byte getWeight() {
		return weight;
	}

	public void setWeight(byte weight) {
		this.weight = weight;
	}

}
