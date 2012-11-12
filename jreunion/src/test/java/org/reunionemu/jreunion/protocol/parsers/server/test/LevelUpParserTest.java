package org.reunionemu.jreunion.protocol.parsers.server.test;

import static org.junit.Assert.*;

import java.util.regex.*;


import org.junit.Test;
import org.reunionemu.jreunion.game.Player.Status;
import org.reunionemu.jreunion.protocol.Packet;
import org.reunionemu.jreunion.protocol.packets.client.LevelUpPacket;
import org.reunionemu.jreunion.protocol.parsers.server.LevelUpParser;

public class LevelUpParserTest {

	@Test
	public void test() {
		LevelUpParser parser = new LevelUpParser();
		Pattern pattern = parser.getPattern();

		int statusId = Status.DEXTERITY.value() - 10;
		String msg = "levelup " + statusId;
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("levelup" + statusId).matches());
		assertFalse(pattern.matcher("levelup").matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof LevelUpPacket);
		assertEquals(Status.DEXTERITY, ((LevelUpPacket) packet).getStatusType());
		assertEquals(msg, packet.toString());

	}

}
