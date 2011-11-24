package org.reunionemu.jreunion.events.server;

import org.reunionemu.jreunion.server.Server;

public class ServerStopEvent extends ServerEvent {

	public ServerStopEvent(Server server) {
		super(server);
	}

}
