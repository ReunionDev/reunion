package netty.packets;

import netty.Packet;

public class CharDeletePacket implements Packet {

	private static final long serialVersionUID = 1L;

	public CharDeletePacket() {

	}

	public CharDeletePacket(int slot) {

		this.slot = slot;
	}

	int slot;

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("char_del ");
		builder.append(getSlot());
		return builder.toString();
	}

}
