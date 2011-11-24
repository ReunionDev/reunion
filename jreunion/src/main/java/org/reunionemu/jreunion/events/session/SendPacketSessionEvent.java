package org.reunionemu.jreunion.events.session;

import org.reunionemu.jreunion.server.Session;

public class SendPacketSessionEvent extends SessionEvent{

	private String data;
	
	public SendPacketSessionEvent(Session session, String data) {
		super(session);
		this.data = data;
		
	}

	public String getData() {
		return data;
	}

}
