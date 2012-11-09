package netty.packets;

import netty.Packet;

public class PartyDisbandPacket implements Packet {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public String toString() {
		return "party disband";
	}
}
