package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;

import org.junit.Test;
import org.reunionemu.jreunion.protocol.packets.server.CombatPacket;
import org.reunionemu.jreunion.protocol.parsers.server.CombatParser;

public class CombatParserTest {

	@Test
	public void test() {
		CombatParser parser = new CombatParser();
		Pattern pattern = parser.getPattern();

		String msg = "combat 2 1";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof CombatPacket);
		assertEquals(msg, packet.toString());

		CombatPacket newChar = (CombatPacket) packet;
		assertEquals(2, (long) newChar.getId());
		assertTrue(newChar.isInCombat());

		msg = "combat 0";
		matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof CombatPacket);
		assertEquals(msg, packet.toString());

		newChar = (CombatPacket) packet;
		assertNull(newChar.getId());

		assertFalse(newChar.isInCombat());

	}

}
