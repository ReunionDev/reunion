package netty.packets;

import netty.Packet;

public class SkillUpPacket implements Packet {

	private static final long serialVersionUID = 1L;

	public SkillUpPacket() {

	}

	public SkillUpPacket(int id) {

		this.id = id;
	}

	int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("skillup ");
		builder.append(getId());
		return builder.toString();
	}

}
