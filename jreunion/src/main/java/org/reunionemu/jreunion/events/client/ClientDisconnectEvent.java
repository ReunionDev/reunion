package org.reunionemu.jreunion.events.client;

import org.reunionemu.jreunion.server.Client;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ClientDisconnectEvent extends ClientEvent {

	public ClientDisconnectEvent(Client client) {
		super(client);
	}

}
