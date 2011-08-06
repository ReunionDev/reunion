package com.googlecode.reunion.jreunion.server.packets;


public class PassPacket extends SessionPacket implements ForLoginServer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PassPacket(String password){
		this.password = password;
	}
	
	String password;

	public String getPassword() {
		return password;
	}
	

}
