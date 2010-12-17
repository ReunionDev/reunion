package com.googlecode.reunion.jreunion.events.network;

import java.net.Socket;
import java.nio.channels.SocketChannel;

import com.googlecode.reunion.jreunion.server.Client;

public class NetworkDisconnectEvent extends NetworkEvent{

	public NetworkDisconnectEvent(SocketChannel socketChannel) {
		super(socketChannel);
	}

}
