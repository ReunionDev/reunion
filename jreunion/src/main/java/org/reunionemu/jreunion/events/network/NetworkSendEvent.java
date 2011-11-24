package org.reunionemu.jreunion.events.network;

import java.nio.channels.SocketChannel;

public class NetworkSendEvent extends NetworkEvent{

	public NetworkSendEvent(SocketChannel socketChannel) {
		super(socketChannel);
	}

}
