package org.reunionemu.jreunion.protocol.parsers.server.test;

import static org.junit.Assert.*;

import java.util.regex.*;


import org.junit.Test;
import org.reunionemu.jreunion.protocol.Packet;
import org.reunionemu.jreunion.protocol.packets.client.StartGamePacket;
import org.reunionemu.jreunion.protocol.parsers.server.StartGameParser;

public class StartGameParserTest {

	@Test
	public void test() {
		StartGameParser parser = new StartGameParser();
		Pattern pattern = parser.getPattern();

		String msg = "start_game";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof StartGamePacket);
		assertEquals(msg, packet.toString());

	}

}
