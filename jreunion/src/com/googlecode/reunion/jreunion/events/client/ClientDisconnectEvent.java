package com.googlecode.reunion.jreunion.events.client;

import com.googlecode.reunion.jreunion.server.Client;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ClientDisconnectEvent extends ClientEvent {

	public ClientDisconnectEvent(Client client) {
		super(client);
	}

}
