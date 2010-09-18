package com.googlecode.reunion.jreunion.events;

import com.googlecode.reunion.jreunion.server.Client;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class NetworkDataEvent extends NetworkEvent
{
	Client client;
	String data;
	
	protected NetworkDataEvent(Client client, String data) {
		super(client);
		this.client = client;
		this.data = data;
	}

	public String getData() {
		return data;
	}
}
