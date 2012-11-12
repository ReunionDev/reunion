package org.reunionemu.jreunion.protocol.packets.server;

import netty.Packet;

public class GoToPacket implements Packet {

	private static final long serialVersionUID = 1L;

	int x;
	int y;
	int z;
	double rotation;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("goto ");
		builder.append(getX());
		builder.append(' ');
		builder.append(getY());
		builder.append(' ');
		builder.append(getZ());
		builder.append(' ');
		builder.append(getRotation());
		return builder.toString();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
}
