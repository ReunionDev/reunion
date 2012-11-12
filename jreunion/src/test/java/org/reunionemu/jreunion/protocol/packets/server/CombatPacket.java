package org.reunionemu.jreunion.protocol.packets.server;

import org.reunionemu.jreunion.protocol.Packet;

public class CombatPacket implements Packet {

	private static final long serialVersionUID = 1L;

	Long id;

	boolean inCombat;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
		builder.append(getId());
		builder.append(' ');		
		builder.append(isInCombat() ? 1 : 0);
		return builder.toString();
	}

}