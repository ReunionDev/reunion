package org.reunionemu.jreunion.protocol.parsers.server.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;

import org.junit.Test;
import org.reunionemu.jreunion.protocol.packets.client.*;
import org.reunionemu.jreunion.protocol.parsers.server.RevivalParser;

public class RevivalParserTest {

	@Test
	public void test() {
		RevivalParser parser = new RevivalParser();
		Pattern pattern = parser.getPattern();

		String msg = "revival";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof RevivalPacket);
		assertEquals(msg, packet.toString());

	}
}
