package com.haiyiyang.light.conf.subscribe;

public interface ConfSubscriber {

	public String registry();

	public String path();

	public void subscribe();

}
