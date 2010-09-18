package com.googlecode.reunion.jreunion.events;

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
