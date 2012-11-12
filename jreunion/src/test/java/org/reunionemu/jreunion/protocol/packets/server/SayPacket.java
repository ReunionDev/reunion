package org.reunionemu.jreunion.protocol.packets.server;

import org.reunionemu.jreunion.protocol.Packet;

public class SayPacket implements Packet {

	private static final long serialVersionUID = 1L;

	int id;

	String message;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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
		builder.append(getId());
		builder.append(' ');
		builder.append(getMessage());
		return builder.toString();
	}

}
