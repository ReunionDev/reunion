package com.googlecode.reunion.jreunion.events.server;

import com.googlecode.reunion.jreunion.server.Server;

public class ServerStopEvent extends ServerEvent {

	public ServerStopEvent(Server server) {
		super(server);
	}

}
