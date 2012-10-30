package org.reunionemu.jreunion.events.network;

import java.nio.channels.SocketChannel;
/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class NetworkAcceptEvent extends NetworkEvent
{
	public NetworkAcceptEvent(SocketChannel socketChannel) {
		super(socketChannel);
	}

}
