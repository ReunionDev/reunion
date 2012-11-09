package netty.packets;

import java.net.Inet4Address;

import netty.Packet;

public class GoWorldPacket implements Packet {

	private static final long serialVersionUID = 1L;

	Inet4Address address;

	int port;

	int mapId;

	int unknown;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("go_world ");
		builder.append(getAddress().getHostAddress());
		builder.append(' ');
		builder.append(getPort());
		builder.append(' ');
		builder.append(getMapId());
		builder.append(' ');
		builder.append(getUnknown());
		return builder.toString();
	}

	public Inet4Address getAddress() {
		return address;
	}

	public void setAddress(Inet4Address address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public int getUnknown() {
		return unknown;
	}

	public void setUnknown(int unknown) {
		this.unknown = unknown;
	}

}
