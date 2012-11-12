package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;

import org.junit.Test;
import org.reunionemu.jreunion.protocol.packets.server.CharListEndPacket;
import org.reunionemu.jreunion.protocol.parsers.client.CharListEndParser;

public class CharListEndParserTest {

	@Test
	public void test() {
		CharListEndParser parser = new CharListEndParser();
		Pattern pattern = parser.getPattern();

		String msg = "chars_end 1 2";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof CharListEndPacket);
		assertEquals(msg, packet.toString());

		CharListEndPacket charListEndPacket = (CharListEndPacket) packet;
		assertEquals(1, charListEndPacket.getUnknown());
		assertEquals(2, charListEndPacket.getAccountId());

	}
}
