package org.reunionemu.jreunion.events;

import org.reunionemu.jreunion.events.client.ClientEvent;
import org.reunionemu.jreunion.server.Client;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class TestEvent extends Event {

	public TestEvent() {
		super();
	}
	static class TestFilter implements Filter{
		
		Client client;
		public TestFilter(Client client){
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
