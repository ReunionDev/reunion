package org.reunionemu.jreunion.protocol.packets.client;

import org.reunionemu.jreunion.protocol.Packet;

public class SayPacket implements Packet {

	private static final long serialVersionUID = 1L;

	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public SayPacket() {

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("say ");
		builder.append(getMessage());
		return builder.toString();
	}

}
