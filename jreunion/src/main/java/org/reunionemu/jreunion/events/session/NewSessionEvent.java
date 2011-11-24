package org.reunionemu.jreunion.events.session;

import org.reunionemu.jreunion.server.Session;

public class NewSessionEvent extends SessionEvent{

	public NewSessionEvent(Session session) {
		super(session);
		
	}

}
