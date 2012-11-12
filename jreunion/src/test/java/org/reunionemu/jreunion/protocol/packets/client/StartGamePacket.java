package org.reunionemu.jreunion.protocol.packets.client;

import netty.Packet;

public class StartGamePacket implements Packet {

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "start_game";
	}
}
