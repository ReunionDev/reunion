package org.reunionemu.jreunion.server;

import org.reunionemu.jreunion.server.PacketFactory.Type;

public interface Sendable {
	
	void sendPacket(Type packetType, Object...args);
	
}
