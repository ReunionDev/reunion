package org.reunionemu.jreunion.protocol.packets.server;

import org.reunionemu.jreunion.protocol.Packet;

public class SkyPacket implements Packet {

	private static final long serialVersionUID = 1L;

	int id;

	boolean flyStatus;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isFlyStatus() {
		return flyStatus;
	}

	public void setFlyStatus(boolean flyStatus) {
		this.flyStatus = flyStatus;
	}

	public SkyPacket() {

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("sky ");
		builder.append(getId());
		builder.append(' ');
		builder.append(isFlyStatus() ? 1 : 0);
		return builder.toString();
	}

}
