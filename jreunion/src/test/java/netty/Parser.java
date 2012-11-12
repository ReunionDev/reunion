package netty;

import org.reunionemu.jreunion.protocol.Packet;

public interface Parser {
	Packet parse(String input);
}
