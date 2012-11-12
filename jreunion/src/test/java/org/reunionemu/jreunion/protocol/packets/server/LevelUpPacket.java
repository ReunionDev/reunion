package org.reunionemu.jreunion.protocol.packets.server;

import org.reunionemu.jreunion.protocol.Packet;

public class LevelUpPacket implements Packet {

	private static final long serialVersionUID = 1L;

	long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LevelUpPacket() {

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("levelup ");
		builder.append(getId());
		return builder.toString();
	}

}
