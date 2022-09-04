package com.haiyiyang.light.invocation;

import java.io.Serializable;

public interface LightMessage extends Serializable {

	public byte getType();

	public byte[] encode();

	public byte getSerializer();

}
