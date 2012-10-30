package org.reunionemu.jreunion.events.session;

import org.reunionemu.jreunion.events.Event;
import org.reunionemu.jreunion.events.Filter;
import org.reunionemu.jreunion.events.InvalidEventException;
import org.reunionemu.jreunion.server.Session;
/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
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
