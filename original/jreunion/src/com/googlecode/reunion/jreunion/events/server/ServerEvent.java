package com.googlecode.reunion.jreunion.events.server;

import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.server.Server;

public class ServerEvent extends Event {

	Server server;
	public Server getServer() {
		return server;
	}
	public ServerEvent(Server server) {
		this.server =server;
	}

}
