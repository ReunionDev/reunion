package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;


import org.junit.Test;
import org.reunionemu.jreunion.protocol.Packet;
import org.reunionemu.jreunion.protocol.packets.server.OutPacket;
import org.reunionemu.jreunion.protocol.packets.server.OutPacket.EntityType;
import org.reunionemu.jreunion.protocol.parsers.client.OutParser;

public class OutParserTest {

	@Test
	public void test() {
		OutParser parser = new OutParser();
		Pattern pattern = parser.getPattern();

		String msg = "out item 10";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof OutPacket);

		assertEquals(msg, packet.toString());
		OutPacket outPacket = (OutPacket) packet;
		assertEquals(10, outPacket.getId());
		assertEquals(EntityType.ITEM, outPacket.getEntityType());

	}

}
