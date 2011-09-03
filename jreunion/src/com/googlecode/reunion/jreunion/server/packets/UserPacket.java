package com.googlecode.reunion.jreunion.server.packets;

public class UserPacket extends SessionPacket implements ForLoginServer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserPacket(String username){
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}

	String username;
}
