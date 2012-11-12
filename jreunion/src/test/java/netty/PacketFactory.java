package netty;

import org.reunionemu.jreunion.protocol.Packet;

public interface PacketFactory {

	String build(Packet msg);

}
