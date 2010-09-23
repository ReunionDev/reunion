package com.googlecode.reunion.jreunion.events.session;

import java.net.Socket;

import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.Filter;
import com.googlecode.reunion.jreunion.events.InvalidEventException;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Session;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class SessionEvent extends Event {
	
	Session session;
	
	public SessionEvent(Session session) {
		this.session = session;
	}
	
	public Session getSession() {
		return session;
	}
	
	public static class SessionFilter implements Filter{
		
		Session session;
		public SessionFilter(Session session){
			this.session = session;			
		}
		
		@Override
		public boolean filter(Event event) {
			if(!(event instanceof SessionEvent)){
				throw new InvalidEventException(event,SessionEvent.class);
			}
			return ((SessionEvent)event).getSession().equals(this.session);			
		}
	}
}
