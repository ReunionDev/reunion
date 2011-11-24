package org.reunionemu.jreunion.events.server;

import org.reunionemu.jreunion.server.Server;

public class ServerStartEvent extends ServerEvent {

	public ServerStartEvent(Server server) {
		super(server);
		
	}
}
