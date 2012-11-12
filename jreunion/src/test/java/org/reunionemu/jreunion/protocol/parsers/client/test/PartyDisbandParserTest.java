package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;

import org.junit.Test;
import org.reunionemu.jreunion.protocol.packets.server.PartyDisbandPacket;
import org.reunionemu.jreunion.protocol.parsers.client.PartyDisbandParser;

public class PartyDisbandParserTest {

	@Test
	public void test() {
		PartyDisbandParser parser = new PartyDisbandParser();
		Pattern pattern = parser.getPattern();

		String msg = "party disband";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof PartyDisbandPacket);
		assertEquals(msg, packet.toString());

	}

}
