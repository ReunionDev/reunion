package org.reunionemu.jreunion.protocol.packets.client;

import netty.Packet;

import org.reunionemu.jreunion.game.Player.Status;

public class LevelUpPacket implements Packet {

	private static final long serialVersionUID = 1L;

	public LevelUpPacket() {

	}

	public LevelUpPacket(Status statusType) {

		this.statusType = statusType;
	}

	Status statusType;

	public Status getStatusType() {
		return statusType;
	}

	public void setStatusType(Status statusType) {
		this.statusType = statusType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("levelup ");
		builder.append(getStatusType().value() - 10);
		return builder.toString();
	}

}
