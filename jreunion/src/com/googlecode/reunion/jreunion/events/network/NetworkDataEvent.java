package com.googlecode.reunion.jreunion.events.network;

import java.net.Socket;
import java.nio.channels.SocketChannel;

import com.googlecode.reunion.jreunion.server.Client;
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
