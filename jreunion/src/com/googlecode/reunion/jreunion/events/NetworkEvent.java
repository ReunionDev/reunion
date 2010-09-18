package com.googlecode.reunion.jreunion.events;

import java.net.Socket;

import com.googlecode.reunion.jreunion.server.Client;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class NetworkEvent extends Event {
	
	Socket socket;
	
	protected NetworkEvent(Socket socket) {
		this.socket = socket;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
}
