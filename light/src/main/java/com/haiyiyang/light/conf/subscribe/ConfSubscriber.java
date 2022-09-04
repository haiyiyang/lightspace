package com.haiyiyang.light.conf.subscribe;

public interface ConfSubscriber {

	public String getRegistry();

	public String getPath();

	public void subscribe();

}
