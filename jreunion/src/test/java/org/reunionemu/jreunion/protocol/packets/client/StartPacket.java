package org.reunionemu.jreunion.protocol.packets.client;

import netty.Packet;

public class StartPacket implements Packet {

	private static final long serialVersionUID = 1L;

	int slot;

	int zone;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("start ");
		builder.append(getSlot());
		builder.append(' ');
		builder.append(getZone());
		return builder.toString();
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public int getZone() {
		return zone;
	}

	public void setZone(int zone) {
		this.zone = zone;
	}

}
