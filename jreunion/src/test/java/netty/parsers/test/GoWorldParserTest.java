package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.GoWorldPacket;
import netty.parsers.GoWorldParser;

import org.junit.Test;

public class GoWorldParserTest {

	@Test
	public void test() {
		GoWorldParser parser = new GoWorldParser();
		Pattern pattern = parser.getPattern();

		String msg = "go_world 127.0.0.1 4005 4 1";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof GoWorldPacket);

		assertEquals(msg, packet.toString());
		GoWorldPacket outPacket = (GoWorldPacket) packet;
		assertEquals("127.0.0.1", outPacket.getAddress().getHostAddress());
		assertEquals(4005, outPacket.getPort());
		assertEquals(4, outPacket.getMapId());
		assertEquals(1, outPacket.getUnknown());

	}

}
