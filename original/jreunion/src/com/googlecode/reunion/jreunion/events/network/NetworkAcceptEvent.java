package com.googlecode.reunion.jreunion.events.network;

import java.nio.channels.SocketChannel;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class NetworkAcceptEvent extends NetworkEvent
{
	public NetworkAcceptEvent(SocketChannel socketChannel) {
		super(socketChannel);
	}

}
