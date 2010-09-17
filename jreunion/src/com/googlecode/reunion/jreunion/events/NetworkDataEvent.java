package com.googlecode.reunion.jreunion.events;

import com.googlecode.reunion.jreunion.server.S_Client;

public class NetworkDataEvent extends NetworkEvent
{
	S_Client client;
	String data;
	
	public NetworkDataEvent(S_Client client, String data) {
		super(client);
		this.client = client;
		this.data = data;
	}

	public String getData() {
		return data;
	}
}
