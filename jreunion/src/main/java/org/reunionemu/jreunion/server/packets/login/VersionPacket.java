package org.reunionemu.jreunion.server.packets.login;

import org.reunionemu.jreunion.server.packets.ForLoginServer;
import org.reunionemu.jreunion.server.packets.SessionPacket;

@ForLoginServer
public class VersionPacket extends SessionPacket {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VersionPacket(short version){
		this.version = version;
	}
	
	private short version;

	public short getVersion() {
		return version;
	}

}
