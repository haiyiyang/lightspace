package com.haiyiyang.light.invocation;

public class MethodReturn {

	public MethodReturn() {

	}

	public MethodReturn(String error) {
		this.error = error;
	}

	public MethodReturn(Object result) {
		this.result = result;
	}

	private String error;

	private Object result;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "MethodReturn [error=" + error + ", result=" + result + "]";
	}

}
