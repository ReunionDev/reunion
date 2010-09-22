package com.googlecode.reunion.jreunion.events;

import com.googlecode.reunion.jreunion.events.client.ClientEvent;
import com.googlecode.reunion.jreunion.server.Client;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class TestEvent extends Event {

	TestEvent() {
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
