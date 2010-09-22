package com.googlecode.reunion.jreunion.events.network;

import java.net.Socket;

import com.googlecode.reunion.jreunion.server.Client;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class NetworkDataEvent extends NetworkEvent
{
	String data;
	
	public NetworkDataEvent(Socket socket, String data) {
		super(socket);
		this.data = data;
	}

	public String getData() {
		return data;
	}
}
