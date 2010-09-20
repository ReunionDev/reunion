package com.googlecode.reunion.jreunion.events;

import java.net.Socket;

import com.googlecode.reunion.jreunion.server.Client;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class NetworkEvent extends Event {
	
	Socket socket;
	
	protected NetworkEvent(Socket socket) {
		this.socket = socket;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public static class NetworkFilter implements Filter{
		
		Socket socket;
		public NetworkFilter(Socket socket){
			this.socket = socket;
			
		}
		
		@Override
		public boolean filter(Event event) {
			if(!(event instanceof NetworkEvent)){
				throw new InvalidEventException(event,NetworkEvent.class);
			}
			return ((NetworkEvent)event).getSocket().equals(this.socket);			
		}
	}
	
}
