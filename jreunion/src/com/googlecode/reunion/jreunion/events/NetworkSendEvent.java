package com.googlecode.reunion.jreunion.events;

import java.net.Socket;

import com.googlecode.reunion.jreunion.server.Client;

public class NetworkSendEvent extends NetworkEvent{

	public NetworkSendEvent(Socket socket) {
		super(socket);
	}

}
