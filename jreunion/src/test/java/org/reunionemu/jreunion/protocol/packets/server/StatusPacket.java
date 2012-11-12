package org.reunionemu.jreunion.protocol.packets.server;

import netty.Packet;

import org.reunionemu.jreunion.game.Player.Status;

public class StatusPacket implements Packet {

	private static final long serialVersionUID = 1L;

	Status statusType;

	long value;

	long max;

	public Status getStatusType() {
		return statusType;
	}

	public void setStatusType(Status statusType) {
		this.statusType = statusType;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("status ");

		builder.append(getStatusType().value());

		builder.append(' ');

		builder.append(getValue());

		builder.append(' ');

		builder.append(getMax());

		return builder.toString();
	}
}
