package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;

import org.junit.Test;
import org.reunionemu.jreunion.protocol.packets.server.MessagePacket;
import org.reunionemu.jreunion.protocol.parsers.client.MessageParser;

public class MessageParserTest {

	@Test
	public void test() {
		MessageParser parser = new MessageParser();
		Pattern pattern = parser.getPattern();

		String msg = "this is a msg message!";
		String eventMsg = "msg " + msg;
		Matcher matcher = pattern.matcher(eventMsg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("msg").matches());
		assertFalse(pattern.matcher(msg).matches());
		assertFalse(pattern.matcher("msg" + msg).matches());

		Packet packet = parser.parse(matcher, eventMsg);
		assertNotNull(packet);
		assertTrue(packet instanceof MessagePacket);
		assertEquals(msg, ((MessagePacket) packet).getMessage());
		assertEquals(eventMsg, packet.toString());

	}

}
