package com.googlecode.reunion.jreunion.events.session;

import com.googlecode.reunion.jreunion.server.Session;

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
