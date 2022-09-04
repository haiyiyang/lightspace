package com.haiyiyang.light.conf.attr;

import com.haiyiyang.light.__.U;

public class AppNode {
	private String ip;
	private byte loadWeight = U.b3;
	private boolean disablePublish;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public byte getLoadWeight() {
		return loadWeight;
	}

	public void setLoadWeight(byte loadWeight) {
		this.loadWeight = loadWeight;
	}

	public boolean isDisablePublish() {
		return disablePublish;
	}

	public void setDisablePublish(boolean disablePublish) {
		this.disablePublish = disablePublish;
	}

}
