package com.googlecode.reunion.jreunion.server.packets.login;

import com.googlecode.reunion.jreunion.server.packets.ForLoginServer;
import com.googlecode.reunion.jreunion.server.packets.SessionPacket;

@ForLoginServer
public class PassPacket extends SessionPacket {

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
