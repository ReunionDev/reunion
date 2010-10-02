package com.googlecode.reunion.jreunion.server;


public abstract class ParameterizedTimerTask extends java.util.TimerTask {
	private Object [] args;
	
	public Object[] getArgs() {
		return args;
	}
	public ParameterizedTimerTask(Object ... args){
		this.args = args;
		
	}

}
