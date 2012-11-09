package netty.packets;

import netty.Packet;

public class FailPacket implements Packet {

	private static final long serialVersionUID = 1L;

	public FailPacket() {

	}

	public FailPacket(String message) {

		this.message = message;
	}

	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("fail");
		String message = getMessage();
		if (message != null) {
			builder.append(' ');
			builder.append(message);
		}
		return builder.toString();
	}
}
