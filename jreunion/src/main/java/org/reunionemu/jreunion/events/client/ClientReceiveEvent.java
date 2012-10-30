package org.reunionemu.jreunion.events.client;

import org.reunionemu.jreunion.server.Client;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class ClientReceiveEvent extends ClientEvent {

	public ClientReceiveEvent(Client client) {
		super(client);
	}

}
