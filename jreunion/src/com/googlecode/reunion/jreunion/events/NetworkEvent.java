package com.googlecode.reunion.jreunion.events;

import com.googlecode.reunion.jreunion.server.Client;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class NetworkEvent extends Event {
	
	Client client;
	
	public NetworkEvent(Client client) {
		this.client = client;
	}
	
	public Client getClient() {
		return client;
	}
	
}
