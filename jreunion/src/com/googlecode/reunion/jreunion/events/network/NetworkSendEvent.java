package com.googlecode.reunion.jreunion.events.network;

import java.net.Socket;
import java.nio.channels.SocketChannel;

import com.googlecode.reunion.jreunion.server.Client;

public class NetworkSendEvent extends NetworkEvent{

	public NetworkSendEvent(SocketChannel socketChannel) {
		super(socketChannel);
	}

}
