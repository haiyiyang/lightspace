package com.haiyiyang.light.__;

public class E extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public E(String message) {
		super(message);
	}

	public E(Throwable cause) {
		super(cause);
	}

	public E(String message, Throwable cause) {
		super(message, cause);
	}

}
