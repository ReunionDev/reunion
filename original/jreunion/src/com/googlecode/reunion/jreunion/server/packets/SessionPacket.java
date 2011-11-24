package com.googlecode.reunion.jreunion.server.packets;

public class SessionPacket extends Packet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int sessionId;
	public int getSessionId() {
		return sessionId;
	}
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	public SessionPacket() {
		
	}
}
