package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;


import org.junit.Test;
import org.reunionemu.jreunion.protocol.Packet;
import org.reunionemu.jreunion.protocol.packets.server.SuccessPacket;
import org.reunionemu.jreunion.protocol.parsers.client.SuccessParser;

public class SuccessParserTest {

	@Test
	public void test() {
		SuccessParser parser = new SuccessParser();
		Pattern pattern = parser.getPattern();

		String msg = "success";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof SuccessPacket);
		assertEquals(msg, packet.toString());

	}

}
