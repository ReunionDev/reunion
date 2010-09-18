package com.googlecode.reunion.jreunion.events;

import com.googlecode.reunion.jreunion.server.Client;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ClientReceiveEvent extends ClientEvent {

	public ClientReceiveEvent(Client client) {
		super(client);
	}

}
