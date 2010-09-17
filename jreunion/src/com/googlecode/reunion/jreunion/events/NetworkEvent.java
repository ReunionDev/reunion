package com.googlecode.reunion.jreunion.events;

import com.googlecode.reunion.jreunion.server.S_Client;

public class NetworkEvent extends Event {
	
	S_Client client;
	
	public NetworkEvent(S_Client client) {
		this.client = client;
	}
	
	public S_Client getClient() {
		return client;
	}
	
}
