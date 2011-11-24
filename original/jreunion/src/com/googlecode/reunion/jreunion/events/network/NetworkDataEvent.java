package com.googlecode.reunion.jreunion.events.network;

import java.nio.channels.SocketChannel;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class NetworkDataEvent extends NetworkEvent
{
	byte [] data;
	
	public NetworkDataEvent(SocketChannel socketChannel, byte[] data) {
		super(socketChannel);
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}
}
