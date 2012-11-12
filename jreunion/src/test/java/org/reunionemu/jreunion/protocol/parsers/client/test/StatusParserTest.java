package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;

import org.junit.Test;
import org.reunionemu.jreunion.game.Player.Status;
import org.reunionemu.jreunion.protocol.packets.server.StatusPacket;
import org.reunionemu.jreunion.protocol.parsers.client.StatusParser;

public class StatusParserTest {

	@Test
	public void test() {
		StatusParser parser = new StatusParser();
		Pattern pattern = parser.getPattern();

		String msg = "status 1 2 3";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof StatusPacket);
		assertEquals(msg, packet.toString());

		StatusPacket charListEndPacket = (StatusPacket) packet;
		assertEquals(Status.byValue(1), charListEndPacket.getStatusType());
		assertEquals(2, charListEndPacket.getValue());
		assertEquals(3, charListEndPacket.getMax());

	}
}
