package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;

import org.junit.Test;
import org.reunionemu.jreunion.protocol.packets.server.EventPacket;
import org.reunionemu.jreunion.protocol.parsers.client.EventParser;

public class EventParserTest {

	@Test
	public void test() {
		EventParser parser = new EventParser();
		Pattern pattern = parser.getPattern();

		String msg = "this is an info message!";
		String eventMsg = "event " + msg;
		Matcher matcher = pattern.matcher(eventMsg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("event").matches());
		assertFalse(pattern.matcher(msg).matches());
		assertFalse(pattern.matcher("event" + msg).matches());

		Packet packet = parser.parse(matcher, eventMsg);
		assertNotNull(packet);
		assertTrue(packet instanceof EventPacket);
		assertEquals(msg, ((EventPacket) packet).getMessage());
		assertEquals(eventMsg, packet.toString());

	}

}
