package netty.packets;

import netty.Packet;

public class EventPacket implements Packet {

	private static final long serialVersionUID = 1L;

	String message;

	public EventPacket() {

	}

	public EventPacket(String message) {

		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("event ");
		builder.append(getMessage());
		return builder.toString();
	}
}
