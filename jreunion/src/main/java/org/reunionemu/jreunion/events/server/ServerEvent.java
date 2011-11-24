package org.reunionemu.jreunion.events.server;

import org.reunionemu.jreunion.events.Event;
import org.reunionemu.jreunion.server.Server;

public class ServerEvent extends Event {

	Server server;
	public Server getServer() {
		return server;
	}
	public ServerEvent(Server server) {
		this.server =server;
	}

}
