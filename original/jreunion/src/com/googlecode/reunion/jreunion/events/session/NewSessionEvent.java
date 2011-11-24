package com.googlecode.reunion.jreunion.events.session;

import com.googlecode.reunion.jreunion.server.Session;

public class NewSessionEvent extends SessionEvent{

	public NewSessionEvent(Session session) {
		super(session);
		
	}

}
