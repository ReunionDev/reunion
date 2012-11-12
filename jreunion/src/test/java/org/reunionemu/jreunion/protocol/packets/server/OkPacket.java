package org.reunionemu.jreunion.protocol.packets.server;

import netty.Packet;

public class OkPacket implements Packet {

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "OK";
	}
}
