package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;


import org.junit.Test;
import org.reunionemu.jreunion.protocol.Packet;
import org.reunionemu.jreunion.protocol.packets.server.InfoPacket;
import org.reunionemu.jreunion.protocol.parsers.client.InfoParser;

public class InfoParserTest {

	@Test
	public void test() {
		InfoParser parser = new InfoParser();
		Pattern pattern = parser.getPattern();

		String msg = "this is an info message!";
		String infoMsg = "info " + msg;
		Matcher matcher = pattern.matcher(infoMsg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("infp").matches());
		assertFalse(pattern.matcher(msg).matches());
		assertFalse(pattern.matcher("info" + msg).matches());

		Packet packet = parser.parse(matcher, infoMsg);
		assertNotNull(packet);
		assertTrue(packet instanceof InfoPacket);
		assertEquals(msg, ((InfoPacket) packet).getMessage());
		assertEquals(infoMsg, packet.toString());

	}

}
