package netty.packets;

import netty.Packet;

public class HourPacket implements Packet {

	private static final long serialVersionUID = 1L;

	int hour;

	public HourPacket() {

	}

	public HourPacket(int hour) {

		this.hour = hour;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("hour ");
		builder.append(getHour());
		return builder.toString();
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}
}
