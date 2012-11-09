package netty.packets;

import netty.Packet;

public class CharExistPacket implements Packet {

	private static final long serialVersionUID = 1L;

	public CharExistPacket() {

	}

	public CharExistPacket(String name) {

		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	String name;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("char_exist ");
		builder.append(getName());
		return builder.toString();
	}
}
