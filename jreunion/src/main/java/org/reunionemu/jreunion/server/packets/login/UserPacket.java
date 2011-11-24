package org.reunionemu.jreunion.server.packets.login;

import org.reunionemu.jreunion.server.packets.ForLoginServer;
import org.reunionemu.jreunion.server.packets.SessionPacket;

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
