package com.googlecode.reunion.jreunion.events;

import com.googlecode.reunion.jreunion.server.Client;

public class NetworkDisconnectEvent extends NetworkEvent{

	public NetworkDisconnectEvent(Client client) {
		super(client);
	}

}
