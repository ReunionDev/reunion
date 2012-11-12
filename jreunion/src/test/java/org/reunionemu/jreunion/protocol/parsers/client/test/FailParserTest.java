package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;


import org.junit.Test;
import org.reunionemu.jreunion.protocol.Packet;
import org.reunionemu.jreunion.protocol.packets.server.FailPacket;
import org.reunionemu.jreunion.protocol.parsers.client.FailParser;

public class FailParserTest {

	@Test
	public void test() {
		FailParser parser = new FailParser();
		Pattern pattern = parser.getPattern();

		String msg = "this is a fail message!";
		String failMsg = "fail " + msg;
		Matcher matcher = pattern.matcher(failMsg);
		assertTrue(matcher.matches());
		assertTrue(pattern.matcher("fail").matches());
		assertFalse(pattern.matcher(msg).matches());
		assertFalse(pattern.matcher("fail" + msg).matches());

		Packet packet = parser.parse(matcher, failMsg);
		assertNotNull(packet);
		assertTrue(packet instanceof FailPacket);
		assertEquals(msg, ((FailPacket) packet).getMessage());
		assertEquals(failMsg, packet.toString());

	}

}
