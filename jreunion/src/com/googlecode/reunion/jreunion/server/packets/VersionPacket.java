package com.googlecode.reunion.jreunion.server.packets;

public class VersionPacket extends Packet {
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
