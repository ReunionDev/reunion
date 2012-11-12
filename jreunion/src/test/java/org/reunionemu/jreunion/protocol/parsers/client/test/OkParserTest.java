package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;


import org.junit.Test;
import org.reunionemu.jreunion.protocol.Packet;
import org.reunionemu.jreunion.protocol.packets.server.OkPacket;
import org.reunionemu.jreunion.protocol.parsers.client.OkParser;

public class OkParserTest {

	@Test
	public void test() {
		OkParser parser = new OkParser();
		Pattern pattern = parser.getPattern();

		String msg = "OK";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof OkPacket);
		assertEquals(msg, packet.toString());

	}

}
