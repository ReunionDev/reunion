package org.reunionemu.jreunion.events.network;

import java.nio.channels.SocketChannel;

import org.reunionemu.jreunion.events.Event;
import org.reunionemu.jreunion.events.Filter;
import org.reunionemu.jreunion.events.InvalidEventException;
/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class NetworkEvent extends Event {
	
	SocketChannel socketChannel;
	
	public NetworkEvent(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}
	
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}
	
	public static class NetworkFilter implements Filter{
		
		SocketChannel socketChannel;
		public NetworkFilter(SocketChannel socketChannel){
			this.socketChannel = socketChannel;
			
		}
		
		@Override
		public boolean filter(Event event) {
			if(!(event instanceof NetworkEvent)){
				throw new InvalidEventException(event, NetworkEvent.class);
			}
			return ((NetworkEvent)event).getSocketChannel().equals(this.socketChannel);			
		}
	}
}
