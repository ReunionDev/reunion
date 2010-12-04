package com.googlecode.reunion.jreunion.events.network;

import java.net.Socket;

import com.googlecode.reunion.jreunion.server.Client;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class NetworkDataEvent extends NetworkEvent
{
	byte [] data;
	
	public NetworkDataEvent(Socket socket, byte[] data) {
		super(socket);
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}
}
