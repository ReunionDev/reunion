package org.reunionemu.jreunion.protocol.parsers.server.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;

import org.junit.Test;
import org.reunionemu.jreunion.protocol.packets.client.StartPacket;
import org.reunionemu.jreunion.protocol.parsers.server.StartParser;

public class StartParserTest {

	@Test
	public void test() {
		StartParser parser = new StartParser();
		Pattern pattern = parser.getPattern();

		String msg = "start 1 2";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof StartPacket);
		assertEquals(msg, packet.toString());

		StartPacket newChar = (StartPacket) packet;
		assertEquals(1, newChar.getSlot());
		assertEquals(2, newChar.getZone());
	}

}
