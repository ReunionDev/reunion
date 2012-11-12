package org.reunionemu.jreunion.protocol.packets.client;

import org.reunionemu.jreunion.protocol.Packet;

public class CombatPacket implements Packet {

	private static final long serialVersionUID = 1L;


	boolean inCombat;

	public boolean isInCombat() {
		return inCombat;
	}

	public void setInCombat(boolean inCombat) {
		this.inCombat = inCombat;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("combat ");
		builder.append(isInCombat() ? 1 : 0);
		return builder.toString();
	}

}
