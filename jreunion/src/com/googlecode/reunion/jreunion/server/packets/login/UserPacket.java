package com.googlecode.reunion.jreunion.server.packets.login;

import com.googlecode.reunion.jreunion.server.packets.ForLoginServer;
import com.googlecode.reunion.jreunion.server.packets.SessionPacket;

@ForLoginServer
public class UserPacket extends SessionPacket {

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
