package org.reunionemu.jreunion.protocol.parsers.server.test;

import static org.junit.Assert.*;

import java.util.regex.*;


import org.junit.Test;
import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.Player.Sex;
import org.reunionemu.jreunion.protocol.Packet;
import org.reunionemu.jreunion.protocol.packets.client.CharNewPacket;
import org.reunionemu.jreunion.protocol.parsers.server.CharNewParser;

public class CharNewParserTest {

	@Test
	public void test() {
		CharNewParser parser = new CharNewParser();
		Pattern pattern = parser.getPattern();

		String msg = "char_new 1 myname 2 1 4 5 6 7 8 9";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof CharNewPacket);
		assertEquals(msg, packet.toString());

		CharNewPacket newChar = (CharNewPacket) packet;
		assertEquals(1, newChar.getSlot());
		assertEquals("myname", newChar.getName());
		assertEquals(Race.byValue(2), newChar.getRace());
		assertEquals(Sex.byValue(1), newChar.getSex());
		assertEquals(4, newChar.getHair());
		assertEquals(5, newChar.getStrength());
		assertEquals(6, newChar.getIntellect());
		assertEquals(7, newChar.getDexterity());
		assertEquals(8, newChar.getConstitution());
		assertEquals(9, newChar.getLeadership());

	}

}
