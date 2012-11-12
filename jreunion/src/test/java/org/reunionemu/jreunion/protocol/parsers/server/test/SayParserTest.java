package org.reunionemu.jreunion.protocol.parsers.server.test;

import static org.junit.Assert.*;

import java.util.regex.*;


import org.junit.Test;
import org.reunionemu.jreunion.protocol.Packet;
import org.reunionemu.jreunion.protocol.packets.client.SayPacket;
import org.reunionemu.jreunion.protocol.parsers.server.SayParser;

public class SayParserTest {

	@Test
	public void test() {
		SayParser parser = new SayParser();
		Pattern pattern = parser.getPattern();

		String msg = "this is a say message";
		String sayMsg = "say " +  msg;
		Matcher matcher = pattern.matcher(sayMsg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("say").matches());
		assertFalse(pattern.matcher("" + msg).matches());
		assertFalse(pattern.matcher("say" + msg).matches());

		Packet packet = parser.parse(matcher, sayMsg);
		assertNotNull(packet);
		assertTrue(packet instanceof SayPacket);
		assertEquals(msg, ((SayPacket) packet).getMessage());

	}

}
