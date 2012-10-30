package org.reunionemu.jreunion.events.network;

import java.nio.channels.SocketChannel;
/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
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
