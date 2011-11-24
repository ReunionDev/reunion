package com.googlecode.reunion.jreunion.server;

import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

public interface Sendable {
	
	void sendPacket(Type packetType, Object...args);
	
}
