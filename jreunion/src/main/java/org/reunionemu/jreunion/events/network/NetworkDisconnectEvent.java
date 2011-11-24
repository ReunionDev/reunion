package org.reunionemu.jreunion.events.network;

import java.nio.channels.SocketChannel;

public class NetworkDisconnectEvent extends NetworkEvent{

	public NetworkDisconnectEvent(SocketChannel socketChannel) {
		super(socketChannel);
	}

}
