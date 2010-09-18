package com.googlecode.reunion.jreunion.events;
import com.googlecode.reunion.jreunion.server.*;
import com.googlecode.reunion.jreunion.events.Event;
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

}
