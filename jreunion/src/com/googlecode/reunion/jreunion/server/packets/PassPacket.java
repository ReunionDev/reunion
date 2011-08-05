package com.googlecode.reunion.jreunion.server.packets;


public class PassPacket extends Packet {

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
