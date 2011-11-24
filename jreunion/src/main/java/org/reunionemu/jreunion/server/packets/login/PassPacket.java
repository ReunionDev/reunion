package org.reunionemu.jreunion.server.packets.login;

import org.reunionemu.jreunion.server.packets.ForLoginServer;
import org.reunionemu.jreunion.server.packets.SessionPacket;

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
