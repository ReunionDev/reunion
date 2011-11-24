package org.reunionemu.jreunion.events.client;

import org.reunionemu.jreunion.events.Event;
import org.reunionemu.jreunion.events.Filter;
import org.reunionemu.jreunion.events.InvalidEventException;
import org.reunionemu.jreunion.server.Client;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ClientEvent extends Event {
	Client client;
	public ClientEvent(Client client){
		this.client = client;
	}
	
	public Client getClient() {
		return client;
	}
	
	public static class ClientFilter implements Filter{
		
		Client client;
		public ClientFilter(Client client){
			this.client = client;
			
		}
		
		@Override
		public boolean filter(Event event) {
			if(!(event instanceof ClientEvent)){
				throw new InvalidEventException(event,ClientEvent.class);
			}
			return ((ClientEvent)event).getClient()==this.client;			
		}
	}
}
